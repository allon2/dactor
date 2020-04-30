package cn.ymotel.dactor.spring.annotaion;

import cn.ymotel.dactor.core.DyanmicUrlPattern;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

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

    /**
     *
     * @return 匹配的路径,AntPathMatcher
     */
    String[] urlPatterns()  default {};

    /**
     *
     * @return 排除的URL,AntPathMatcher
     */
    String[] excludeUrlPatterns() default {};
    String  chain() default "defaultchain";

    String  parent() default "";
    String[]  domains() default {};
    String  data()  default "";
    String eval() default "";
//    String[] methods() default { };
    RequestMethod[] methods() default { };

    /**
     *  返回view的相关参数
     * @return 标准格式为zip:download.zip json:
     */
    String  view() default "";
    /**
     *
     * @return 毫秒数 ，默认时间30s
     */
    long timeout() default  -1;
    Class<? extends DyanmicUrlPattern>[] urlPatternClass() default {};
}
