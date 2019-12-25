package com.boot.biz.user.service;

import com.boot.base.BaseService;
import com.boot.biz.user.dto.UserAuthGroupDto;
import com.boot.biz.user.dto.UserAuthInfo;
import com.boot.biz.user.entity.SysUser;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
public interface UserService extends BaseService<SysUser> {

	//加密密码
	String encryptionPwd(String pwd);

	/**
	 * 设置用户登录后的过期时间，单位分钟
	 * @param minute
	 */
	void setCacheTimeout(long minute);


	/**
	 * 通过登录后的key，获取登录用户
	 * @param key
	 * @return
	 */
	SysUser loginUserByKey(String key);


	/**
	 * 登录成功后，将用户加入到cache中
	 * @param key
	 * @param sysUser
	 */
	void loginUser2Cache(String key, SysUser sysUser);

	/**
	 * 登录用户权限信息
	 * @return
	 */
	UserAuthInfo userAuthInfo();

	/**
	 *获取用户所属的权限组
	 * @param id
	 */
	List<UserAuthGroupDto> userAuthGroup(Long id);
}
