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
import java.util.List;

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
                    BeanDefinitionBuilder UrlPatternbuilder = BeanDefinitionBuilder.rootBeanDefinition(DyanmicUrlPattern.class);
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
