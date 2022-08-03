package com.boot.config;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.boot.base.util.AesUtil;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author mengdexuan on 2022/8/1 11:08.
 */
@Component
@WebFilter(filterName="handleFilter",urlPatterns="/*")
public class HandleFilter extends OncePerRequestFilter {

    private static byte[] key = "0123456789ABCDEF".getBytes();
    private static SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        String aesHeader = httpServletRequest.getHeader("AES");
        if (StrUtil.isNotEmpty(aesHeader)){
            ModifyParametersWrapper mParametersWrapper = new ModifyParametersWrapper(httpServletRequest);
            Map<String, String[]> parameterMap = new HashMap<>(mParametersWrapper.getParameterMap());

            Map<String, String[]> parameterMap2 = Maps.newHashMap();

            for (Map.Entry<String, String[]> entry:parameterMap.entrySet()){
                String key = entry.getKey();
                String[] val = entry.getValue();

                String[] arr = new String[1];
                //进行参数解密
                arr[0] = aes.decryptStr(val[0], CharsetUtil.CHARSET_UTF_8);

                parameterMap2.put(key,arr);
            }
            //将参数放入 request
            mParametersWrapper.setParameterMap(parameterMap2);


            NativeWebRequest webRequest = new ServletWebRequest(httpServletRequest);
            Map<String, String> map = (Map<String, String>) webRequest.getAttribute(
                    HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE,
                    RequestAttributes.SCOPE_REQUEST);

            //使用新的 request 往下传递
            filterChain.doFilter(mParametersWrapper, httpServletResponse);
        }else {
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        }


    }



    public static void main(String[] args) {
        String str = AesUtil.aesDecrypt("TZsroXQE98ZgtUMeOv1Hyw==");

        System.out.println(str);
    }


    public static void main2(String[] args) {
        String content = "123";


//加密为16进制表示
        String encryptHex = aes.encryptHex(content);

        encryptHex = "TZsroXQE98ZgtUMeOv1Hyw==";

        byte[] temp = aes.decrypt(encryptHex);

        System.out.println(String.valueOf(temp));
//解密为字符串
        String decryptStr = aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);

        AES test = new AES();
        System.out.println(encryptHex);
        System.out.println(decryptStr);

    }



}
