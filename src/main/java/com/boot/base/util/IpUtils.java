package com.boot.base.util;

import org.apache.commons.lang3.text.StrTokenizer;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * #Nginx 设置
 * location  ~  ^/static {
 * proxy_pass  ....;
 * proxy_set_header X-Forward-For $remote_addr ;
 * }
 * 这段配置是在前端Nginx反向代理上的（其他反向代理请自行搜索），这段配置
 *
 * 作的事情是将X-Forward-For替换为remote_addr，再将X-Forward-For在内网
 * 各服务器间安全传输。
 *
 * 这里我再针对TCP/IP多做一些解释，众所周知TCP/IP建立连接时需要三次握手的，并且，只有知道了client端请求的IP地址，server端的数据才能返回给client，所以client想要获取到数据就必须提供真实的IP（DDOS攻击除外），而request.getRemoteAddr()获取的就是用户最真实的IP。
 *
 * 那么为什么不直接使用使用request.getRemoteAddr()这个方法呢？
 *
 * 如果没有反向代理的话当然可行。但是出于安全原因，现在大多数的服务都使用代理服务器（如Nginx，代理服务器可以理解为用户和服务器之间的中介，双方都可信任。），而用户对代理服务器发起的HTTP请求，代理服务器对服务集群中的真实部署的对应服务进行“二次请求”，所以最终获取的IP是代理服务器在内网中的ip地址，如192.168.xx.xx/10.xx.xx.xx等等。
 *
 * 所以在使用了反向代理的情况下，request.getRemoteAddr()获取的是反响代理在内网中的ip地址。所以在反向代理中将X-Forward-For替换为remote_addr，即，真实的IP地址。之后在内网中获取的x-forwarded-for便是真实的ip地址了。
 *
 *
 *
 * Nginx部分：
 * location  ~  ^/static {
 * proxy_pass  ....;
 * proxy_set_header X-Forward-For $remote_addr ;
 * }
 *
 *
 * @author mengdexuan on 2020/2/19 9:47.
 */
public class IpUtils {

	public static final String _255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	public static final Pattern pattern = Pattern.compile("^(?:" + _255 + "\\.){3}" + _255 + "$");

	public static String longToIpV4(long longIp) {
		int octet3 = (int) ((longIp >> 24) % 256);
		int octet2 = (int) ((longIp >> 16) % 256);
		int octet1 = (int) ((longIp >> 8) % 256);
		int octet0 = (int) ((longIp) % 256);
		return octet3 + "." + octet2 + "." + octet1 + "." + octet0;
	}

	public static long ipV4ToLong(String ip) {
		String[] octets = ip.split("\\.");
		return (Long.parseLong(octets[0]) << 24) + (Integer.parseInt(octets[1]) << 16)
				+ (Integer.parseInt(octets[2]) << 8) + Integer.parseInt(octets[3]);
	}

	public static boolean isIPv4Private(String ip) {
		long longIp = ipV4ToLong(ip);
		return (longIp >= ipV4ToLong("10.0.0.0") && longIp <= ipV4ToLong("10.255.255.255"))
				|| (longIp >= ipV4ToLong("172.16.0.0") && longIp <= ipV4ToLong("172.31.255.255"))
				|| longIp >= ipV4ToLong("192.168.0.0") && longIp <= ipV4ToLong("192.168.255.255");
	}

	public static boolean isIPv4Valid(String ip) {
		return pattern.matcher(ip).matches();
	}

	public static String getIpFromRequest(HttpServletRequest request) {
		String ip;
		boolean found = false;
		if ((ip = request.getHeader("x-forwarded-for")) != null) {
			StrTokenizer tokenizer = new StrTokenizer(ip, ",");
			while (tokenizer.hasNext()) {
				ip = tokenizer.nextToken().trim();
				if (isIPv4Valid(ip) && !isIPv4Private(ip)) {
					found = true;
					break;
				}
			}
		}
		if (!found) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
