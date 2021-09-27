package com.boot.biz.tio.common;


import org.tio.core.intf.Packet;

/**
 * @author mengdexuan on 2021/9/22 16:39.
 */
public class HelloPacket extends Packet {

	private static final long serialVersionUID = -172060606924066412L;
	public static final int HEADER_LENGHT = 4;//消息头的长度
	public static final String CHARSET = "utf-8";
	private byte[] body;
	/**
	 * @return the body
	 */
	public byte[] getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(byte[] body) {
		this.body = body;
	}
}
