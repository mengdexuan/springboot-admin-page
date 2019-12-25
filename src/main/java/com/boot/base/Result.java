package com.boot.base;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    // 返回是否成功
    private Boolean success = false;

    // 返回信息
    private String msg = "操作成功";

    private Integer code = 0;

    private T data;

}
