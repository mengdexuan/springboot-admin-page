package com.boot.biz.ws;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.json.JSONUtil;
import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.boot.biz.keyval.service.KeyValService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * @author mengdexuan on 2022/3/29 19:04.
 */
@Slf4j
@Controller
@RequestMapping("/back/ws")
public class LogController {

    @GetMapping("/view")
    public String toLogPage(ModelAndView modelAndView,String dataId) {
        log.info("to view log page...");

        modelAndView.addObject("dataId",dataId);

        return "back/sys/logView";
    }



    @GetMapping("/pageInfo")
    @ResponseBody
    public Result pageInfo(String dataId) {



        return ResultUtil.buildSuccess(dataId);
    }




    @GetMapping("/download")
    public void download(HttpServletResponse response, String dataId) throws Exception {

        File file = new File("");

        String filename = file.getName();
        filename = java.net.URLEncoder.encode(filename, "UTF-8");

        response.setHeader("Content-Disposition", "filename=" + filename);
        response.setCharacterEncoding("utf-8");
        ServletOutputStream out = response.getOutputStream();

        FileInputStream in = new FileInputStream(file);
        IoUtil.copyByNIO(in, out, 1024, (StreamProgress) null);
        IoUtil.close(in);
        IoUtil.close(out);
    }












}
