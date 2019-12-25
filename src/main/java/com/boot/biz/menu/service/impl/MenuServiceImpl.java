package com.boot.biz.menu.service.impl;

import com.boot.base.BaseServiceImpl;
import com.boot.biz.menu.entity.Menu;
import com.boot.biz.menu.repository.MenuRepository;
import com.boot.biz.menu.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
@Service
public class MenuServiceImpl extends BaseServiceImpl<Menu,MenuRepository> implements MenuService {

	@Autowired
	MenuRepository repository;

	@Override
	public List<Menu> findByIdList(List<Long> idList) {
		return repository.findByIdList(idList);
	}
}
