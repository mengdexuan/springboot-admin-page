package com.boot.biz.validation;

import com.boot.base.Result;
import com.boot.base.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author mengdexuan on 2019/6/19 18:28.
 */
@Validated
@RestController
@Slf4j
@RequestMapping("/validate")
public class ValidateController {


	@PostMapping("/test1")
	public Result<String> test1(@Validated ValidatedBean validatedBean) {


		return ResultUtil.buildSuccess("abc");
	}


	@PostMapping("/test2")
	public Result test2(@Validated @RequestBody ValidatedBean validatedBean) {


		return ResultUtil.buildSuccess();
	}


	// 路径变量
	@GetMapping("/{userId}")
	public Result detail(@PathVariable("userId") @Min(value = 10, message = "最小填写 10")
									 Long userId) {

		return ResultUtil.buildSuccess();
	}

	// 查询参数
	@GetMapping("/getByAccount")
	public Result getByAccount(@NotEmpty @Length(min = 6, max = 10) @RequestParam String account) {
		// 校验通过，才会执行业务逻辑处理

		return ResultUtil.buildSuccess();
	}


	/**
	 * 测试分组校验
	 * @param userDTO
	 * @return
	 */
	@PostMapping("/save")
	public Result saveUser(@RequestBody @Validated(UserDTO.Save.class) UserDTO userDTO) {
		// 校验通过，才会执行业务逻辑处理
		return ResultUtil.buildSuccess();
	}

	/**
	 * 测试分组校验
	 * @param userDTO
	 * @return
	 */
	@PostMapping("/update")
	public Result updateUser(@RequestBody @Validated(UserDTO.Update.class) UserDTO userDTO) {
		// 校验通过，才会执行业务逻辑处理
		return ResultUtil.buildSuccess();
	}


	/**
	 * 测试嵌套校验
	 * @param userDTO
	 * @return
	 */
	@PostMapping("/nest")
	public Result nest(@RequestBody @Validated(UserDTO.Update.class) UserDTO userDTO) {
		// 校验通过，才会执行业务逻辑处理
		return ResultUtil.buildSuccess();
	}


	/**
	 * 集合校验
	 * @param userList
	 * @return
	 */
	@PostMapping("/saveList")
	public Result saveList(@RequestBody @Validated(UserDTO.Save.class) ValidationList<UserDTO> userList) {
		// 校验通过，才会执行业务逻辑处理
		return ResultUtil.buildSuccess();
	}



	/**
	 * 自定义校验
	 * @param pwd
	 * @return
	 */
	@PostMapping("/self")
	public Result self(@EncryptId @RequestParam String pwd) {
		// 校验通过，才会执行业务逻辑处理
		return ResultUtil.buildSuccess();
	}



	/**
	 * 自定义校验
	 * @param pwd
	 * @return
	 */
	@PostMapping("/self2")
	public Result self2(@Validated UserInfo userInfo) {
		// 校验通过，才会执行业务逻辑处理
		return ResultUtil.buildSuccess();
	}
















}
