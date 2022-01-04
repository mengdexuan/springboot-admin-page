package com.boot.biz.validation;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Validated分组校验
 *
 * 在实际项目中，可能多个方法需要使用同一个DTO类来接收参数，而不同方法的校验规则很可能是不一样的。这个时候，简单地在DTO类的字段上加约束注解无法解决这个问题。因此，spring-validation支持了分组校验的功能，专门用来解决这类问题。
 *
 * 还是上面的例子，比如保存User的时候，UserId是可空的，但是更新User的时候，UserId的值必须>=10000000000000000L；其它字段的校验规则在两种情况下一样。这个时候使用分组校验的代码示例如下：
 *
 * 约束注解上声明适用的分组信息groups
 *
 * @author mengdexuan on 2022/1/4 17:55.
 */
@Data
public class UserDTO {

	@NotNull(groups = {Update.class})
	private Long userId;

	@NotNull(groups = {Save.class, Update.class})
	@Length(min = 2, max = 10, groups = {Save.class, Update.class})
	private String userName;

	@NotNull(groups = {Save.class, Update.class})
	@Length(min = 6, max = 10, groups = {Save.class, Update.class})
	private String account;

	@NotNull(groups = {Save.class, Update.class})
	@Length(min = 5, max = 8, groups = {Save.class, Update.class})
	private String password;

	/**
	 * 保存的时候校验分组
	 */
	public interface Save {
	}

	/**
	 * 更新的时候校验分组
	 */
	public interface Update {
	}


	/**
	 * 嵌套校验，此时DTO类的对应字段必须标记@Valid注解。
	 */
	@NotNull(groups = {Save.class, Update.class})
	@Valid
	private SubJob subJob;


	@Data
	public static class SubJob {

		@Min(value = 1, groups = Update.class)
		private Long jobId;

		@NotNull(groups = {Save.class, Update.class})
		@Length(min = 2, max = 10, groups = {Save.class, Update.class})
		private String jobName;

		@NotNull(groups = {Save.class, Update.class})
		@Length(min = 2, max = 10, groups = {Save.class, Update.class})
		private String position;
	}




}
