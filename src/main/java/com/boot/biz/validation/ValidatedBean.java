package com.boot.biz.validation;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author mengdexuan on 2019/1/21 14:34.
 */
@Data
public class ValidatedBean {

	@Min(value = 5,message = "ID最小值应不小于5")
	@Max(value = 10,message = "ID最大值不超过10！")
	@NotNull
	private Integer id;



	@NotEmpty(message = "名称不允许为空！")
	private String name;

}
