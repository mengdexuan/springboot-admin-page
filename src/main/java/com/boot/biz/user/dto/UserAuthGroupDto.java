package com.boot.biz.user.dto;

import lombok.Data;

/**
 * @author mengdexuan on 2019/12/12 15:16.
 */
@Data
public class UserAuthGroupDto {
	private Long authGroupId;
	private String authGroupTitle;
	private Boolean hasAuthGroup;
}
