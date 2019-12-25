package com.boot.shutdown;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mengdexuan on 2019/6/17 22:16.
 */
@Configuration
public class ServletWebServerFactoryConfig {

	@Autowired
	private GracefulShutdownTomcat gracefulShutdownTomcat;

	@Bean
	public ServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		tomcat.addConnectorCustomizers(gracefulShutdownTomcat);
		return tomcat;
	}

}
