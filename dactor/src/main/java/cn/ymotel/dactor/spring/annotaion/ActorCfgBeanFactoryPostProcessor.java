package cn.ymotel.dactor.spring.annotaion;

import cn.ymotel.dactor.core.ActorTransactionCfg;
import cn.ymotel.dactor.core.DyanmicUrlPattern;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActorCfgBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    private BeanDefinitionRegistry registry;
    private BeanNameGenerator beanNameGenerator=new DefaultBeanNameGenerator();
     @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        DefaultListableBeanFactory defaultListableBeanFactory
                = (DefaultListableBeanFactory) beanFactory;
          String[] beans = beanFactory.getBeanDefinitionNames();

        for (String s : beans) {
            Class<?> beanType = beanFactory.getType(s);
            ActorCfg actorCfg = AnnotationUtils.findAnnotation(beanType,
                    ActorCfg.class);
            if (actorCfg != null) {
                RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
                rootBeanDefinition.setBeanClass(ActorTransactionCfg.class);
                 String name = beanNameGenerator.generateBeanName(rootBeanDefinition,registry);
                beanBuilder(actorCfg,s,name,defaultListableBeanFactory);

            }
        }
    }
    public void beanBuilder(ActorCfg actorCfg, String beanid,String name, DefaultListableBeanFactory defaultListableBeanFactory ){
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ActorTransactionCfg.class);

        builder.addPropertyValue("beginBeanId", beanid);
        if(actorCfg.urlPatterns()!=null&&actorCfg.urlPatterns().length!=0) {
            builder.addPropertyValue("urlPattern", actorCfg.urlPatterns());
        }
        if(actorCfg.methods()!=null&&actorCfg.methods().length!=0) {
            String[] methods=new String[actorCfg.methods().length];
            for(int i=0;i<actorCfg.methods().length;i++){
                methods[i]=actorCfg.methods()[i].name();
            }

            builder.addPropertyValue("methods", methods);
        }
        if(actorCfg.excludeUrlPatterns()!=null&&actorCfg.excludeUrlPatterns().length!=0) {
            builder.addPropertyValue("excludeUrlPattern", actorCfg.excludeUrlPatterns());
        }
        if(actorCfg.httpStatus()!=null&&actorCfg.httpStatus().length!=0){
            builder.addPropertyValue("httpStatus", actorCfg.httpStatus());
        }
        if(actorCfg.dispatcherType()!=null&&actorCfg.dispatcherType().length!=0){
            String[] dispatcherTypes=new String[actorCfg.dispatcherType().length];
            for(int i=0;i<actorCfg.dispatcherType().length;i++) {
                dispatcherTypes[i]=actorCfg.dispatcherType()[i].name();
            }
            builder.addPropertyValue("dispatcherTypes", dispatcherTypes);

        }
        if(StringUtils.hasText(actorCfg.chain())) {

            builder.addPropertyReference("chain", actorCfg.chain());
        }
        if(StringUtils.hasText(actorCfg.parent())) {

            builder.addPropertyReference("parent", actorCfg.parent());
        }
        if(StringUtils.hasText(actorCfg.view())) {
            Map viewmap=new HashMap();
            viewmap.put("success",actorCfg.view());
            builder.addPropertyValue("results", viewmap);
        }
//        builder.addPropertyReference("global", "ActorGlobal");
        String cfgid=null;
        if(actorCfg.id()==null|| actorCfg.id().equals("")){
            cfgid=name;
        }else{
            cfgid= actorCfg.id();
        }
        builder.addPropertyValue("domains", actorCfg.domains());

        builder.addPropertyValue("id", cfgid);

        if(actorCfg.timeout()>0){
            builder.addPropertyValue("timeout", actorCfg.timeout());
        }
        {
            List urlPatternList = new ManagedList<>();

            if (actorCfg.urlPatternClass() == null || actorCfg.urlPatternClass().length == 0) {
                /**
                 * 将自身加入
                 */

                urlPatternList.add(new RuntimeBeanReference(beanid));

            } else {
                for (int i = 0; i < actorCfg.urlPatternClass().length; i++) {
                    BeanDefinitionBuilder UrlPatternbuilder = BeanDefinitionBuilder.rootBeanDefinition(actorCfg.urlPatternClass()[i]);
                    String UrlPatternname = beanNameGenerator.generateBeanName(UrlPatternbuilder.getRawBeanDefinition(), registry);
                    defaultListableBeanFactory.registerBeanDefinition(UrlPatternname, UrlPatternbuilder.getRawBeanDefinition());
                    urlPatternList.add(new RuntimeBeanReference(UrlPatternname));
                }


            }
            builder.addPropertyValue("dyanmicUrlPatterns", urlPatternList);
        }


        builder.setLazyInit(false);


        defaultListableBeanFactory.registerBeanDefinition(cfgid,builder.getRawBeanDefinition());
    }
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
         this.registry=registry;
    }
}
