package com.boot.base.exception;

/**
 * 平台服务层异常，主要是在业务数据或者状态异常时使用
 * @date 2017.12.25
 */
public class GlobalServiceException extends RuntimeException{

    private Integer code;

    public GlobalServiceException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public GlobalServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public GlobalServiceException(String message) {
        super(message);
    }

    public static void onCondition(boolean condition,String message){
        if(condition){
            throw new GlobalServiceException(message);
        }
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
