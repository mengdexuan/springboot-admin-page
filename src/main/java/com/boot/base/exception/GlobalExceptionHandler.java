package com.boot.base.exception;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.boot.base.Result;
import com.boot.base.ResultUtil;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 全局 验证 异常 处理 
 * @date 2017-12-20
 */
@ControllerAdvice(annotations = {RestController.class,Controller.class})
public class GlobalExceptionHandler {

    @Value("${spring.profiles.active:dev}")
    public String activeProfile;

    private static final String DEV_MODE = "dev";
    private static final String TEST_MODE = "test";

    private Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private String getMessage(Exception exception,ErrorStatus status){
        String msg = "";
        if (status!=null){
            msg += status.getMessage();
            msg += ":";
        }
        if (exception!=null){
            msg += exception.getMessage();
        }

        return msg;
    }

    /**
     * 构建返回错误
     * @param status
     * @param exception
     * @param data
     * @return
     */
    private Result failure(ErrorStatus status, Exception exception, Object data){
        String meesage = null;
        if(exception != null){
            meesage = this.getMessage(exception,status);
        }
        return ResultUtil.buildFailure(status.value(),meesage,data);
    }

    /**
     * 构建返回错误
     * @param status
     * @param data
     * @return
     */
    private Result failure(ErrorStatus status,Object data){
        return failure(status,null,data);
    }

    /**
     * 构建返回错误
     * @param status
     * @param exception
     * @return
     */
    private Result failure(ErrorStatus status,Exception exception){
        return failure(status,exception,null);
    }

    public static <T> Result<T> buildFailure(Integer code,String msg){
        return ResultUtil.build(Boolean.FALSE,msg,code,null);
    }

    /**
     * 构建返回错误
     * @param status
     * @return
     */
    private Result failure(ErrorStatus status){
        return ResultUtil.buildFailure(status);
    }


    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf,false));
    }


    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result handleException(MethodArgumentNotValidException e) {
        log.error(ErrorStatus.ILLEGAL_DATA.getMessage() + ":" + e.getMessage());
        List<Map<String, Object>> fields = new ArrayList<>();
        BindingResult bindingResult = e.getBindingResult();

        for (FieldError error : bindingResult.getFieldErrors()) {
            Map<String, Object> field = new HashMap<>();
            field.put("field", error.getField());
            field.put("message", error.getDefaultMessage());
            fields.add(field);
        }

        return failure(ErrorStatus.ILLEGAL_DATA, fields);
    }


    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result handleException(ResponseStatusException e) {
        log.error(ErrorStatus.METHOD_NOT_ALLOWED.getMessage() + ":" + e.getMessage(),e);

        return failure(ErrorStatus.METHOD_NOT_ALLOWED, ErrorStatus.METHOD_NOT_ALLOWED.getMessage());
    }



    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ConstraintViolationException.class)
    public Result handleValidationException(ConstraintViolationException e) {
        log.error(ErrorStatus.ILLEGAL_DATA.getMessage() + ":" + e.getMessage(),e);
        List<Map<String, Object>> fields = new ArrayList<>();
        for (ConstraintViolation<?> cv : e.getConstraintViolations()) {
            String fieldName = ((PathImpl) cv.getPropertyPath()).getLeafNode().asString();
            String message = cv.getMessage();
            Map<String, Object> field = new HashMap<>();
            field.put("field", fieldName);
            field.put("message", message);
            fields.add(field);
        }
        return failure(ErrorStatus.ILLEGAL_DATA, fields);
    }

    @ResponseBody
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result handleBindException(BindException e) {
        log.error(ErrorStatus.ILLEGAL_DATA.getMessage() + ":" + e.getMessage(),e);
        List<Map<String, Object>> fields = new ArrayList<>();
        for (FieldError error : e.getFieldErrors()) {
            Map<String, Object> field = new HashMap<>();
            field.put("field", error.getField());
            field.put("message", error.getDefaultMessage());
            fields.add(field);
        }
        return failure(ErrorStatus.ILLEGAL_DATA, fields);
    }

    @ResponseBody
    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result handleMultipartException(){
        return failure(ErrorStatus.MULTIPART_TOO_LARGE);
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(ErrorStatus.ILLEGAL_ARGUMENT.getMessage() + ":" + e.getMessage(),e);
        return failure(ErrorStatus.ILLEGAL_ARGUMENT,e);
    }

    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        log.error(ErrorStatus.MISSING_ARGUMENT.getMessage() + ":" + e.getMessage(),e);
        return failure(ErrorStatus.MISSING_ARGUMENT,e);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result handleMethodArgumentTypeMismatchExceptionException(
            MethodArgumentTypeMismatchException e) {

        log.error(ErrorStatus.ILLEGAL_ARGUMENT_TYPE.getMessage() + ":" + e.getMessage(),e);
        return failure(ErrorStatus.ILLEGAL_ARGUMENT_TYPE,e);
    }



    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {

        log.error(ErrorStatus.METHOD_NOT_ALLOWED.getMessage() + ":" + e.getMessage(),e);
        return failure(ErrorStatus.METHOD_NOT_ALLOWED,e);
    }



    @ResponseBody
    @ExceptionHandler(GlobalServiceException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result handleServiceException(GlobalServiceException e) {
        log.error(ErrorStatus.SERVICE_EXCEPTION.getMessage() + ":" + e.getMessage(),e);
        if(e.getCode() == null){
            return failure(ErrorStatus.SERVICE_EXCEPTION,e);
        }else{
            return buildFailure(e.getCode(),e.getMessage());
        }
    }


    @ResponseBody
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result handleServiceException(NullPointerException e) {
        StackTraceElement trace = e.getStackTrace()[0];
        return failure(ErrorStatus.SERVICE_EXCEPTION,trace);
    }


    @ResponseBody
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result handleIllegalStateException(IllegalStateException e) {
        log.error("exception",e);
        return failure(ErrorStatus.ILLEGAL_STATE,e);
    }



    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleException(Exception e) {
        log.error(ErrorStatus.INTERNAL_SERVER_ERROR.getMessage() + ":" + e.getMessage(),e);
        return failure(ErrorStatus.INTERNAL_SERVER_ERROR,e);
    }

}
