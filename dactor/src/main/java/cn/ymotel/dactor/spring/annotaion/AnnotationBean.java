//package cn.ymotel.dactor.spring.annotaion;
//
//import cn.ymotel.dactor.core.ActorTransactionCfg;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.config.BeanDefinitionHolder;
//import org.springframework.beans.factory.support.*;
//import org.springframework.beans.factory.xml.ParserContext;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.support.GenericApplicationContext;
//import org.springframework.util.StringUtils;
//
//import java.util.Map;
//
//public class AnnotationBean implements ApplicationContextAware, InitializingBean  {
//    private ApplicationContext applicationContext;
//    private ParserContext parserContext;
//
//    public void setParserContext(ParserContext parserContext) {
//        this.parserContext = parserContext;
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext=applicationContext;
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//
//       Map map= applicationContext.getBeansWithAnnotation(ActorCfg.class);
////        GenericApplicationContext context = new GenericApplicationContext();
////        context.setParent(applicationContext);
//        for(java.util.Iterator iter=map.entrySet().iterator();iter.hasNext();){
//            Map.Entry entry=(Map.Entry)iter.next();
//            ActorCfg actorCfg = entry.getValue().getClass().getAnnotation(ActorCfg.class);
//            beanBuilder(actorCfg,(String)entry.getKey(),null);
//
//        }
//        /**
//         * 无此方法，无法执行ActorTransactionCfg的afterPropertiesSet方法
//         */
//        Map t=applicationContext.getBeansOfType(ActorTransactionCfg.class);
//    }
//    public void beanBuilder(ActorCfg actorCfg, String beanid, GenericApplicationContext context){
//        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ActorTransactionCfg.class);
//
//         builder.addPropertyValue("beginBeanId", beanid);
//        if(actorCfg.urlPatterns()==null&&actorCfg.urlPatterns().length!=0) {
//             builder.addPropertyValue("urlPattern", actorCfg.urlPatterns());
//         }
//        if(StringUtils.hasText(actorCfg.chain())) {
//
//            builder.addPropertyReference("chain", actorCfg.chain());
//        }
//        if(StringUtils.hasText(actorCfg.parent())) {
//
//            builder.addPropertyReference("parent", actorCfg.parent());
//        }
////        builder.addPropertyReference("global", "ActorGlobal");
//        String cfgid=null;
//        if(actorCfg.id()==null|| actorCfg.id().equals("")){
//            cfgid=parserContext.getReaderContext().generateBeanName(builder.getRawBeanDefinition());
//        }else{
//            cfgid= actorCfg.id();
//        }
//        builder.addPropertyValue("id", cfgid);
//
//        builder.addPropertyValue("domains", actorCfg.domains());
//
//        if(actorCfg.data()!=null) {
//            builder.addPropertyValue("data", actorCfg.data());
//        }
//        if(actorCfg.eval()!=null) {
//            builder.addPropertyValue("eval", actorCfg.eval());
//        }
//        builder.setLazyInit(false);
////        System.out.println("in annotaton");
//        parserContext.getRegistry().registerBeanDefinition(cfgid, builder.getRawBeanDefinition());
//
//
//    }
//    private static DefaultListableBeanFactory unwrapDefaultListableBeanFactory(BeanDefinitionRegistry registry) {
//        if (registry instanceof DefaultListableBeanFactory) {
//            return (DefaultListableBeanFactory) registry;
//        }
//        else if (registry instanceof GenericApplicationContext) {
//            return ((GenericApplicationContext) registry).getDefaultListableBeanFactory();
//        }
//        else {
//
//            return null;
//        }
//    }
//}
