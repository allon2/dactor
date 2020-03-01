package cn.ymotel.dactor.spring.annotaion;

import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(value = ActorCfgBeanFactoryPostProcessor.class)
@EnableAsync
public @interface EnableAnnotation {
}
