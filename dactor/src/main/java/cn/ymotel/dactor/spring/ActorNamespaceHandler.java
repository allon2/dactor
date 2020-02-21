/*
 * @(#)ActorNamespaceHandler.java	1.0 2014年5月16日 上午9:49:37
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.spring;

import cn.ymotel.dactor.spring.beandef.ActorChainCfgBeanDefinitionParser;
import cn.ymotel.dactor.spring.beandef.ActorGlobalCfgBeanDefinitionParser;
import cn.ymotel.dactor.spring.beandef.ActorTransactionCfgBeanDefinitionParser;
import cn.ymotel.dactor.spring.beandef.AnnotationBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年5月16日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class ActorNamespaceHandler extends NamespaceHandlerSupport {

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.xml.NamespaceHandler#init()
     */
    @Override
    public void init() {
        ActorsBeanDefinitionParser actors = new ActorsBeanDefinitionParser();
        actors.setHandler(this);
        registerBeanDefinitionParser("actor", new ActorTransactionCfgBeanDefinitionParser());
        registerBeanDefinitionParser("chain", new ActorChainCfgBeanDefinitionParser());

        registerBeanDefinitionParser("global", new ActorGlobalCfgBeanDefinitionParser());
        registerBeanDefinitionParser("actors", actors);
        registerBeanDefinitionParser("annotation", new AnnotationBeanDefinitionParser());


    }

}
