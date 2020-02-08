package com.boot.biz.requestfollow.repository;

import com.boot.base.BaseRepository;
import com.boot.biz.requestfollow.entity.RequestFollow;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * <p>
 * 请求追踪记录 Mapper 接口
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-12-16
 */
public interface RequestFollowRepository extends BaseRepository<RequestFollow> {

	//利用原生的SQL进行删除操作
	@Query(value = "delete from request_follow where id < ?1", nativeQuery = true)
	@Modifying
	public void delData(Long id);
}
