package com.boot.biz.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 实现ConstraintValidator接口编写约束校验器
 * @author mengdexuan on 2022/1/4 18:25.
 */
public class EncryptIdValidator implements ConstraintValidator<EncryptId, String> {

	private static final Pattern PATTERN = Pattern.compile("^[a-f\\d]{1,8}$");

	@Override
	public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
		// 不为null才进行校验
		if (value != null) {
			Matcher matcher = PATTERN.matcher(value);
			return matcher.find();
		}
		return true;
	}

}
