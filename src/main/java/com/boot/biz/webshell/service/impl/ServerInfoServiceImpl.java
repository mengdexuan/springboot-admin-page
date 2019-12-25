package com.boot.biz.webshell.service.impl;

import com.boot.base.BaseServiceImpl;
import com.boot.biz.webshell.entity.SshServerInfo;
import com.boot.biz.webshell.repository.ServerInfoRepository;
import com.boot.biz.webshell.service.ServerInfoService;
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
public class ServerInfoServiceImpl extends BaseServiceImpl<SshServerInfo,ServerInfoRepository> implements ServerInfoService {

}
