package com.boot.biz.validation;

import lombok.Data;

/**
 * @author mengdexuan on 2022/1/4 18:31.
 */
@Data
public class UserInfo {

	@EncryptId
	private String pwd;

}
