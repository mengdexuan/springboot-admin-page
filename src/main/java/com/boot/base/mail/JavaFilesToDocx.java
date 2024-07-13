package com.boot.base.mail;

/**
 * @author JackMeng on 2024-07-13 20:54.
 */
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JavaFilesToDocx {

    public static void main(String[] args) {

        // 设定要搜索的目录路径
        String directoryPath = "/Users/jackmeng/Desktop/project/wukong-backend/biz-ctrl/biz-app";
        // 设定输出的docx文件路径
        String outputDocxPath = "/Users/jackmeng/Desktop/temp/biz-app 服务源码.docx";


        try {
            // 创建一个新的docx文档
            XWPFDocument document = new XWPFDocument();

            // 获取目录下所有的java文件
            List<Path> javaFiles = Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .toList();

            for (Path javaFile : javaFiles) {
                // 读取每个java文件的内容
                List<String> lines = Files.readAllLines(javaFile, StandardCharsets.UTF_8);

                // 创建一个新的段落
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun titleRun = paragraph.createRun();
                titleRun.setBold(true);
                titleRun.setFontSize(14);
                titleRun.setText(javaFile.getFileName().toString());
                titleRun.addCarriageReturn();
                titleRun.addCarriageReturn();

                for (String line : lines) {
                    XWPFRun run = paragraph.createRun();
                    run.setFontFamily("Courier New");
                    run.setFontSize(12);
                    run.setText(line);
                    run.addCarriageReturn();
                }

                // 添加额外的换行以分隔文件
                XWPFRun separatorRun = paragraph.createRun();
                separatorRun.addCarriageReturn();
                separatorRun.addCarriageReturn();
            }

            // 将文档写入到文件中
            try (FileOutputStream out = new FileOutputStream(outputDocxPath)) {
                document.write(out);
            }

            // 关闭文档
            document.close();

            System.out.println("所有的Java文件已成功写入到 " + outputDocxPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}