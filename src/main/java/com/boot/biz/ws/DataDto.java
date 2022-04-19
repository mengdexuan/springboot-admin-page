package com.boot.biz.ws;

import lombok.Data;

import java.io.InputStream;

/**
 * @author mengdexuan on 2022/3/30 11:38.
 */
@Data
public class DataDto {
    private Process process;
    private InputStream inputStream;
}
