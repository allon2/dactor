package cn.ymotel.dactor.springboot;

import cn.ymotel.dactor.action.BeginActor;
import cn.ymotel.dactor.action.EndActor;
import cn.ymotel.dactor.action.FinishActor;
import cn.ymotel.dactor.action.PlaceholderActor;
import cn.ymotel.dactor.core.ActorChainCfg;
import cn.ymotel.dactor.core.ActorGlobalCfg;
import cn.ymotel.dactor.core.ActorTransactionCfg;
import cn.ymotel.dactor.core.MessageDispatcher;
import cn.ymotel.dactor.core.disruptor.MessageRingBufferDispatcher;
import cn.ymotel.dactor.core.disruptor.RingBufferMonitorThread;
import cn.ymotel.dactor.message.DefaultResolveMessage;
import cn.ymotel.dactor.response.TransportResponseViewActor;
import cn.ymotel.dactor.spring.annotaion.ActorCfgBeanFactoryPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConditionalOnClass(MessageDispatcher.class)
@EnableConfigurationProperties(DactorProperties.class)
@ConditionalOnProperty(name = "dactor.enabled", matchIfMissing = true)
public class DactorAutoConfiguration {

    @Autowired
    private DactorProperties properties=null;
    @Autowired
    private  ApplicationContext applicationContext=null;

//    public DactorAutoConfiguration( ApplicationContext applicationContext) {
////        this.properties=properties;
//        this.applicationContext = applicationContext;
//    }

    @Bean
    @ConditionalOnMissingBean
    public ActorGlobalCfg getActorGlobalCfg(){
        return new ActorGlobalCfg();
    }

    @Bean(name="beginActor")
    @ConditionalOnMissingBean
    public BeginActor getBeginActor(){
        return new BeginActor();
    }
    @Bean(name="FinishActor")
    @ConditionalOnMissingBean
    public FinishActor getFinishActor(){
        return new FinishActor();
    }
    @Bean(name="endActor")
    @ConditionalOnMissingBean
    public EndActor getEndActor(){
        return new EndActor();
    }
    @Bean(name="placeholderActor")
    @ConditionalOnMissingBean
    public PlaceholderActor getPlaceholderActor(){
        return new PlaceholderActor();
    }
    @Bean
    @ConditionalOnMissingBean
    public MessageDispatcher getMessageDispatcher(){
        MessageRingBufferDispatcher messageDispatcher=new MessageRingBufferDispatcher();
        messageDispatcher.setChecktime(properties.getChecktime());
        messageDispatcher.setMaxsize(properties.getThreadmin());
        messageDispatcher.setMaxsize(properties.getThreadmax());
        messageDispatcher.setBufferSize(properties.getBufferSize());
        if(properties.isMonitor()){
            RingBufferMonitorThread monitorThread=new RingBufferMonitorThread();
            monitorThread.setMessageRingBufferDispatcher(messageDispatcher);
            monitorThread.start();
        }
        return messageDispatcher;
    }
    @Bean(name="TransportResponseViewActor")
    @ConditionalOnMissingBean
    public TransportResponseViewActor getTransportResponseViewActor(){
        return new TransportResponseViewActor();
    }
//    @Bean(name="chainactor")
//    @ConditionalOnMissingBean(name = "chainactor")
    public ActorTransactionCfg createDefaultChainActor() {
        ActorTransactionCfg cfg=new ActorTransactionCfg();
        cfg.setHandleException(true);
        cfg.setEndBeanId("TransportResponseViewActor");
        cfg.setId("chainactor");
        Map map=new HashMap<>();
        List list=new ArrayList<>();
        Map property = new HashMap();
        property.put("fromBeanId","beginActor");
        property.put("toBeanId","placeholderActor");
        property.put("conditon","");
        list.add(property);
        map.put("beginActor",list);
        cfg.setSteps(map);
        return cfg;
    }
    @Bean(name="defaultchain")
    @ConditionalOnMissingBean
    public ActorChainCfg creatDefaultChain(){
        ActorChainCfg cfg=new ActorChainCfg();
        cfg.setId("defaultchain");
        List ls=new ArrayList();
        ActorTransactionCfg cfg1=  createDefaultChainActor();
        cfg1.setApplicationContext(this.applicationContext);
        try {
            cfg1.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ls.add(cfg1);
        cfg.setChain(ls);
        return cfg;
    }
    @Bean(name="DefaultResolveMessage")
    @ConditionalOnMissingBean
    public DefaultResolveMessage getDefaultResolveMessage(){
        return new DefaultResolveMessage();
    }
    @Bean
    @ConditionalOnMissingBean
    public static ActorCfgBeanFactoryPostProcessor createActorCfgBeanFactoryPostProcessor(){
        return new ActorCfgBeanFactoryPostProcessor();
    }
}