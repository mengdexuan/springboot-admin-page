package com.boot.biz.webshell.controller;

import ch.ethz.ssh2.SFTPException;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.json.JSONUtil;
import com.boot.base.util.ConcurrentLRUCache;
import com.boot.base.util.HelpMe;
import com.boot.biz.webshell.entity.SshServerInfo;
import com.boot.biz.webshell.service.ServerInfoService;
import com.boot.biz.webshell.sftp.CommandBean;
import com.boot.biz.webshell.sftp.SftpBean;
import com.boot.biz.webshell.sftp.SftpClient;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author mengdexuan on 2019/11/27 10:14.
 */
@Controller
@Slf4j
public class SftpController {

	private static ConcurrentLRUCache<String, SftpClient> sftpClientMap = new ConcurrentLRUCache<>(20);


	@Autowired
	ServerInfoService serverInfoService;

	@GetMapping("/sftp")
	public String sftp(Model model, String id) {

		log.info("进入 sftp 页面...");

		SshServerInfo info = new SshServerInfo();
		info.setId(id);
		info.setType(1);

		SshServerInfo one = serverInfoService.one(info);

		model.addAttribute("sshInfo",one);

		return "ssh/sftp";
	}

	@GetMapping("/closeSftpClient")
	@ResponseBody
	public String closeSftpClient(String id) {

		SshServerInfo info = new SshServerInfo();
		info.setId(id);
		info.setType(1);

		SshServerInfo one = serverInfoService.one(info);

		SftpClient sftpClient = sftpClientMap.remove(id);

		log.info("关闭 {} 的SftpClient连接。",one.getHostname());

		sftpClient.close();


		return "ok";
	}


	@PostMapping("/batchDelSftpFile")
	@ResponseBody
	public String batchDelSftpFile(String id,String splitStr,String fileNameStr) throws Exception{

		SshServerInfo info = new SshServerInfo();
		info.setId(id);
		info.setType(1);

		SshServerInfo sshServerInfo = serverInfoService.one(info);

		SftpClient sftpClient = sftpClientMap.get(id);

		List<String> fileNameList = HelpMe.easySplit(fileNameStr, splitStr.charAt(0));

		String hostName = sshServerInfo.getHostname();
		String pwd = sftpClient.getCurrentCatalog();
		if (!pwd.equals("/")){
			pwd += "/";
		}

		for (String temp:fileNameList){
			List<String> tempList = HelpMe.easySplit(temp);

			String fileName = tempList.get(0);
			boolean isDir = Boolean.parseBoolean(tempList.get(1));

			if (isDir){
				sftpClient.deleteDir(fileName);
			}else {
				sftpClient.deleteFile(fileName);
			}

			log.info("删除SFTP服务器 {} 上的文件(夹)：{}",hostName,pwd+fileName);
		}

		return "true";
	}


	@GetMapping("/connectSftp")
	@ResponseBody
	public Object connectSftp(String id) {

		SshServerInfo info = new SshServerInfo();
		info.setId(id);
		info.setType(1);

		SshServerInfo sshServerInfo = serverInfoService.one(info);

		SftpClient sftpClient = sftpClientMap.get(id);
		if (sftpClient == null) {
			sftpClient = new SftpClient();
			boolean flag = sftpClient.connect(sshServerInfo);
			if (flag) {
				sftpClientMap.put(id, sftpClient);
			} else {
				log.info("创建sftp连接失败！");
			}
		}

		try {
			SftpBean sftpBean = sftpClient.ls();
			return sftpBean;
		} catch (IOException e) {
			log.error("错误！", e);
			sftpClient.close();
			sftpClientMap.remove(id);
		}

		String msg = "sftp未连接！";
		log.info(msg);
		HashMap<Object, Object> map = Maps.newHashMap();

		map.put("code", -1);
		map.put("msg", msg);

		return map;
	}


	@PostMapping("/execCommand")
	@ResponseBody
	public Object execCommand(String id, CommandBean commandBean, HttpServletRequest request) throws IOException {

		SftpClient sftp = sftpClientMap.get(id);

		Map<String,Object> errMap = Maps.newHashMap();

		if (sftp != null && sftp.isConnected()) {

			try {

				switch (commandBean.getCmd()) {
					case "cd":
						sftp.changeDirectory(commandBean.getCmdParam());
						break;
					case "rm":
						SshServerInfo info = new SshServerInfo();
						info.setId(id);
						info.setType(1);
						SshServerInfo sshServerInfo = serverInfoService.one(info);

						String hostName = sshServerInfo.getHostname();
						String pwd = sftp.getCurrentCatalog();
						String fileNameStr = commandBean.getCmdParam();
						if (!pwd.equals("/")){
							pwd += "/";
						}
						if (commandBean.getIsDir()){
							sftp.deleteDir(fileNameStr);
						}else {
							sftp.deleteFile(fileNameStr);
						}
						log.info("删除SFTP服务器 {} 上的文件(夹)：{}",hostName,pwd+fileNameStr);
						break;
					case "mkdir":
						sftp.mkDir(commandBean.getCmdParam());
						break;
					case "upload":
						MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
						Iterator<String> fns = mRequest.getFileNames();
						InputStream inputStream = null;
						long totalSize = 0;
						String fileName = "";

						while (fns.hasNext()) {
							String fn = fns.next();
							List<MultipartFile> file = mRequest.getFiles(fn);
							for (MultipartFile multipartFile : file) {
								fileName = multipartFile.getOriginalFilename();
								totalSize = multipartFile.getSize();
								inputStream = multipartFile.getInputStream();
								break;
							}
							break;
						}

						sftp.uploadFile(inputStream, totalSize,fileName);
						break;
					case "attr":
						sftp.setAttributes(commandBean.getFileFileName(), Integer.valueOf("" + commandBean.getPermissions(), 8));
						break;
				}

				SftpBean sftpBean = sftp.ls();

				return JSONUtil.toJsonStr(sftpBean);
			} catch (SFTPException ex) {
				log.error("操作失败！",ex);
				errMap.put("code",-1);
				errMap.put("msg","权限不够，操作失败！");
			} catch (IOException e) {
				errMap.put("code",-1);
				errMap.put("msg","内部异常！");
			}

		} else {
			sftpClientMap.remove(id);

			errMap.put("code",-1);
			errMap.put("msg","sftp连接关闭！");
		}

		return JSONUtil.toJsonStr(errMap);
	}




	@PostMapping("/downloadFile")
	@ResponseBody
	public void downloadFile(String id, CommandBean commandBean, HttpServletResponse response) throws UnsupportedEncodingException {

		SftpClient sftp = sftpClientMap.get(id);

		response.setHeader("Content-Disposition","attachment;fileName="
				+ java.net.URLEncoder.encode(commandBean.getFileFileName(), "UTF-8"));
		response.setCharacterEncoding("utf-8");

		try {
			sftp.downloadFile(response.getOutputStream(),commandBean.getFileFileName());
		} catch (IOException e) {
			log.error("错误！",e);
		}

	}



	@GetMapping("/uploadState")
	@ResponseBody
	public String uploadState(String id) throws UnsupportedEncodingException {

		SftpClient sftpClient = sftpClientMap.get(id);

		Map<String,Object> progressMap = sftpClient.getProgressMap();

		String state = (String) progressMap.get("progress");

		if (state != null) {
			if (state.indexOf("100") != -1) {
				progressMap.remove("progress");
			}
		} else {
			return "{\"percent\":\"0.00%\",\"num\":\"0\"}";
		}

		return state;
	}







}
