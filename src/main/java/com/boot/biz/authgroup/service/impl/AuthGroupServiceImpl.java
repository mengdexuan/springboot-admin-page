package com.boot.biz.authgroup.service.impl;

import com.boot.base.BaseServiceImpl;
import com.boot.biz.authgroup.entity.AuthGroup;
import com.boot.biz.authgroup.repository.AuthGroupRepository;
import com.boot.biz.authgroup.service.AuthGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 权限组表 服务实现类
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
@Service
public class AuthGroupServiceImpl extends BaseServiceImpl<AuthGroup,AuthGroupRepository> implements AuthGroupService {

	@Autowired
	AuthGroupRepository repository;

	@Override
	public List<AuthGroup> findByIdList(List<Long> idList) {
		return repository.findByIdList(idList);
	}

}
