package com.boot.base.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mengdexuan on 2020/2/7 12:13.
 */
@Data
@ApiModel("分页信息")
public class PageInfo {

	@ApiModelProperty("当前页")
	private Integer pageNo = 1;//当前页
	@ApiModelProperty("每页条数")
	private Integer pageSize = 10;//每页条数
}
