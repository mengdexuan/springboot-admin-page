package com.boot.biz.user.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.CacheObj;
import cn.hutool.cache.impl.LRUCache;
import cn.hutool.crypto.SecureUtil;
import com.boot.base.BaseServiceImpl;
import com.boot.base.util.HelpMe;
import com.boot.biz.authgroup.entity.AuthGroup;
import com.boot.biz.authgroup.service.AuthGroupService;
import com.boot.biz.dict.service.DictService;
import com.boot.biz.menu.entity.Menu;
import com.boot.biz.menu.service.MenuService;
import com.boot.biz.user.RequestUserHolder;
import com.boot.biz.user.dto.UserAuthGroupDto;
import com.boot.biz.user.dto.UserAuthInfo;
import com.boot.biz.user.entity.SysUser;
import com.boot.biz.user.repository.UserRepository;
import com.boot.biz.user.service.UserService;
import com.boot.biz.userauthgroup.entity.UserAuthGroup;
import com.boot.biz.userauthgroup.service.UserAuthGroupService;
import com.boot.config.SysConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
@Slf4j
@Service
public class UserServiceImpl extends BaseServiceImpl<SysUser,UserRepository> implements UserService {

	@Autowired
	DictService dictService;

	@Autowired
	UserAuthGroupService userAuthGroupService;

	@Autowired
	AuthGroupService groupService;

	@Autowired
	MenuService menuService;

	@Autowired
	SysConfig sysConfig;

	//缓存默认大小 100
	private static int cacheSize = 100;

	//30*60*1000	缓存默认30分钟过期
	private LRUCache<String, SysUser> cache;

	@PostConstruct
	private void init(){

		cache = CacheUtil.newLRUCache(cacheSize, sysConfig.loginTimeOut*60*1000);

		//过期检测
		cache.prune();
	}

	//加密用户密码
	public String encryptionPwd(String pwd){
		return SecureUtil.md5(pwd);
	}

	//设置登录用户过期时间
	public void setCacheTimeout(long minute){

		Iterator<CacheObj<String, SysUser>> iterator = cache.cacheObjIterator();

		LRUCache<String, SysUser> tempCache = CacheUtil.newLRUCache(cacheSize, minute * 60 * 1000);

		while (iterator.hasNext()) {
			CacheObj<String, SysUser> item = (CacheObj<String, SysUser>) iterator.next();
			tempCache.put(item.getKey(),item.getValue());
		}

		cache.clear();
		cache = null;
		cache = tempCache;
		cache.prune();
	}


	//通过Key，查询已登录的用户
	public SysUser loginUserByKey(String key){
		return cache.get(key);
	}

	//将登录成功的用户加入到缓存中
	public void loginUser2Cache(String key,SysUser pbankUser){
		cache.put(key,pbankUser);
	}


	/**
	 * 登录用户权限信息
	 *
	 * @return
	 */
	@Override
	public UserAuthInfo userAuthInfo() {

		UserAuthInfo info = new UserAuthInfo();
		SysUser user = RequestUserHolder.getCurrentUser();

		UserAuthGroup authGroup = new UserAuthGroup();
		authGroup.setUid(user.getId());

		List<UserAuthGroup> userAuthGroupList = userAuthGroupService.list(authGroup);

		List<Long> groupIdList = userAuthGroupList.stream().map(item -> {
			return item.getGroupId();
		}).collect(Collectors.toList());

		List<AuthGroup> groupList = groupService.findByIdList(groupIdList);

		Map<String,List<Menu>> groupMenuMap = Maps.newHashMap();

		for (AuthGroup group:groupList){
			String menus = group.getMenus();
			List<Menu> menuList = Lists.newArrayList();

			if (HelpMe.isNotNull(menus)){
				List<String> menuStrList = HelpMe.easySplit(menus);

				List<Long> menuIdList = menuStrList.stream().map(item -> {
					return Long.parseLong(item);
				}).collect(Collectors.toList());

				menuList = menuService.findByIdList(menuIdList);
			}

			groupMenuMap.put(group.getTitle(),menuList);
		}

		info.setSysUser(user);
		info.setAuthGroupList(groupList);
		info.setGroupMenuMap(groupMenuMap);

		log.info("登录用户权限信息：{}",info);

		return info;
	}


	/**
	 * 获取用户所属的权限组
	 *
	 * @param id
	 */
	@Override
	public List<UserAuthGroupDto> userAuthGroup(Long id) {

		AuthGroup authGroup = new AuthGroup();
		authGroup.setStatus(1);

		List<AuthGroup> list = groupService.list(authGroup);

		UserAuthGroup userAuthGroup = new UserAuthGroup();
		userAuthGroup.setUid(id);

		List<UserAuthGroup> userAuthGroupList = userAuthGroupService.list(userAuthGroup);


		List<Long> groupIdList = userAuthGroupList.stream().map(item -> {
			return item.getGroupId();
		}).collect(Collectors.toList());


		List<UserAuthGroupDto> result = Lists.newArrayList();

		for (AuthGroup item:list){
			UserAuthGroupDto dto = new UserAuthGroupDto();
			dto.setAuthGroupId(item.getId());
			dto.setAuthGroupTitle(item.getTitle());
			if (groupIdList.contains(item.getId())){
				dto.setHasAuthGroup(true);
			}else {
				dto.setHasAuthGroup(false);
			}
			result.add(dto);
		}

		return result;
	}
}
