package com.boot.base.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.RuntimeUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * @author mengdexuan on 2021/9/1 17:28.
 */
@Slf4j
public class ShellExeUtil {


	/**
	 * 使用docker部署时，有时使用 Runtime.getRuntime().exec() 执行命令不生效；把命令写到一个脚本中，
	 * 再执行脚本是生效的，此处的功能就是这样：将要执行的命令写入一个临时脚本中，执行脚本，获取结果，删除脚本
	 * @param cmd
	 * @return
	 */
	public static String exeCmd(String cmd){

		String head = "#!/usr/bin/env bash";

		File file = FileUtil.newFile(IdUtil.fastSimpleUUID() + ".sh");

		FileUtil.appendUtf8String(head,file);
		FileUtil.appendUtf8String("\n",file);
		FileUtil.appendUtf8String(cmd,file);

		log.info("生成临时脚本文件：{}",file.getAbsolutePath());

		//设置可执行权限
		RuntimeUtil.execForStr("chmod 777 "+file.getAbsolutePath());

		log.info("\n------ 脚本内容如下\n\n{}\n\n------ 脚本内容结束",FileUtil.readUtf8String(file));

		String cmdResult = RuntimeUtil.execForStr(file.getAbsolutePath());

		log.info("脚本执行结果：\n{}",cmdResult);

		log.info("删除临时脚本文件：{}",file.getAbsolutePath());

		FileUtil.del(file);

		return cmdResult;
	}



	/**
	 * 按行获取执行结果
	 * @param cmd
	 * @return
	 */
	public static List<String> exeCmdGetList(String cmd){

		String str = exeCmd(cmd);

		File file = FileUtil.newFile(IdUtil.fastSimpleUUID() + ".txt");

		FileUtil.appendUtf8String(str,file);

		List<String> list = FileUtil.readUtf8Lines(file);

		FileUtil.del(file);

		List<String> result = Lists.newArrayList();

		for (String item:list){
			//将 tab 换成 4 个空格
			String temp = ReUtil.replaceAll(item, "\t", "    ");
			result.add(temp);
		}

		return result;
	}








}
