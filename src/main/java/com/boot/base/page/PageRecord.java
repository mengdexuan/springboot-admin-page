package com.boot.base.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author mengdexuan on 2020/2/7 11:16.
 */
@Data
@ApiModel("分页数据实体")
public class PageRecord<T> {
	@ApiModelProperty("当前页")
	private Integer pageNo;//当前页
	@ApiModelProperty("每页条数")
	private Integer pageSize;//每页条数
	@ApiModelProperty("总条数")
	private Long totalSize;//总条数
	@ApiModelProperty("总页数")
	private Integer totalPage;//总页数
	@ApiModelProperty("是否为第1页")
	private Boolean isFirst;//是否为第1页
	@ApiModelProperty("是否为最后1页")
	private Boolean isLast;//是否为最后1页
	@ApiModelProperty("当前页数据")
	private List<T> dataList;
}
