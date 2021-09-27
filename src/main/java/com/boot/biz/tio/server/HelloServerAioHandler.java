package com.boot.biz.tio.server;

import com.boot.biz.tio.common.HelloPacket;
import lombok.extern.slf4j.Slf4j;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.TioConfig;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioHandler;

import java.nio.ByteBuffer;

/**
 * @author mengdexuan on 2021/9/22 16:42.
 */
@Slf4j
public class HelloServerAioHandler implements ServerAioHandler {

	/**
	 * 解码：把接收到的ByteBuffer，解码成应用可以识别的业务消息包
	 * 总的消息结构：消息头 + 消息体
	 * 消息头结构：    4个字节，存储消息体的长度
	 * 消息体结构：   对象的json串的byte[]
	 */
	@Override
	public HelloPacket decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {
		//提醒：buffer的开始位置并不一定是0，应用需要从buffer.position()开始读取数据
		//收到的数据组不了业务包，则返回null以告诉框架数据不够
		if (readableLength < HelloPacket.HEADER_LENGHT) {
			return null;
		}
		//读取消息体的长度
		int bodyLength = buffer.getInt();
		//数据不正确，则抛出AioDecodeException异常
		if (bodyLength < 0) {
			throw new AioDecodeException("bodyLength [" + bodyLength + "] is not right, remote:" + channelContext.getClientNode());
		}
		//计算本次需要的数据长度
		int neededLength = HelloPacket.HEADER_LENGHT + bodyLength;
		//收到的数据是否足够组包
		int isDataEnough = readableLength - neededLength;
		// 不够消息体长度(剩下的buffe组不了消息体)
		if (isDataEnough < 0) {
			return null;
		} else //组包成功
		{
			HelloPacket imPacket = new HelloPacket();
			if (bodyLength > 0) {
				byte[] dst = new byte[bodyLength];
				buffer.get(dst);
				imPacket.setBody(dst);
			}
			return imPacket;
		}
	}
	/**
	 * 编码：把业务消息包编码为可以发送的ByteBuffer
	 * 总的消息结构：消息头 + 消息体
	 * 消息头结构：    4个字节，存储消息体的长度
	 * 消息体结构：   对象的json串的byte[]
	 */
	@Override
	public ByteBuffer encode(Packet packet, TioConfig tioConfig, ChannelContext channelContext) {
		HelloPacket helloPacket = (HelloPacket) packet;
		byte[] body = helloPacket.getBody();
		int bodyLen = 0;
		if (body != null) {
			bodyLen = body.length;
		}
		//bytebuffer的总长度是 = 消息头的长度 + 消息体的长度
		int allLen = HelloPacket.HEADER_LENGHT + bodyLen;
		//创建一个新的bytebuffer
		ByteBuffer buffer = ByteBuffer.allocate(allLen);
		//设置字节序
		buffer.order(tioConfig.getByteOrder());
		//写入消息头----消息头的内容就是消息体的长度
		buffer.putInt(bodyLen);
		//写入消息体
		if (body != null) {
			buffer.put(body);
		}
		return buffer;
	}
	/**
	 * 处理消息
	 */
	public static ChannelContext context = null;

	@Override
	public void handler(Packet packet, ChannelContext channelContext) throws Exception {
		context = channelContext;
		HelloPacket helloPacket = (HelloPacket) packet;
		byte[] body = helloPacket.getBody();
		if (body != null) {
			String str = new String(body, HelloPacket.CHARSET);
			log.info("收到消息：" + str);
			HelloPacket resppacket = new HelloPacket();
			resppacket.setBody(("收到了你的消息，你的消息是:" + str).getBytes(HelloPacket.CHARSET));
			Tio.send(channelContext, resppacket);
		}
		return;
	}

	public static void send(String msg){
		HelloPacket packet = new HelloPacket();
		try {
			packet.setBody((msg).getBytes(HelloPacket.CHARSET));
			Tio.send(context, packet);
		} catch (Exception e) {
			log.error("错误",e);
		}
	}

}
