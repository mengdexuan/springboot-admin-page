package com.boot.biz.dict.service;

import com.boot.base.BaseService;
import com.boot.biz.dict.entity.Dict;

/**
 * <p>
 * 数据字典表 服务类
 * </p>
 *
 * @author adi
 * @since 2019-05-24
 */
public interface DictService extends BaseService<Dict> {

	String dictVal(String dictKey);

	void reloadDictCache();

}
