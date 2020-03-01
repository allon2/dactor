package cn.ymotel.dactor.spring.annotaion;

import cn.ymotel.dactor.core.ActorTransactionCfg;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

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
        if(StringUtils.hasText(actorCfg.urlPatterns())) {
            builder.addPropertyValue("urlPattern", actorCfg.urlPatterns());
        }
        if(StringUtils.hasText(actorCfg.chain())) {

            builder.addPropertyReference("chain", actorCfg.chain());
        }
        if(StringUtils.hasText(actorCfg.parent())) {

            builder.addPropertyReference("parent", actorCfg.parent());
        }
//        builder.addPropertyReference("global", "ActorGlobal");
        String cfgid=null;
        if(actorCfg.id()==null|| actorCfg.id().equals("")){
            cfgid=name;
        }else{
            cfgid= actorCfg.id();
        }
        builder.addPropertyValue("domain", actorCfg.domain());

        builder.addPropertyValue("id", cfgid);
        builder.setLazyInit(false);
        defaultListableBeanFactory.registerBeanDefinition(cfgid,builder.getRawBeanDefinition());
    }
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
         this.registry=registry;
    }
}
