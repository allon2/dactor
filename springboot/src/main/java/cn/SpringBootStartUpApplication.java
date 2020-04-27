package cn;

import cn.ymotel.dactor.async.web.AsyncServletFilter;
import cn.ymotel.dactor.spring.annotaion.EnableAnnotation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;

@ImportResource({"classpath*:/conf/**/*.xml"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//@ServletComponentScan
@EnableAsync
@EnableAnnotation
public class SpringBootStartUpApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootStartUpApplication.class, args);
	}
	@Bean   //相当于spring中<bean>标签
	public FilterRegistrationBean testFilterRegistration() {
		FilterRegistrationBean bean = new FilterRegistrationBean();
		bean.setFilter(new AsyncServletFilter());
		bean.addUrlPatterns("/*");
		return bean;
//		FilterRegistrationBean registration = new FilterRegistrationBean<>();
//		registration.setFilter(new AsyncServletFilter());
//		registration.addUrlPatterns("/*");//配置过滤路径
////		registration.addInitParameter("paramName", "paramValue");//添加初始值
//		registration.setName("AsyncServletFilter");//设置filter名称
//		registration.setOrder(1);//请求中过滤器执行的先后顺序，值越小越先执行
//		return registration;
	}

}
