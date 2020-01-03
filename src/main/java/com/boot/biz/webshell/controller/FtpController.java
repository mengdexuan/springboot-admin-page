package com.boot.biz.webshell.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpMode;
import com.boot.base.util.ConcurrentLRUCache;
import com.boot.base.util.HelpMe;
import com.boot.biz.webshell.entity.SshServerInfo;
import com.boot.biz.webshell.ftp.FtpBean;
import com.boot.biz.webshell.ftp.FtpFile;
import com.boot.biz.webshell.service.ServerInfoService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author mengdexuan on 2019/11/29 19:16.
 */
@Controller
@Slf4j
public class FtpController {


	private static ConcurrentLRUCache<String, Ftp> ftpClientMap = new ConcurrentLRUCache<>(20);
	private static ConcurrentLRUCache<String, String> uploadProgressMap = new ConcurrentLRUCache<>(5);


	@Autowired
	ServerInfoService serverInfoService;

	@Autowired
	Executor executor;


	@RequestMapping("/ftpServerList")
	public String sshServerList(Model model) {

		log.info("ftpServerList页面");

		SshServerInfo info = new SshServerInfo();
		info.setType(2);

		List<SshServerInfo> infoList = serverInfoService.list(info);

		infoList = infoList.stream().sorted(Comparator.comparing(SshServerInfo::getCreateTime)).collect(Collectors.toList());

		model.addAttribute("infoList",infoList);

		return "ssh/ftpServerList";
	}


	@GetMapping("/ftpPage")
	public String ftpPage(Model model,String id,String dir,Boolean parentDir) {

		SshServerInfo info = new SshServerInfo();
		info.setType(2);
		info.setId(id);

		SshServerInfo ftpInfo = serverInfoService.one(info);

		List<FtpFile> ftpFileList = Lists.newArrayList();

		Ftp ftp = ftpClientMap.get(id);

		if (ftp==null){
			ftp = connect(ftpInfo);
			ftpClientMap.put(id,ftp);
		}

		Integer dirCount = 0;
		Integer fileCount = 0;

		FtpBean ftpBean = new FtpBean();

		try {
			if (HelpMe.isNotNull(dir)){
				ftp.cd(dir);
			}

			if (parentDir!=null&&parentDir){
				ftp.toParent();
			}

			FTPFile[] ftpFileArr = ftp.lsFiles(ftp.pwd());
			for (FTPFile ftpFile:ftpFileArr){
				FtpFile one = new FtpFile();
				one.setName(ftpFile.getName());
				one.setDir(ftpFile.isDirectory());
				one.setSize(FileUtil.readableFileSize(ftpFile.getSize()));
				one.setDate(ftpFile.getTimestamp().getTime());

				if (ftpFile.isDirectory()){
					dirCount ++;
				}else {
					fileCount ++;
				}

				ftpFileList.add(one);
			}

			ftpBean.setCurrDir(ftp.pwd());
			ftpBean.setFtpFileList(ftpFileList);
			ftpBean.setDirCount(dirCount);
			ftpBean.setFileCount(fileCount);
			if (ftp.pwd().equals("/")){
				ftpBean.setShowFirstRow(false);
			}else {
				ftpBean.setShowFirstRow(true);
			}
		}catch (Exception e){
			close(id);
		}



		model.addAttribute("ftpBean",ftpBean);
		model.addAttribute("ftpInfo",ftpInfo);

		return "ssh/ftp";
	}



	@GetMapping("/closeFtpClient")
	@ResponseBody
	public String closeFtp(String id) {

		close(id);

		return "ok";
	}




	@PostMapping("/createFtpDir")
	@ResponseBody
	public String createFtpDir(String id,String dirName) {

		Ftp ftp = ftpClientMap.get(id);

		boolean flag = ftp.mkdir(dirName);

		return flag+"";
	}




	@PostMapping("/downloadFtpFile")
	@ResponseBody
	public void downloadFtpFile(String id, String fileName, HttpServletResponse response) throws UnsupportedEncodingException {

		Ftp ftp = ftpClientMap.get(id);

		response.setHeader("Content-Disposition","attachment;fileName="
				+ java.net.URLEncoder.encode(fileName, "UTF-8"));
		response.setCharacterEncoding("utf-8");

		try {
			ftp.download(fileName,fileName,response.getOutputStream());
		} catch (IOException e) {
			log.error("下载FTP文件失败！",e);
		}

	}



	@GetMapping("/delFtpFile")
	@ResponseBody
	public String delFtpFile(String id,String fileName,Boolean isDir) {

		Ftp ftp = ftpClientMap.get(id);

		boolean flag = false;
		if (isDir){
			flag = ftp.delDir(fileName);
		}else {
			flag = ftp.delFile(fileName);
		}

		return flag+"";
	}


