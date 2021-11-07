package com.boot.biz.attachment.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.util.StrUtil;
import com.boot.base.BaseController;
import com.boot.base.Result;
import com.boot.base.util.HelpMe;
import com.boot.biz.attachment.entity.Attachment;
import com.boot.biz.attachment.service.AttachmentService;
import com.boot.biz.dict.service.DictService;
import com.boot.config.SysConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 附件 控制器
 * </p>
 *
 * @author code-make-tool
 * @since 2020-03-16
 */
@RestController
@RequestMapping("/attachment")
@Api(tags = "附件 接口")
@Slf4j
public class AttachmentController extends BaseController {

    @Autowired
    private DictService dictService;

    @Autowired
    private AttachmentService attachmentService;

    @PostMapping(value = "/upload")
    @ResponseBody
    @ApiOperation("上传附件")
    public Result<Attachment> upload(HttpServletRequest request) throws IOException {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        MultipartFile file = null;
        BufferedOutputStream out = null;

        String uuid = HelpMe.uuid();

        file = files.get(0);
        String fileName = file.getOriginalFilename();
        String extName = FileUtil.extName(fileName);

        HelpMe.createDir(storageDir(), uuid);

        String storePath = HelpMe.createDir(storageDir(), uuid) + File.separator + fileName;

        Attachment attachment = new Attachment();

        try {
            File attachmentFile = new File(storePath);

            log.info("文件存储路径：{}", attachmentFile.getAbsolutePath());

            out = new BufferedOutputStream(new FileOutputStream(attachmentFile));

            InputStream in = file.getInputStream();
            IoUtil.copyByNIO(in, out, 1024, (StreamProgress) null);
            IoUtil.close(in);
            IoUtil.close(out);


            attachment.setUuid(uuid);
            attachment.setCreateTime(new Date());
            attachment.setFileExt(extName);
            attachment.setFileName(fileName);

            String savePath = StrUtil.removePrefix(attachmentFile.getAbsolutePath(), dictService.dictVal(SysConfig.file_save_dir));
            attachment.setFilePath(HelpMe.removeFirstChar(savePath));

            attachment.setFileSize(file.getSize());

            attachmentService.save(attachment);

        } catch (Exception e) {
            log.error("上传附件失败！", e);
        }

        return success(attachment);
    }


    @GetMapping(value = "/download/{uuid}")
    @ApiOperation("下载附件")
    public void download(HttpServletResponse response, @PathVariable String uuid) throws Exception {

        Attachment attachment = attachmentService.getByFieldEqual("uuid", uuid);

        String savePath = dictService.dictVal(SysConfig.file_save_dir) + File.separator + attachment.getFilePath();

        File file = new File(savePath);

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


    @GetMapping(value = "/del/{uuid}")
    @ApiOperation("删除附件")
    public Result del(HttpServletResponse response, @PathVariable String uuid) throws Exception {

        Attachment attachment = attachmentService.getByFieldEqual("uuid", uuid);

        String savePath = dictService.dictVal(SysConfig.file_save_dir) + File.separator + attachment.getFilePath();

        File file = new File(savePath);

        FileUtil.del(file.getParentFile());

        attachmentService.delete(attachment);

        return success();
    }


    public static void main(String[] args) {
        testUpload();
    }


    public static void testUpload() {
        RestTemplate restTemplate = new RestTemplate();

        String serverUrl = "http://localhost:2020/pbank/attachment.sql/upload";
        String filePath = "C:\\Users\\18514\\Desktop\\test\\queryEsTest.tar.gz";


        File file = new File(filePath);
        FileSystemResource resource = new FileSystemResource(file);


        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("file", resource);

        Result result = restTemplate.postForObject(serverUrl, param, Result.class);

        log.info("{}", result);

    }


    private String attachmentPath;

//    @PostConstruct
    private void init() {
        attachmentPath = HelpMe.createDir(dictService.dictVal(SysConfig.file_save_dir), "attachment.sql");
    }

    private String storageDir() {
        String dateDir = DateUtil.format(new Date(), HelpMe.yyyy_MM_dd);
        return HelpMe.createDir(attachmentPath, dateDir);
    }

}

