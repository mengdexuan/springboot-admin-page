package com.boot.base.encrypt;

import com.alibaba.druid.filter.config.ConfigTools;
import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author mengdexuan on 2021/9/4 15:42.
 */
@RestController
@RequestMapping("/encrypt")
@Api(tags = "加密 接口")
@Slf4j
public class EncryptController {


	@Autowired
	Environment environment;


	/**
	 * druid 非对称加密：对传入的 pwd 进行加密返回
	 *
	 * 	 类似druid的jar包工具： java -cp druid-1.1.9.jar com.alibaba.druid.filter.config.ConfigTools 123456pwd >aaa.txt
	 *
	 * @param pwd
	 * @return
	 */
	@GetMapping("/druid/encrypt")
	public Result encrypt(String pwd) throws Exception{

		String[] arr = ConfigTools.genKeyPair(512);

		HashMap<String, Object> map = Maps.newHashMap();

		map.put("privateKey",arr[0]);
		map.put("publicKey",arr[1]);
		map.put("encryptPwd",ConfigTools.encrypt(arr[0], pwd));

		return ResultUtil.buildSuccess(map);
	}


	/**
	 * druid 非对称加密：使用 publicKey 解密 encryptPwd 并返回
	 * @param encryptPwd
	 * @param publicKey
	 * @return
	 */
	@GetMapping("/druid/decrypt")
	public Result<String> decrypt(String publicKey,String encryptPwd) throws Exception{

		String result = ConfigTools.decrypt(publicKey, encryptPwd);

		return ResultUtil.buildSuccess(result);
	}


	/**
	 * 获取 Spring Environment 环境配置的值
	 * @param evnName
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/envVal")
	@ResponseBody
	public Result envName(@RequestBody String evnName) throws Exception {

		String val = environment.getProperty(evnName);

		return ResultUtil.buildSuccess(val);
	}



	@Autowired
	private StringEncryptor stringEncryptor;


	/**
	 * @param val	待加密字符串
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/jasypt/encrypt")
	public Result jasyptEncrypt(String val) throws Exception{

		String result = stringEncryptor.encrypt(val);

		return ResultUtil.buildSuccess(result);
	}



	/**
	 * @param val	待解密字符串
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/jasypt/decrypt")
	public Result<String> jasyptDecrypt(String val) throws Exception{

		String result = stringEncryptor.decrypt(val);

		return ResultUtil.buildSuccess(result);
	}






















}
