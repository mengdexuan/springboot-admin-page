package com.boot.base;

import com.boot.base.util.ShellExeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mengdexuan on 2021/11/18 15:49.
 */
@Slf4j
@RestController
@RequestMapping("/shell")
@Api(tags = "执行shell命令 接口")
public class ShellExecController extends BaseController {


	@PostMapping(value = "/exec")
	@ResponseBody
	@ApiOperation("执行shell命令")
	public Result exec(@RequestBody String cmd) {

		String result = ShellExeUtil.exeCmd(cmd);

		return success(result);
	}




}
