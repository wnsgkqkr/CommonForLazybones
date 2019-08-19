package com.cfl.config;

import com.cfl.interceptor.NetworkAclInterceptor;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private NetworkAclInterceptor networkAclInterceptor;
    
    @Value("${tomcat.ajp.protocol}")
    String tomcatAjpProtocal;
    
    @Value("${tomcat.ajp.port}")
    String tomcatAjpPort;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(networkAclInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(wildcardPathVariableHandlerMethodArgumentResolver());
    }

    @Bean
    public WildcardPathVariableHandlerMethodArgumentResolver wildcardPathVariableHandlerMethodArgumentResolver() {
        return new WildcardPathVariableHandlerMethodArgumentResolver();
    }
    
    
	@Bean
	public ServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		tomcat.addAdditionalTomcatConnectors(createAjpConnector());
		return tomcat;
	}

	
	
	private Connector createAjpConnector() {
		Connector ajpConnector = new Connector(tomcatAjpProtocal);
		ajpConnector.setPort(Integer.parseInt(tomcatAjpPort));
		ajpConnector.setSecure(false);
		ajpConnector.setAllowTrace(false);
		ajpConnector.setScheme("http");
		return ajpConnector;
	}
}
