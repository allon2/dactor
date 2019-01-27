/*
 * @(#)ActorChainCfgBeanDefinitionParser.java	1.0 2014年9月21日 上午10:49:26
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.spring.beandef;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import cn.ymotel.dactor.core.ActorChainCfg;

/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年9月21日
 *   Modification history	
 *   {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class ActorChainCfgBeanDefinitionParser extends
		AbstractSingleBeanDefinitionParser {
//	private String actor="actorref";
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser#getBeanClass(org.w3c.dom.Element)
	 */
	@Override
	protected Class<?> getBeanClass(Element element) {		 
		return ActorChainCfg.class;
	}
//	 @SuppressWarnings("all")  
//	    public Map parseMapElement(Element mapEle,   
//	            ParserContext parserContext, BeanDefinitionBuilder builder) {  
//	  
//	        List entryEles = DomUtils.getChildElementsByTagName(mapEle, "child");  
//	                // 关键是以下这个ManagedMap类型，充当着一个map类型的beandefinition类型的说明对象  
//	        ManagedMap map = new ManagedMap(entryEles.size());  
//	        map.setMergeEnabled(true);  
//	        map.setSource(parserContext.getReaderContext().extractSource(mapEle));  
//	  
//	        for (Iterator it = entryEles.iterator(); it.hasNext();) {  
//	            Element entryEle = (Element) it.next();  
//	      
//	            String name = entryEle.getAttribute("name");  
//	  
//	            map.put(name, parserContext.getDelegate().parseCustomElement(  
//	                    entryEle, builder.getRawBeanDefinition()));  
//	  
//	        }  
//	  
//	        return map;  
//	    }  
	 
	 
	 public List parseMapElement(Element mapEle,   
	            ParserContext parserContext, BeanDefinitionBuilder builder) {  
	  
	        List entryEles = DomUtils.getChildElementsByTagName(mapEle, "child");  
	                // 关键是以下这个ManagedMap类型，充当着一个map类型的beandefinition类型的说明对象  
	        ManagedList map = new ManagedList(entryEles.size());  
	        map.setMergeEnabled(true);  
	        map.setSource(parserContext.getReaderContext().extractSource(mapEle));  
	  
	        for (Iterator it = entryEles.iterator(); it.hasNext();) {  
	            Element entryEle = (Element) it.next();  
	      
//	            String name = entryEle.getAttribute("name");  
//	            parserContext.getDelegate().par
	            map.add(parserContext.getDelegate().parseListElement(entryEle, builder.getRawBeanDefinition()));
//	            map.put(name, parserContext.getDelegate().parseCustomElement(  
//	                    entryEle, builder.getRawBeanDefinition()));  
	  
	        }  
	  
	        return map;  
	    }  
	 
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		
//      RootBeanDefinition beanDef = new RootBeanDefinition();

	     String actorId=element.getAttribute("id");

//      Map params=new HashMap();
      
//		BeanDefinitionHolder hodler=new BeanDefinitionHolder();
		org.w3c.dom.NodeList list=element.getChildNodes();
//		System.out.println(element+"----"+list.getLength());
  	for(int i=0;i<list.getLength();i++){
  		org.w3c.dom.Node node=list.item(i);
  		
  		if (node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {continue;}
  		
  		if(parserContext.getDelegate().getLocalName(node).equals("before")){
  			Element el=(Element)node;
//  			List rtnList=parserContext.getDelegate().parseListElement(el, builder.getRawBeanDefinition());
  			builder.addPropertyValue("before", getRefs(el,parserContext));

  		}
		if(parserContext.getDelegate().getLocalName(node).equals("after")){
			Element el=(Element)node;
//			List rtnList=parserContext.getDelegate().parseListElement(el, builder.getRawBeanDefinition());
			builder.addPropertyValue("after", getRefs(el,parserContext));

		}
  	}
//  	builder.addPropertyValue("chain", chain);
  	
  	builder.addPropertyValue("id", actorId);
  	
      BeanDefinitionHolder holder = new BeanDefinitionHolder(builder.getRawBeanDefinition(), actorId);  

      BeanDefinitionReaderUtils.registerBeanDefinition(holder, parserContext.getRegistry());

	}
	private List getRefs(Element element,ParserContext parserContext){
		org.w3c.dom.NodeList list=element.getChildNodes();
		List ls=new ManagedList();
		for(int i=0;i<list.getLength();i++){
			org.w3c.dom.Node node=list.item(i);

			if (node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {continue;}
			if(parserContext.getDelegate().getLocalName(node).equals("ref")) {
				Element el=(Element)node;
				String bean= el.getAttribute("bean");
				ls.add(new RuntimeBeanReference(bean));
			}
		}
		return ls;
	}
}