	@PostMapping("/batchDelFtpFile")
	@ResponseBody
	public String batchDelFtpFile(String id,String splitStr,String fileNameStr) {

		Ftp ftp = ftpClientMap.get(id);

		String host = ftp.getClient().getPassiveHost();

		List<String> fileNameList = HelpMe.easySplit(fileNameStr, splitStr.charAt(0));

		for (String temp:fileNameList){
			List<String> tempList = HelpMe.easySplit(temp);

			String fileName = tempList.get(0);
			boolean isDir = Boolean.parseBoolean(tempList.get(1));

			if (isDir){
				ftp.delDir(fileName);
			}else {
				ftp.delFile(fileName);
			}

			log.info("删除FTP服务器 {} 上的文件：{}",host,fileName);
		}

		return "true";
	}




	@PostMapping(value = "/uploadFtpFile")
	@ResponseBody
	public String uploadFtpFile(String id,HttpServletRequest request) throws IOException {

		MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
		Iterator<String> fns = mRequest.getFileNames();

		while (fns.hasNext()) {
			String fn = fns.next();
			List<MultipartFile> file = mRequest.getFiles(fn);
			for (MultipartFile multipartFile : file) {

				dealUploadFile(multipartFile,id);

				break;
			}
			break;
		}


		return "ok";
	}


	private void dealUploadFile(MultipartFile multipartFile, String id){

		Ftp ftp = ftpClientMap.get(id);

		String fileName = multipartFile.getOriginalFilename();

		double totalSize = multipartFile.getSize() * 1.0;

		log.info("上传文件名称：{}", fileName);

		InputStream in = null;
		try {
			in = multipartFile.getInputStream();
		} catch (IOException e) {
		}

		String fileNameMd5 = SecureUtil.md5(fileName);

		FTPClient ftpClient = ftp.getClient();

		OutputStream out = null;
		try {
			out = ftpClient.storeFileStream(ftp.pwd() + "/" + fileName);
		} catch (IOException e) {
		}

		InputStream finalIn = in;
		OutputStream finalOut = out;
		IoUtil.copyByNIO(in, out, 10240, new StreamProgress() {
			/**
			 * 开始
			 */
			@Override
			public void start() {
				uploadProgressMap.put(fileNameMd5, "0.00%");
			}

			/**
			 * 进行中
			 *
			 * @param progressSize 已经进行的大小
			 */
			@Override
			public void progress(long progressSize) {
				double num = NumberUtil.div(progressSize, totalSize) * 100;
				String percent = NumberUtil.round(num, 2).toString() + "%";
				uploadProgressMap.put(fileNameMd5, percent);
			}

			/**
			 * 结束
			 */
			@Override
			public void finish() {
				uploadProgressMap.put(fileNameMd5, "100.00%");

				try {
					ftpClient.completePendingCommand();
				} catch (IOException e) {
				}

				IoUtil.close(finalIn);
				IoUtil.close(finalOut);

			}
		});

	}


	@GetMapping("/clearFtpUploadState")
	@ResponseBody
	public String clearFtpUploadState(String fileName) {

		String fileNameMd5 = SecureUtil.md5(fileName);

		uploadProgressMap.remove(fileNameMd5);

		return "ok";
	}


	@GetMapping("/ftpUploadState")
	@ResponseBody
	public Map<String,Object> ftpUploadState(String fileName) {

		String fileNameMd5 = SecureUtil.md5(fileName);

		String percent = uploadProgressMap.get(fileNameMd5);

		Map<String,Object> progressMap = Maps.newHashMap();

		if (HelpMe.isNull(percent)){
			progressMap.put("percent","0.00%");
		}else {
			progressMap.put("percent",percent);
		}

		return progressMap;
	}



	private void close(String id){
		Ftp ftp = ftpClientMap.get(id);
		try {
			ftp.close();
		} catch (IOException e) {
		}
		ftpClientMap.remove(id);
	}


	private void reConnect(Ftp ftp,String id){

		String pwd = ftp.pwd();

		close(id);

		SshServerInfo info = new SshServerInfo();
		info.setType(2);
		info.setId(id);
		SshServerInfo ftpInfo = serverInfoService.one(info);

		ftp = connect(ftpInfo);
		ftp.cd(pwd);
		ftpClientMap.put(id,ftp);
	}



	private Ftp connect(SshServerInfo info){

		Ftp ftp = null;
		try {
			ftp = new Ftp(info.getHostname(),info.getPort(),info.getUsername(),info.getPassword());
			ftp.setMode(FtpMode.Passive);
			ftp.getClient().setFileType(FTPClient.BINARY_FILE_TYPE);
		} catch (Exception e) {
			log.error("连接FTP失败！",e);
		}

		return ftp;
	}



}
