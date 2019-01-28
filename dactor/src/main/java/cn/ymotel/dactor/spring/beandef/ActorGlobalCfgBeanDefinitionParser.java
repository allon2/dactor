/*
 * @(#)ActorGlobalCfgBeanDefinitionParser.java	1.0 2014年9月19日 下午11:49:25
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.spring.beandef;

import cn.ymotel.dactor.core.ActorGlobalCfg;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年9月19日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class ActorGlobalCfgBeanDefinitionParser extends
        AbstractSingleBeanDefinitionParser {

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser#getBeanClass(org.w3c.dom.Element)
     */
    @Override
    protected Class<?> getBeanClass(Element element) {

        return ActorGlobalCfg.class;
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

//        RootBeanDefinition beanDef = new RootBeanDefinition();


        Map params = new HashMap();

//		BeanDefinitionHolder hodler=new BeanDefinitionHolder();
        org.w3c.dom.NodeList list = element.getChildNodes();
//		System.out.println(element+"----"+list.getLength());
        for (int i = 0; i < list.getLength(); i++) {
            org.w3c.dom.Node node = list.item(i);

            if (node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {
                continue;
            }

            if (parserContext.getDelegate().getLocalName(node).equals("param")) {
                Element el = (Element) node;
                String name = el.getAttribute("name");
                String value = el.getAttribute("value");
                params.put(name, value);
            }
//    		System.out.println(parserContext.getDelegate().getLocalName(node)+"-------"+node.getNodeType()+"node----"+node);
        }
        builder.addPropertyValue("params", params);

        BeanDefinitionHolder holder = new BeanDefinitionHolder(builder.getRawBeanDefinition(), "ActorGlobal");

        BeanDefinitionReaderUtils.registerBeanDefinition(holder, parserContext.getRegistry());

    }
//  	  protected void doParse(Element element, BeanDefinitionBuilder bean) {  
//    	org.w3c.dom.NodeList list=element.getChildNodes();
//    	for(int i=0;i<list.getLength();i++){
//    		org.w3c.dom.Node node=list.item(i);
//    		System.out.println(parserContext.getDelegate().getLocalName(node));
//    	}
//    
//    }
}
