package cn.ymotel.dactor.spring.annotaion;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(value = ActorCfgBeanFactoryPostProcessor.class)

public @interface EnableAnnotation {
}
