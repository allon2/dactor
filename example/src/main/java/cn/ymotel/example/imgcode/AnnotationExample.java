package cn.ymotel.example.imgcode;

import cn.ymotel.dactor.action.Actor;
import cn.ymotel.dactor.core.UrlMapping;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.spring.annotaion.ActorCfg;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

//@Component
@ActorCfg(urlPatterns = "/aaa")
public class AnnotationExample implements Actor, InitializingBean, ApplicationContextAware {
    @Override
    public Object HandleMessage(Message message) throws Exception {
        System.out.println("bbb-aaaa");
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("urlmapping"+UrlMapping.getMapping());
    }
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=this.applicationContext;
    }
}
