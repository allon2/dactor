/*
 * @(#)ActorChainCfgBeanDefinitionParser.java	1.0 2014年9月21日 上午10:49:26
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.spring.beandef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ymotel.dactor.core.ActorTransactionCfg;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

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
public class ActorTransactionCfgBeanDefinitionParser extends
		AbstractSingleBeanDefinitionParser {
	private String actor="actor";
	private String CHAIN="chain";
	private String CONDTIONS="steps";
	private String condtion="step";

	private String results="results";
	private String result="result";
	private String URLPATTERN="urlPattern";
 	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser#getBeanClass(org.w3c.dom.Element)
	 */
	@Override
	protected Class<?> getBeanClass(Element element) {		 
		return ActorTransactionCfg.class;
	}
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
//      RootBeanDefinition beanDef = new RootBeanDefinition();

      
//     List chain=new ArrayList();
//		Map condtions=new HashMap();
		List condtions=new ArrayList();
     
     String actorId=element.getAttribute("id");
     
     
//     String urlPattern="";
//		
//		if(actorId.indexOf("/")>=0){
//			urlPattern=actorId.substring(actorId.indexOf("/"));
////			builder.addPropertyValue(URLPATTERN, urlPattern);
//			actorId=actorId.substring(0,actorId.indexOf("/"));
//		}
     
     if( StringUtils.hasText(element.getAttribute("beginBeanId"))){
     builder.addPropertyValue("beginBeanId", element.getAttribute("beginBeanId"));
     }
     if( StringUtils.hasText(element.getAttribute("endBeanId"))){
     builder.addPropertyValue("endBeanId", element.getAttribute("endBeanId"));
     }
     
     if( StringUtils.hasText(element.getAttribute("urlPattern"))){
         builder.addPropertyValue("urlPattern", element.getAttribute("urlPattern").split("/"));
     }
    
     if( StringUtils.hasText(element.getAttribute("handleException"))){
         builder.addPropertyValue("handleException", element.getAttribute("handleException"));
         }
     
     if( StringUtils.hasText(element.getAttribute("chain"))){
    	 builder.addPropertyReference("chain", element.getAttribute("chain"));
//         builder.addPropertyValue("chain", new RuntimeBeanReference(element.getAttribute("chain")));

     };
     /**
      * 增加父类配置
      */
     if( StringUtils.hasText(element.getAttribute("parent"))){
    	 builder.addPropertyReference("parent", element.getAttribute("parent"));
     };

      
     
     
//      Map params=new HashMap();
      
//		BeanDefinitionHolder hodler=new BeanDefinitionHolder();
		org.w3c.dom.NodeList list=element.getChildNodes();
		builder.addPropertyReference("global", "ActorGlobal");
  	  	builder.addPropertyValue("id", actorId);
		
//		System.out.println(element+"----"+list.getLength());
  	for(int i=0;i<list.getLength();i++){
  		org.w3c.dom.Node node=list.item(i);
  		
  		if (node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {continue;}
  		
  		if(parserContext.getDelegate().getLocalName(node).equals(CONDTIONS)){
  			Element el=(Element)node;
  			Map tmpcondtions=getCondtion(el,parserContext);

  		     builder.addPropertyValue("steps", tmpcondtions);

  			
  		}
  		if(parserContext.getDelegate().getLocalName(node).equals(results)){
  			Element el=(Element)node;
  			Map ls=getResults(el,parserContext);
  		     builder.addPropertyValue("results", ls);		
  		}
  		
  		if(parserContext.getDelegate().getLocalName(node).equals("overrides")){
  			Element el=(Element)node;
  			Map ls=getOverrideMap(el,parserContext);
  		     builder.addPropertyValue("overridesMap", ls);		
  		}
  		
  		
  		
  	

//  		System.out.println(parserContext.getDelegate().getLocalName(node)+"-------"+node.getNodeType()+"node----"+node);
  	}
  	

  	
//  	builder.addPropertyValue("chain", chain);
  	
      BeanDefinitionHolder holder = new BeanDefinitionHolder(builder.getRawBeanDefinition(), actorId);  

      BeanDefinitionReaderUtils.registerBeanDefinition(holder, parserContext.getRegistry());

	}
	public Map getResults(Element element,ParserContext parserContext){
		Map map=new HashMap();
		org.w3c.dom.NodeList list=element.getChildNodes();
		for(int i=0;i<list.getLength();i++){
			org.w3c.dom.Node node=list.item(i);
	  		if (node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {continue;}
//  			Map property=new HashMap();

	  		if(parserContext.getDelegate().getLocalName(node).equals(result)){
	  			Element el=(Element)node;

	  			map.put(el.getAttribute("name"), el.getTextContent());
	  			
	  		}
	  		
		}
		return map;
	}
	public Map getOverrideMap(Element element,ParserContext parserContext){
		Map map=new HashMap();
		org.w3c.dom.NodeList list=element.getChildNodes();
		for(int i=0;i<list.getLength();i++){
			org.w3c.dom.Node node=list.item(i);
	  		if (node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {continue;}
//  			Map property=new HashMap();

	  		if(parserContext.getDelegate().getLocalName(node).equals("beanId")){
	  			Element el=(Element)node;

	  			map.put(el.getAttribute("name"), el.getTextContent());
	  			
	  		}
	  		
		}
		return map;
	}
	public Map getCondtion(Element element,ParserContext parserContext){
//		List ls=new ArrayList();
		Map condtions=new HashMap();
		
		
		org.w3c.dom.NodeList list=element.getChildNodes();
		for(int i=0;i<list.getLength();i++){
			org.w3c.dom.Node node=list.item(i);
	  		if (node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {continue;}
  			Map property=new HashMap();
  			
  			List tmpList=null;
	  		if(parserContext.getDelegate().getLocalName(node).equals(condtion)){
	  			Element el=(Element)node;
 	  			String fromBeanId= el.getAttribute("fromBeanId");
	  			if(condtions.containsKey(fromBeanId)){
	  				tmpList=(List)condtions.get(fromBeanId);
	  			}else{
	  				tmpList=new ArrayList();
	  				condtions.put(fromBeanId, tmpList);
	  			}
	  			property.put("fromBeanId", el.getAttribute("fromBeanId"));
	  			property.put("toBeanId", el.getAttribute("toBeanId"));
	  			property.put("conditon", el.getAttribute("conditon"));
	  			property.put("async", el.getAttribute("async"));
	  			property.put("after", el.getAttribute("after"));

	  			tmpList.add(property);

	  		}
	  		
		}
		return condtions;
	}
}
