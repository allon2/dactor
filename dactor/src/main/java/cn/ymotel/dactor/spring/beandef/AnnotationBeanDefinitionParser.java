/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cn.ymotel.dactor.spring.beandef;

import cn.ymotel.dactor.core.ActorTransactionCfg;
import cn.ymotel.dactor.spring.annotaion.ActorCfg;
import cn.ymotel.dactor.spring.annotaion.ActorCfgBeanFactoryPostProcessor;
import cn.ymotel.dactor.spring.annotaion.AnnotationBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.util.Set;

/**
 * Created by jychai on 17/6/28.
 */
public class AnnotationBeanDefinitionParser implements BeanDefinitionParser {

//    private final Class<?> beanClass= AnnotationBean.class;
    private final Class<?> beanClass= ActorCfgBeanFactoryPostProcessor.class;



    public BeanDefinition parse(Element element, ParserContext parserContext) {


//        Set<BeanDefinitionHolder> processorDefinitions = AnnotationConfigUtils.registerAnnotationConfigProcessors(parserContext.getReaderContext().getRegistry(), element);
//        ActorClassPathBeanDefinitionScanner scanner=new ActorClassPathBeanDefinitionScanner(parserContext.getRegistry());
//        scanner.addIncludeFilter(new AnnotationTypeFilter(ActorCfg.class));
//        Set<BeanDefinitionHolder> holderSet= scanner.doScan("cn.ymotel");
//        for(java.util.Iterator iter=holderSet.iterator();iter.hasNext();){
//            BeanDefinitionHolder holder=(BeanDefinitionHolder)iter.next();
//            if (holder.getBeanDefinition() instanceof AnnotatedBeanDefinition) {
//              ActorCfg cfg=  ((AnnotatedBeanDefinition) holder.getBeanDefinition()).getMetadata()..get(ActorCfg.class);
////                ((AnnotatedBeanDefinition)holder.getBeanDefinition()).
//            }
//
//            System.out.println(holder);
//        }
        return parse(element, parserContext, beanClass);
    }
//    public void beanBuilder(ActorCfg actorCfg, String beanid,ParserContext parserContext){
//        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ActorTransactionCfg.class);
////        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
////        rootBeanDefinition.setBeanClass(ActorTransactionCfg.class);
////        builder.addPropertyValue("beginBeanId", "ttt");
////        System.out.println("addPropertyValuu=e");
//        builder.addPropertyValue("beginBeanId", beanid);
//        if(StringUtils.hasText(actorCfg.urlPatterns())) {
//            builder.addPropertyValue("urlPattern", actorCfg.urlPatterns());
//        }
//        if(StringUtils.hasText(actorCfg.chain())) {
//
//            builder.addPropertyReference("chain", actorCfg.chain());
//        }
//        if(StringUtils.hasText(actorCfg.parent())) {
//
//            builder.addPropertyReference("parent", actorCfg.parent());
//        }
//        builder.addPropertyReference("global", "ActorGlobal");
//        String cfgid=null;
//        if(actorCfg.id()==null|| actorCfg.id().equals("")){
//            cfgid=parserContext.getReaderContext().generateBeanName(builder.getRawBeanDefinition());
//        }else{
//            cfgid= actorCfg.id();
//        }
//        builder.addPropertyValue("id", cfgid);
//        builder.setLazyInit(false);
////        BeanDefinitionHolder holder = new BeanDefinitionHolder(builder.getRawBeanDefinition(), cfgid);
////        BeanDefinitionReaderUtils.registerBeanDefinition(holder, parserContext.getRegistry());
//        System.out.println("in annotaton");
//        parserContext.getRegistry().registerBeanDefinition(cfgid, builder.getRawBeanDefinition());
//
//
//
//
//    }
    private BeanDefinition parse(Element element, ParserContext parserContext, Class<?> beanClass) {
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        rootBeanDefinition.setBeanClass(beanClass);
        rootBeanDefinition.setLazyInit(false);

//        rootBeanDefinition.getPropertyValues().addPropertyValue("parserContext", parserContext);
        String generatedBeanName = beanClass.getName();
        parserContext.getRegistry().registerBeanDefinition(generatedBeanName, rootBeanDefinition);
        return rootBeanDefinition;
    }


}
