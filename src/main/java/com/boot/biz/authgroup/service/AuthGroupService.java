package com.boot.biz.authgroup.service;


import com.boot.base.BaseService;
import com.boot.biz.authgroup.entity.AuthGroup;

import java.util.List;

/**
 * <p>
 * 权限组表 服务类
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
public interface AuthGroupService extends BaseService<AuthGroup> {

	List<AuthGroup> findByIdList(List<Long> idList);

}
