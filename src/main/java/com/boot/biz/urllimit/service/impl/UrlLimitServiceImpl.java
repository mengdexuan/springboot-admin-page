package com.boot.biz.urllimit.service.impl;

import com.boot.base.BaseServiceImpl;
import com.boot.biz.urllimit.entity.UrlLimit;
import com.boot.biz.urllimit.repository.UrlLimitRepository;
import com.boot.biz.urllimit.service.UrlLimitService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 接口限流 服务实现类
 * </p>
 *
 * @author adi
 * @since 2019-06-21
 */
@Service
public class UrlLimitServiceImpl extends BaseServiceImpl<UrlLimit,UrlLimitRepository> implements UrlLimitService {

}
