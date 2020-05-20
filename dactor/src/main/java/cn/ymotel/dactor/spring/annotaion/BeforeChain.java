package cn.ymotel.dactor.spring.annotaion;

import cn.ymotel.dactor.core.DyanmicUrlPattern;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
@Order
@BaseChain
public @interface BeforeChain {
    @AliasFor(value = "value",annotation = Component.class)
    String  value() default "";

    @AliasFor(value = "value",annotation = Component.class)
    String   id() default "";
    /**
     * 数字越大，越先执行
     * @return 优先级
     */

    @AliasFor(value = "value",annotation = Order.class)
    int order() default 1;

    @AliasFor(value = "chain",annotation = BaseChain.class)
    String[] chain() default {"defaultchain"};

}
