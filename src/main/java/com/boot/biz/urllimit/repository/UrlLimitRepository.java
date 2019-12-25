package com.boot.biz.urllimit.repository;


import com.boot.base.BaseRepository;
import com.boot.biz.urllimit.entity.UrlLimit;

import java.util.List;

/**
 * <p>
 * 接口限流 Mapper 接口
 * </p>
 *
 * @author adi
 * @since 2019-06-21
 */
public interface UrlLimitRepository extends BaseRepository<UrlLimit> {

	List<UrlLimit> findByUrlLimitGreaterThan(Integer urlLimit);

}
