package com.boot.biz.menu.service;


import com.boot.base.BaseService;
import com.boot.biz.menu.entity.Menu;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
public interface MenuService extends BaseService<Menu> {

	List<Menu> findByIdList(List<Long> idList);

}
