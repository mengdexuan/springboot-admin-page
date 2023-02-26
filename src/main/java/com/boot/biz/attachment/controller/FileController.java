package com.boot.biz.attachment.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.boot.base.BaseController;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author mengdexuan on 2023-02-26 18:47.
 */
@RestController
@RequestMapping("/file")
@Api(tags = "文件上传 接口")
@Slf4j
public class FileController extends BaseController {

    /**
     * 导出模板
     *
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {

        String path = "/templates/excel/信息管理模版.xlsx";

        //这种方式springboot打成 jar 后，依然可以拿到类路径下的xlsx文件
        InputStream in = FileController.class.getResourceAsStream(path);

        String filename = new File(path).getName();
        filename = java.net.URLEncoder.encode(filename, "UTF-8");

        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        response.setCharacterEncoding("utf-8");
        ServletOutputStream out = response.getOutputStream();

        IoUtil.copyByNIO(in, out, 1024, (StreamProgress) null);
        IoUtil.close(in);
        IoUtil.close(out);
    }


    /**
     * 模板数据导入
     *
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/import")
    public void importExcel(@RequestParam("file") MultipartFile file) throws Exception {

        BufferedOutputStream out = null;

        String extName = FileUtil.extName(file.getOriginalFilename());
        String path = IdUtil.simpleUUID() + "." + extName;
        log.info("导入excel文件 --> " + path);
        File excel = new File(path);
        try {
            out = new BufferedOutputStream(new FileOutputStream(excel));

            InputStream in = file.getInputStream();
            IoUtil.copyByNIO(in, out, 1024, (StreamProgress) null);
            IoUtil.close(in);
            IoUtil.close(out);

        } catch (Exception e) {
            log.info("文件导入失败，{}", e.getMessage());
        }

        ThreadUtil.safeSleep(0);

        //业务处理后，将上传的临时文件删除
        FileUtil.del(excel);

    }
}
