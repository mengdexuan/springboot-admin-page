package com.boot.biz.ws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author mengdexuan on 2022/4/19 16:15.
 */
@Component
public class LogPathBean implements CommandLineRunner {

    @Value("${logging.info-log}")
    private String infoLogTemp;
    public static String infoLog;

    @Value("${logging.error-log}")
    private String errorLogTemp;
    public static String errorLog;



    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        infoLog = infoLogTemp;
        errorLog = errorLogTemp;
    }
}
