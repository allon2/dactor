package cn.ymotel.dactor.springboot;

import cn.ymotel.dactor.action.ViewResolveActor;
import cn.ymotel.dactor.async.web.AsyncServletFilter;
import cn.ymotel.dactor.async.web.MessageSourceFilter;
import cn.ymotel.dactor.async.web.view.*;
import cn.ymotel.dactor.response.ResponseViewType;
import cn.ymotel.dactor.response.TransportResponseViewActor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(javax.servlet.jsp.jstl.core.Config.class)
@AutoConfigureAfter({DactorAutoConfiguration.class})
@ConditionalOnProperty(name = "dactor.enabled", matchIfMissing = true)
public class DactorMessageFilterConfiguration {

    @Bean
    @ConditionalOnClass(javax.servlet.jsp.jstl.core.Config.class)
    public MessageSourceFilter MessageSourceFilter() {
        MessageSourceFilter filter=new MessageSourceFilter();
        return filter;
    }

}