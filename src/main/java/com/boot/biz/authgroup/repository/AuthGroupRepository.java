package com.boot.biz.authgroup.repository;

import com.boot.base.BaseRepository;
import com.boot.biz.authgroup.entity.AuthGroup;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * <p>
 * 权限组表 Mapper 接口
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
public interface AuthGroupRepository extends BaseRepository<AuthGroup> {

	@Query(value = "select * from auth_group where id in ?1 and status = 1",nativeQuery = true)
	@Modifying
	List<AuthGroup> findByIdList(List<Long> idList);
}
