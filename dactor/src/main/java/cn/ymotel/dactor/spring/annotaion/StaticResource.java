package cn.ymotel.dactor.spring.annotaion;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(value = StaticResourceImportSelector.class)
public @interface StaticResource {
    @AliasFor("locations")
    String[] value() default {};

    String[] locations() default {};
}
