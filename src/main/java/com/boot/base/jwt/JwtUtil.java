package com.boot.base.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mengdexuan on 2020/5/9 8:52.
 */
@Slf4j
public class JwtUtil {

	/**
	 * 失效时间： 1 天 =24*3600*1000 ms
	 */
	public static final long  EXPIRATION_TIME  =1*24*3600*1000;
	/**
	 * 私钥
	 */
	public static final String  SECRET  ="http://www.itangquan.com";
	/**
	 * token 前缀
	 */
//	public static final String  TOKEN_PREFIX  ="Bearer ";
	public static final String  TOKEN_PREFIX  ="";
	/**
	 * Authorization header
	 */
	public static final String  HEADER_STRING  ="Authorization";
	/**
	 * 保存的数据字段名称
	 */
	public static final String  USER_ID  ="userId";


	/**
	 *
	 * @param userId	用户ID
	 * @param expirationTime	token 失效时间（ms）
	 * @return
	 */
	public static String generateToken(Long userId,Long expirationTime) {

		HashMap<String, Object> map = new HashMap<>();

		//可以将自定义相关的数据放入Map中

		map.put(USER_ID, userId);

		String jwt = Jwts.builder()

				.setClaims(map)

				.setExpiration(new Date(System.currentTimeMillis() +  expirationTime))

				.signWith(SignatureAlgorithm.HS512,  SECRET)

				.compact();

		//jwt前面一般都会加Bearer

		return  TOKEN_PREFIX  +jwt;

	}


	public static Long validateTokenAndGetUserId(String token) {

		log.info("请求token:" + token);

		if (token != null) {
			// 解析令牌token.
			try {
				Map<String, Object> body = Jwts.parser()
						.setSigningKey(SECRET)
						.parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
						.getBody();

				long userId = Long.parseLong(body.get(USER_ID).toString());

				return userId;
			} catch (Exception e) {
				log.error("token 校验失败！",e);
			}

		} else {
			log.error("用户未登录！");
		}

		return null;
	}

}
