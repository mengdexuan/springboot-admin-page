package com.boot.biz.user.controller;


import com.boot.base.BaseController;
import com.boot.base.Result;
import com.boot.base.exception.GlobalServiceException;
import com.boot.base.jwt.JwtUtil;
import com.boot.base.util.HelpMe;
import com.boot.biz.dict.service.DictService;
import com.boot.biz.user.dto.UserAuthGroupDto;
import com.boot.biz.user.dto.UserAuthInfo;
import com.boot.biz.user.entity.SysUser;
import com.boot.biz.user.service.UserService;
import com.boot.config.SysConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户 控制器
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户 接口")
public class UserController extends BaseController {

	@Autowired
	private UserService userService;

	@Autowired
	DictService dictService;


	/**
	 * 获取用户列
	 */
	@GetMapping(value = "/list")
	@ApiOperation("获取用户列表")
	public Result<List<SysUser>> list() {
		return this.success(userService.findAll());
	}

	@GetMapping(value = "/userAuthInfo")
	@ApiOperation("获取用户权限信息")
	public Result<UserAuthInfo> userAuthInfo() {

		UserAuthInfo userAuthInfo = userService.userAuthInfo();

		return this.success(userAuthInfo);
	}



	@GetMapping(value = "/userAuthGroup")
	@ApiOperation("获取用户所属的权限组")
	public Result<List<UserAuthGroupDto>> userAuthGroup(Long id) {

		List<UserAuthGroupDto> result = userService.userAuthGroup(id);

		return this.success(result);
	}


	/**
	 * 用户登录
	 */
	@PostMapping(value = "/login")
	@ApiOperation("用户登录")
	public Result<SysUser> login(SysUser pbankUser) {

		pbankUser.setPassword(userService.encryptionPwd(pbankUser.getPassword()));

		SysUser persistenceObj = userService.one(pbankUser);

		if (persistenceObj==null){
			String dictVal = dictService.dictVal(SysConfig.err_code_10001);
			throw new GlobalServiceException(Integer.parseInt(SysConfig.err_code_10001),dictVal);
		}else {
			long timeOut = Long.parseLong(dictService.dictVal(SysConfig.login_time_out));
			long expirationTime = timeOut * 60 * 1000;
			String token = JwtUtil.generateToken(persistenceObj.getId(),expirationTime);
			persistenceObj.setKey(token);
		}

		return this.success(persistenceObj);
	}


	/**
	 * 新增用户
	 */
	@PostMapping(value = "/add")
	@ApiOperation("新增用户")
	public Result<String> add(SysUser pbankUser) {
		pbankUser.setId(null);
		pbankUser.setCreateTime(new Date());
		pbankUser.setLastLoginTime(new Date());
		pbankUser.setStatus(1);
		pbankUser.setPassword(userService.encryptionPwd(pbankUser.getPassword()));

		userService.save(pbankUser);

		return this.success();
	}

	/**
	 * 删除用户
	 */
	@PostMapping(value = "/delete/{id}")
	@ApiOperation("删除用户")
	public Result<String> delete(@PathVariable("id") Long id) {
		userService.delete(id);
		return this.success();
	}

	/**
	 * 修改用户
	 */
	@PostMapping(value = "/update")
	@ApiOperation("修改用户")
	public Result<String> update(SysUser pbankUser) {

		if (HelpMe.isNotNull(pbankUser.getPassword())) {
			pbankUser.setPassword(userService.encryptionPwd(pbankUser.getPassword()));
		}

		userService.update(pbankUser);

		return this.success();
	}

	/**
	 * 获取用户
	 */
	@GetMapping(value = "/get/{id}")
	@ApiOperation("获取用户")
	public Result<SysUser> get(@PathVariable("id") Long id) {
		return this.success(userService.get(id));
	}
}

