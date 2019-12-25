package com.boot.biz.webshell.ssh;

import com.boot.base.util.HelpMe;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.InputStream;

public class ShellOutPutTask extends Thread{
	
	private final WebSocketSession session;
	private final InputStream out;

	//定义一个flag,来停止线程用
	private boolean isStop = false;

	//停止线程
	public void stopThread() {
		this.isStop = true;
	}

	public ShellOutPutTask(WebSocketSession session, InputStream out) {
		super();
		this.session = session;
		this.out = out;
	}

	@Override
	public void run() {
		super.run();
		
		byte[] buff = new byte[8192];
		StringBuilder sb = new StringBuilder();
		try
		{
			while (!isStop && session !=null && session.isOpen())
			{
				sb.setLength(0);
				int len = out.read(buff);
				if (len == -1)
					return;
				for (int i = 0; i < len; i++)
				{
					char c = (char) (buff[i] & 0xff);
					sb.append(c);
				}
				if (HelpMe.getEncoding(sb.toString()).equals("ISO-8859-1"))
					session.sendMessage(new TextMessage(new String(sb.toString().getBytes("ISO-8859-1"),"UTF-8")));
				else
					session.sendMessage(new TextMessage(new String(sb.toString().getBytes("gb2312"),"UTF-8")));
			}
		}
		catch (Exception e)
		{
		}
	}
	
	
}
