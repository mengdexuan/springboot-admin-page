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
