package com.boot.biz.menu.repository;

import com.boot.base.BaseRepository;
import com.boot.biz.menu.entity.Menu;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
public interface MenuRepository extends BaseRepository<Menu> {

	@Query(value = "select * from sys_menu where id in ?1 and status = 1",nativeQuery = true)
	@Modifying
	List<Menu> findByIdList(List<Long> idList);

}
