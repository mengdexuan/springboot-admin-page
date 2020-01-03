package com.boot.biz.webshell.controller;

import ch.ethz.ssh2.Connection;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.ftp.Ftp;
import com.boot.base.util.HelpMe;
import com.boot.biz.webshell.entity.SshServerInfo;
import com.boot.biz.webshell.service.ServerInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class ServerInfoController {


	@Autowired
	ServerInfoService serverInfoService;


	@RequestMapping("/sshServerList")
	public String sshServerList(Model model) {

		log.info("sshServerList页面");

		SshServerInfo info = new SshServerInfo();
		info.setType(1);

		List<SshServerInfo> infoList = serverInfoService.list(info);

		infoList = infoList.stream().sorted(Comparator.comparing(SshServerInfo::getCreateTime)).collect(Collectors.toList());

		model.addAttribute("infoList",infoList);

		return "ssh/sshServerList";
	}



	@RequestMapping("/webShell")
	public String webShell(Model model,String id) {

		log.info("进入 webShell 页面...");

		SshServerInfo info = new SshServerInfo();
		info.setId(id);
		info.setType(1);

		SshServerInfo one = serverInfoService.one(info);

		model.addAttribute("sshInfo",one);

		return "ssh/webShell";
	}



	@RequestMapping("/checkSshServer")
	@ResponseBody
	public Boolean checkSshServer(SshServerInfo info){

		log.info("验证信息：{}",info);

		if (info.getType()==1){
			// 建立连接
			Connection conn = new Connection(info.getHostname(), info.getPort());
			// 连接
			try {
				conn.connect();
			} catch (IOException e) {
			}

			// 授权
			boolean isAuthenticate = false;
			try {
				isAuthenticate = conn.authenticateWithPassword(info.getUsername(), info.getPassword());
			} catch (IOException e) {
			}
			if (isAuthenticate) {
				conn.close();
				return true;
			}
		}else if (info.getType()==2){//ftp
			try {
				Ftp ftp = new Ftp(info.getHostname(),info.getPort(),info.getUsername(),info.getPassword());
				ftp.close();
				ftp = null;
				return true;
			} catch (Exception e) {
				log.error("测试FTP连接失败！",e);
			}
		}

		return false;
	}



	@RequestMapping("/addSshServer")
	@ResponseBody
	public Boolean addSshServer(SshServerInfo info){

		if (checkSshServer(info)){
			log.info("添加SshServer信息：{}",info);

			SshServerInfo temp = new SshServerInfo();
			BeanUtil.copyProperties(info,temp);
			temp.setNote(null);

			SshServerInfo one = serverInfoService.one(temp);

			if (one==null){
				info.setId(HelpMe.uuid());
				info.setCreateTime(new Date());
				serverInfoService.save(info);
			}else {
				log.info("服务器已经存在！");
				return false;
			}
		}

		return true;

	}


	@RequestMapping("/editSshServer")
	@ResponseBody
	public Boolean editSshServer(SshServerInfo info){

		serverInfoService.update(info);

		return true;

	}



	@RequestMapping("/delSshServer")
	@ResponseBody
	public Boolean delSshServer(String id){

		log.info("删除SshServer信息，id：{}",id);

		SshServerInfo info = new SshServerInfo();
		info.setId(id);

		serverInfoService.delete(info);

		return true;
	}




}



















