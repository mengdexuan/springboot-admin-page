package com.boot.biz.dict.controller;


import cn.hutool.core.bean.BeanUtil;
import com.boot.base.BaseController;
import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.boot.base.util.SpringContextUtil;
import com.boot.biz.dict.entity.Dict;
import com.boot.biz.dict.service.DictService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 数据字典表 控制器
 * </p>
 *
 * @author adi
 * @since 2019-05-24
 */
@RestController
@RequestMapping("/dict")
public class DictController extends BaseController {

	@Autowired
	private DictService dictService;

	/**
	 * 获取数据字典表列表
	 */
	@GetMapping(value = "/list")
	public Result<List<Dict>> list() {
		return ResultUtil.buildSuccess(dictService.findAll());
	}

	/**
	 * 新增数据字典表
	 */
	@PostMapping(value = "/add")
	public Result<String> add(Dict dataCentralDict) {
		dataCentralDict.setOrderNo(1);
		dataCentralDict.setEnable(1);
		dataCentralDict.setCreateTime(new Date());

		dictService.save(dataCentralDict);

		dictService.reloadDictCache();

		return ResultUtil.buildSuccess();
	}

	/**
	 * 删除数据字典表
	 */
	@PostMapping(value = "/delete/{id}")
	public Result<String> delete(@PathVariable("id") Long id) {
		dictService.delete(id);
		dictService.reloadDictCache();
		return ResultUtil.buildSuccess();
	}


	/**
	 * 复制1条配置
	 */
	@PostMapping(value = "/copy/{id}")
	public Result<String> copy(@PathVariable("id") Long id) {

		Dict one = dictService.get(id);

		Dict dict = new Dict();

		BeanUtil.copyProperties(one, dict);

		dict.setDictKey(dict.getDictKey() + "_copy");
		dict.setId(null);

		dictService.save(dict);

		dictService.reloadDictCache();
		return ResultUtil.buildSuccess();
	}


	/**
	 * 修改数据字典表
	 */
	@PostMapping(value = "/update")
	public Result<String> update(Dict dataCentralDict) {

		Dict entity = dictService.getByFieldEqual("dictKey", dataCentralDict.getDictKey());

		boolean changeVal = true;

		if (entity.getDictValue().equals(dataCentralDict.getDictValue())) {
			// val 值没有被修改
			changeVal = false;
		}

		dictService.update(dataCentralDict);

		dictService.reloadDictCache();

		if (changeVal) {
			//发布数据字典更新事件
			SpringContextUtil.applicationContext.publishEvent(dataCentralDict);
		}


		return ResultUtil.buildSuccess();
	}

	/**
	 * 获取数据字典表
	 */
	@GetMapping(value = "/get/{id}")
	public Result<Dict> get(@PathVariable("id") Long id) {
		return ResultUtil.buildSuccess(dictService.get(id));
	}


	/**
	 * 实体状态映射
	 */
	@GetMapping(value = "/statusMap")
	@ApiOperation("实体状态映射")
	public Result statusMap() {

		Map<String,Object> result = statusMap(Dict.class);

		return this.success(result);
	}





}

