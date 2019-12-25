package com.boot.biz.webshell.sftp;

import ch.ethz.ssh2.*;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.boot.biz.webshell.entity.SshServerInfo;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

@Slf4j
@Data
public class SftpClient {
	
	private Connection conn;
	private SFTPv3Client client;
	private Stack<String> catalogs = new Stack<>();
	private String currentCatalog;
	private Map<String,Object> progressMap = Maps.newHashMap();

	public SftpClient() {
	}
	
	public SftpClient(SshServerInfo sshServerInfo) {
		super();
		connect(sshServerInfo);
	}


	public boolean connect(SshServerInfo sshServerInfo) {
		try {
			conn = new Connection(sshServerInfo.getHostname(),sshServerInfo.getPort());
			conn.connect();
			if (!conn.authenticateWithPassword(sshServerInfo.getUsername(),sshServerInfo.getPassword()))
				return false;
			client = new SFTPv3Client(conn);
			
			//init the current catalogs
			initCatalogs(client.canonicalPath("."));
		} catch (IOException e) {
			log.error("创建sftpClient失败！",e);
			return false;
		}
		return true;
	}
	
	public boolean isConnected() {
		if (client != null)
			return client.isConnected();
		return false;
	}
	
	private void initCatalogs(String str) {
		
		currentCatalog = str;
		catalogs.push("/");
		
		String[] ss = str.split("/");
		
		for (String s : ss) {
			if (!"".equals(s))
				catalogs.push(s);
		}
	}
	
	public String getCurrentCatalog() {
		return currentCatalog;
	}
	
	public void changeDirectory(String dirName) {
		
		//if param is a absolute path
		if ("/".equals(dirName.substring(0, 1))) {
			catalogs.clear();
			initCatalogs(dirName);
			currentCatalog = dirName;
			return ;
		}
		
		//parent directory
		if ("..".equals(dirName) && catalogs.size() > 1) {
			//update the catalogs stack
			catalogs.pop();
		} 
		//child directory
		else {
			catalogs.push(dirName);
		}
		updateCurrentCatalog();
	}
	
	private void updateCurrentCatalog() {
		String path = "";
		for (String s : catalogs) {
			if (!"/".equals(s))
				path += "/" + s;
		}
		if (catalogs.size() == 1)
			path = "/";
		currentCatalog = path;
	}
	
	public SftpBean ls() throws IOException {
		
		List<SFTPv3DirectoryEntry> list =  client.ls(currentCatalog);
		List<SftpFileBean> sftpFiles = new ArrayList<>();

		Integer fileCount = 0;
		Integer dirCount = 0;

		for (SFTPv3DirectoryEntry entry : list) {
			if (!entry.filename.equals("..") && !entry.filename.equals(".")) {
				SftpFileBean sfile = new SftpFileBean();
				sfile.setFilename(entry.filename);
				sfile.setDirectory(entry.attributes.isDirectory());
				sfile.setIntPermissions(entry.attributes.permissions);
				sfile.setStrPermissions(getStringPermission(entry.attributes.permissions));
				sfile.setOctalPermissions(entry.attributes.getOctalPermissions());
				String timeStr = DateUtil.format(new Date((long) entry.attributes.mtime * 1000),
						"yyyy-MM-dd HH:mm:ss");
				sfile.setMtime(timeStr);
				sfile.setSize(FileUtil.readableFileSize(entry.attributes.size));
				sftpFiles.add(sfile);

				boolean isDir = entry.attributes.isDirectory();
				if (isDir){
					dirCount ++;
				}else {
					fileCount ++;
				}

			}
		}
		
		Collections.sort(sftpFiles, new Comparator<SftpFileBean>() {
			@Override
			public int compare(SftpFileBean o1, SftpFileBean o2) {
				return o1.compareTo(o2);
			}
		});

		SftpBean sftpBean =  new SftpBean(getCurrentCatalog(), sftpFiles);
		sftpBean.setFileCount(fileCount);
		sftpBean.setDirCount(dirCount);

		return sftpBean;
	}
	
	private String getStringPermission(Integer p) {
		String temp[] = new String[] {"---", "--x", "-w-", "-wx", "r--", "r-x", "rw-", "rwx"};
		//is a directory ?
		return (p / 8 / 8 / 8 / 8 % 8 == 04 ? "d" : "-") + 
				temp[p / 8 / 8 % 8] + temp[p / 8 % 8] + temp[p % 8];
	}
	
	//upload file to current catalog
	public void uploadFile(InputStream inputStream, long totalSize,String fileName) throws IOException {

		if (inputStream == null)
			return ;

		byte[] b = new byte[1024*8];
		long count = 0;

		String remoteDir = getCurrentCatalog() + "/" + fileName;

		log.info("上传文件：{}",remoteDir);

		SFTPv3FileHandle handle = client.createFile(remoteDir);
		DecimalFormat df = new DecimalFormat("#.00");
		
		while (true) {
			
			int len = inputStream.read(b);
			if (len == -1)
				break;
			client.write(handle, count, b, 0, len);
			count += len;

			progressMap.put("progress", "{\"percent\":\""+df.format((double)count / totalSize * 100)+"%\",\"num\":\""+(int)((double)count / totalSize)+"\"}");

		}
		client.closeFile(handle);
		inputStream.close();
	}
	
	//download file from current catalog
	public void downloadFile(OutputStream outputStream,String fileName) throws IOException {
		
		log.info("下载文件："+getCurrentCatalog() + "/" + fileName);

		SFTPv3FileHandle handle = client.openFileRO(getCurrentCatalog() + "/" + fileName);
		
		long count = 0;
		byte[] buff = new byte[1024*8];
		
		while (true) {
			int len = client.read(handle, count, buff, 0, 1024*8);
			if (len == -1)
				break;
			outputStream.write(buff, 0, len);
			count += len;
		}

		client.closeFile(handle);
	}
	
	public void deleteFile(String fileName) throws IOException {
		client.rm(getCurrentCatalog() + "/" + fileName);
	}

	public void deleteDir(String dirName) throws IOException {
		client.rmdir(getCurrentCatalog() + "/" + dirName);
	}
	
	public void mkDir(String dirName) throws IOException {
		client.mkdir(getCurrentCatalog() + "/" + dirName, 0755);
	}
	
	public void createFile(String fileName) throws IOException {
		client.createFileTruncate(getCurrentCatalog() + "/" + fileName);
	}
	
	public void setAttributes(String fileName, Integer permissions) throws IOException {
		
		SFTPv3FileAttributes attr = client.stat(getCurrentCatalog() + "/" + fileName);
		
		SFTPv3FileAttributes attr1 = new SFTPv3FileAttributes();
		attr1.permissions = attr.permissions / 8 / 8 / 8 * 8 * 8 * 8 + permissions;
		client.setstat(getCurrentCatalog() + "/" + fileName, attr1);
	}

	public void close(){
		try {
			client.close();
		} catch (Exception e) {
		}

		try {
			conn.close();
		} catch (Exception e) {
		}
	}
}
