/*
 * @(#)AsyncServlet.java	1.0 2014年8月29日 下午11:35:14
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.async.web;

import cn.ymotel.dactor.core.ActorTransactionCfg;
import cn.ymotel.dactor.core.disruptor.MessageRingBufferDispatcher;
import cn.ymotel.dactor.message.DefaultResolveMessage;
import cn.ymotel.dactor.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.util.UrlPathHelper;

/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年8月29日
 *   Modification history	
 *   {add your history}
 * </p>
 * <p>
 *
 *
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class AsyncServlet extends FrameworkServlet {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(AsyncServlet.class);
	

	private long timeout=1000l*60*15;
	private UrlPathHelper urlPathHelper = new UrlPathHelper();
	private DefaultResolveMessage defaultResolveMessage;
	private String messageSourceId="messageSource";

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.FrameworkServlet#doService(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doService(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		 /**
		  * Fmt:message可以直接访问MessageSource对应的属性
		  */
		org.springframework.context.MessageSource messageSource=(org.springframework.context.MessageSource )this.getWebApplicationContext().getBean(messageSourceId);
		JstlUtils.exposeLocalizationContext(request, messageSource);

		
		if (logger.isTraceEnabled()) {
			logger.trace("doService(HttpServletRequest, HttpServletResponse) - contenttype----" + request.getContentType()); //$NON-NLS-1$
		}
			java.util.Enumeration enum1=request.getHeaderNames();
			for(;enum1.hasMoreElements();){
				String el=enum1.nextElement().toString();
			if (logger.isTraceEnabled()) {
				logger.trace("doService(HttpServletRequest, HttpServletResponse) - el----" + el + "----" + request.getHeader(el)); //$NON-NLS-1$ //$NON-NLS-2$
			};
			}
		  
		  String UrlPath=urlPathHelper.getLookupPathForRequest(request);
		  
 		  
		  String suffix=UrlPath.substring(UrlPath.lastIndexOf(".")+1);

		  
		 String transactionId= resolveTransactionId(UrlPath,request);
		if (logger.isDebugEnabled()) {
			logger.debug("doService(HttpServletRequest, HttpServletResponse) - suffix-----" + suffix + "--transactionId--" + transactionId); //$NON-NLS-1$ //$NON-NLS-2$
		}
		/**
		 * 找不到交易码，直接输出空白结果
		 */
		if(!this.getWebApplicationContext().containsBean(transactionId)){
			response.getOutputStream().flush();;

			return ;
		}
		 ActorTransactionCfg cfg=(ActorTransactionCfg)this.getWebApplicationContext().getBean(transactionId);
//			System.out.println("重复提交3");

		 AsyncContext asyncContext = request.startAsync(request, response);

		  asyncContext.addListener(new DActorAsyncListener());
		  asyncContext.setTimeout(timeout);

		  
		  Message message=defaultResolveMessage.resolveContext(asyncContext,request,response);
		 
		 Map params=getUrlmap(UrlPath,cfg,request);
		 
		 message.getContext().putAll(params);
		 message.getContext().put("_METHOD", request.getMethod());
		 message.getContext().put("_SUFFIX", suffix);

		 
		 try {
 			  getDispatcher(request.getServletContext()).startMessage(message, cfg,false);
  		} catch (Exception e) {
			if (logger.isTraceEnabled()) {
				logger.trace("doService(HttpServletRequest, HttpServletResponse)"); //$NON-NLS-1$
			}
 			asyncContext.getResponse().setContentType("text/html; charset=utf-8");
 			asyncContext.getRequest().setAttribute("_EXCEPTION", e);
 			//输出空白页面
			 asyncContext.getResponse().getOutputStream().flush();

			 asyncContext.complete();
		}

	}
	public Map getUrlmap(String urlPath, ActorTransactionCfg cfg, HttpServletRequest request){
		
		if(cfg.getUrlPattern()==null){
			return new HashMap();
		}

		if(urlPath.startsWith("/")){
			urlPath=urlPath.substring(1);
		}
		
		///wx.service/1.do
		if(urlPath.indexOf("/")<0){
			return new HashMap();
		}
		int end=urlPath.lastIndexOf(".");
		if(end>=0){
			urlPath=urlPath.substring(0,end);
		}
		Map map=new HashMap();
		String[] urlPaths=urlPath.split("/");
		String[] tmps=cfg.getUrlPattern();
		int len=0;
		if(tmps.length>urlPaths.length){
			len=urlPaths.length;
		}else{
			len=tmps.length;
		}
		
		for(int i=0;i<len;i++){
			
			String value=getUrlName(tmps[i]);
			if(value==null){
				continue;
			}
			
			map.put(value, urlPaths[i]);
		}
//		urlPath.
		return map;
 	}
	public static String getUrlName(String value){
		if(value.indexOf("{")<0){
			return null;
		}
		return value.substring(value.indexOf("{")+1,value.indexOf("}"));
		
	}
	public static void main(String[] args){
		String s="/olview.view/a.do";
//		System.out.println(s.replaceAll("/",".").replace(".","/"));
		System.out.println(resolveTransactionId(s,null));
	}
	protected static String resolveTransactionId(String path, HttpServletRequest request)
	{

		/*
		 * the path fetch from urlPathHelper, according servlet url pattern:
		 * for example: my servlet context is "context", full url will be:
		 * full url					url pattern      path
		 * /context/.../test.do		*.do			 /test.do
		 * /context/prefix/test/ss	/prefix/*		 /test/ss
		 */
		
		String transactionId=null;
		
//		String transactionId = request.getParameter(idParameterName);
//		if(transactionId == null){
			int s = 1;
			int l1 = path.indexOf('/', s);
			int l2 = path.lastIndexOf('.');
			
			
			int l = -1;
			if(l1 != -1 && l2 != -1){
				l = l1 > l2 ? l2 : l1;
			}else{
				if(l1 != -1)
					l = l1;
				else if(l2 != -1)
					l = l2;
			}

			if(l == -1) l = path.length();
			
			transactionId = path.substring(s, l);
//		transactionId=transactionId.replaceAll("/",".");
			
			 
//		}
		return transactionId;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	private static final String DISPATCHER = WebApplicationContext.class.getName() + ".dispatchers";
	public   MessageRingBufferDispatcher  getDispatcher(ServletContext sc) {


		if(sc.getAttribute(DISPATCHER)!=null){
			return (MessageRingBufferDispatcher )sc.getAttribute(DISPATCHER);
		}
		MessageRingBufferDispatcher dispatcher=(MessageRingBufferDispatcher)this.getWebApplicationContext().getBean("MessageRingBufferDispatcher");
		sc.setAttribute(DISPATCHER,dispatcher);

		return dispatcher;



	}
	@Override
	public void init(ServletConfig config) throws ServletException {
		this.setApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext()));
 		super.init(config);
 		
 		urlPathHelper.setAlwaysUseFullPath(true);



		defaultResolveMessage=(DefaultResolveMessage)this.getWebApplicationContext().getBean("DefaultResolveMessage");
	}

}
