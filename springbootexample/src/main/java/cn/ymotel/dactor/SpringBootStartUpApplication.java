package cn.ymotel.dactor;

import cn.ymotel.dactor.async.web.AsyncServletFilter;
import cn.ymotel.dactor.spring.annotaion.EnableAnnotation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;

//@ImportResource({"classpath*:/conf/**/*.xml"})
@SpringBootApplication
//@EnableAnnotation
public class SpringBootStartUpApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootStartUpApplication.class, args);
	}
//	@Bean   //相当于spring中<bean>标签
//	public FilterRegistrationBean AsyncServletFilterRegistration() {
//		FilterRegistrationBean bean = new FilterRegistrationBean();
//		bean.setFilter(new AsyncServletFilter());
//		bean.addUrlPatterns("/*");
//		return bean;
//
//	}

}
