package com.boot.biz.user.dto;

import com.boot.biz.authgroup.entity.AuthGroup;
import com.boot.biz.menu.entity.Menu;
import com.boot.biz.user.entity.SysUser;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author mengdexuan on 2019/10/25 16:05.
 */
@Data
public class UserAuthInfo {

	private SysUser sysUser;
	private List<AuthGroup> authGroupList;

	private Map<String,List<Menu>> groupMenuMap;
}
