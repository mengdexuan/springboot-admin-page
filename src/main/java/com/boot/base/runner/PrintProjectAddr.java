package com.boot.base.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 项目启动后，打印出访问地址
 * @author mengdexuan on 2021/5/7 15:13.
 */
@Slf4j
@Component
public class PrintProjectAddr implements CommandLineRunner {

    @Value("${server.port}")
    private Integer port;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("");
        log.info("http://localhost:"+port+contextPath);
        log.info("http://localhost:"+port+contextPath+"/doc.html");
        log.info("");
    }


}
