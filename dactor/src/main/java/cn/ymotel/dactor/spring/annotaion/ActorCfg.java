package cn.ymotel.dactor.spring.annotaion;

import cn.ymotel.dactor.core.DyanmicUrlPattern;
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

    String[] urlPatterns()  default {};
    String  chain() default "defaultchain";

    String  parent() default "";
    String  domain() default "";
    String  data()  default "";
    String eval() default "";
    long timeout() default  -1;
    Class<DyanmicUrlPattern>[] urlPatternClass() default {};
}
