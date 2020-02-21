package cn.ymotel.dactor.spring.annotaion;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
//@Import(value = ActorCfgBeanDefinitionRegister.class)
@Component
public @interface ActorCfg {
    @AliasFor("id")
    String value() default "";

    @AliasFor("value")
    String  id() default "";

    String urlPatterns()  default "";
    String  chain() default "";

    String  parent() default "";
    String  domain() default "";
}
