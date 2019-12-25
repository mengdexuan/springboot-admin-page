package com.boot.biz.webshell.ssh;

import ch.ethz.ssh2.Connection;
import com.boot.biz.webshell.entity.SshServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;

@Slf4j
public class SshClient {
	
	private Connection conn;
	private ch.ethz.ssh2.Session sess;
	private InputStream in;
	private OutputStream out;
	private BufferedWriter inToShell;
	private ShellOutPutTask writeThread;

	public boolean connect(SshServerInfo sshServerInfo) {
		try {
			conn = new Connection(sshServerInfo.getHostname(), sshServerInfo.getPort());
			conn.connect();
			if (!conn.authenticateWithPassword(sshServerInfo.getUsername(),sshServerInfo.getPassword())){
				log.info("创建连接失败！");
				return false;
			}
			sess = conn.openSession();
			sess.requestPTY("xterm", 90, 30, 0, 0, null);
			sess.startShell();
			in = sess.getStdout();
			out = sess.getStdin();
			inToShell = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 写命令到服务
	 * @param text
	 * @throws IOException
	 */
	public void write(String text) throws IOException {
		if (inToShell != null) {
			//写命令到服务器
			inToShell.write(text);
			//刷到服务器上
			inToShell.flush();
		}
	}
	
	public void startShellOutPutTask(WebSocketSession session) {
		writeThread = new ShellOutPutTask(session, in);
		writeThread.start();
	}
	
	public void disconnect() {
		if (conn != null){
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		if (sess != null){
			try {
				sess.close();
			} catch (Exception e) {
			}
		}
		writeThread.stopThread();
		conn = null;
		sess = null;
	}
}
