/*
 * @(#)AsyncServlet.java	1.0 2014年8月29日 下午11:35:14
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.async.web;

import cn.ymotel.dactor.Constants;
import cn.ymotel.dactor.core.ActorTransactionCfg;
import cn.ymotel.dactor.core.MessageDispatcher;
import cn.ymotel.dactor.core.UrlMapping;
import cn.ymotel.dactor.core.disruptor.MessageRingBufferDispatcher;
import cn.ymotel.dactor.message.DefaultResolveMessage;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.ActorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 *  不再更新，以后新特性都使用AsyncServletFilter实现
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年8月29日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
@Deprecated
public class AsyncServlet extends FrameworkServlet {
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(AsyncServlet.class);


    private long timeout = 1000l * 60 * 15;
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private DefaultResolveMessage defaultResolveMessage;
    private String messageSourceId = "messageSource";
    private int errorcode=429;//请求数量太多

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.FrameworkServlet#doService(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doService(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        /**
         * Fmt:message可以直接访问MessageSource对应的属性
         */

        org.springframework.context.MessageSource messageSource = (org.springframework.context.MessageSource) ActorUtils.getCacheBean(this.getWebApplicationContext(),messageSourceId);
        JstlUtils.exposeLocalizationContext(request, messageSource);

        String UrlPath = urlPathHelper.getLookupPathForRequest(request);

        ActorTransactionCfg cfg=null;
        {
           Object requestHandler= getRequestHandler(request, UrlPath);
           if(requestHandler==null){
               response.getOutputStream().flush();
               return ;
           }else
           if(requestHandler instanceof HttpRequestHandler){

               ((HttpRequestHandler)requestHandler).handleRequest(request,response);
               return ;

           }else if(requestHandler instanceof ActorTransactionCfg ){
               cfg=(ActorTransactionCfg)requestHandler;
           }
        }

        String suffix = null;
        if(UrlPath.lastIndexOf(".")>=0) {
            suffix= UrlPath.substring(UrlPath.lastIndexOf(".") + 1);
        }

        AsyncContext asyncContext = request.startAsync(request, response);

        asyncContext.addListener(new DActorAsyncListener());
        asyncContext.setTimeout(timeout);


        Message message = defaultResolveMessage.resolveContext(asyncContext, request, response);

        Map params = getUrlmap(UrlPath, cfg, request);

        message.getContext().putAll(params);
        message.getContext().put(Constants.METHOD, request.getMethod());
        message.getContext().put(Constants.SUFFIX, suffix);


        try {
          boolean b=  getDispatcher(request.getServletContext()).startMessage(message, cfg, false);
          if(!b){
              //队列满
                ((HttpServletResponse)asyncContext.getResponse()).sendError(errorcode);
              asyncContext.complete();

              return ;
          }
        } catch (Exception e) {

            asyncContext.getResponse().setContentType("text/html; charset=utf-8");
            asyncContext.getRequest().setAttribute("_EXCEPTION", e);
            //输出空白页面
            asyncContext.getResponse().getWriter().print(e.getMessage());
            asyncContext.getResponse().getWriter().flush();

            asyncContext.complete();
        }

    }
    public Object getRequestHandler(HttpServletRequest request, String UrlPath){
        String transactionId = resolveTransactionId(UrlPath, request);

        /**
         * 找不到交易码，直接输出空白结果
         */
        transactionId= ActorUtils.getBeanFromTranstionId(this.getWebApplicationContext(),transactionId);
        if(transactionId!=null){
            return  ActorUtils.getCacheBean(this.getWebApplicationContext(),transactionId);
         }
        //使用UrlPattern进行查找
        Map.Entry matchentry=null;
        Map mapping=UrlMapping.getMapping();

            for(java.util.Iterator iter=mapping.entrySet().iterator();iter.hasNext();){
             Map.Entry entry=(Map.Entry)iter.next();
                if(UrlPath.equals("/")&&entry.getKey().equals("/")){
                    matchentry=entry;
                    break;
                }
             //取key最长的返回
            if(antPathMatcher.match((String)entry.getKey(),UrlPath)){
                 if(matchentry==null){
                     matchentry=entry;
                 }else{


                     if(((String)matchentry.getKey()).length()<((String)entry.getKey()).length()){
                         matchentry=entry;
                     }
                 }
            };
        }
        if(matchentry==null){
            return null;
        }
        return matchentry.getValue();

    }
    private AntPathMatcher antPathMatcher=new AntPathMatcher();
    public Map getUrlmap(String urlPath, ActorTransactionCfg cfg, HttpServletRequest request) {

        if (cfg.getUrlPattern() == null||cfg.getUrlPattern().length==0) {
            return new HashMap();
        }
        String[] pattern=cfg.getUrlPattern();
        for(int i=0;i<pattern.length;i++) {
            if(antPathMatcher.match(pattern[i],urlPath)){
                return antPathMatcher.extractUriTemplateVariables(pattern[i], urlPath);

            };
        }
        return new HashMap();
//        if (urlPath.startsWith("/")) {
//            urlPath = urlPath.substring(1);
//        }
//
//        ///wx.service/1.do
//        if (urlPath.indexOf("/") < 0) {
//            return new HashMap();
//        }
//        int end = urlPath.lastIndexOf(".");
//        if (end >= 0) {
//            urlPath = urlPath.substring(0, end);
//        }
//        Map map = new HashMap();
//        String[] urlPaths = urlPath.split("/");
//        String[] tmps = cfg.getUrlPattern();
//        int len = 0;
//        if (tmps.length > urlPaths.length) {
//            len = urlPaths.length;
//        } else {
//            len = tmps.length;
//        }
//
//        for (int i = 0; i < len; i++) {
//
//            String value = getUrlName(tmps[i]);
//            if (value == null) {
//                continue;
//            }
//
//            map.put(value, urlPaths[i]);
//        }
////		urlPath.
//        return map;
    }

//    public static String getUrlName(String value) {
//        if (value.indexOf("{") < 0) {
//            return null;
//        }
//        return value.substring(value.indexOf("{") + 1, value.indexOf("}"));
//
//    }

    public static void main(String[] args) {
        String s = "/olview.view/a.do";
//		System.out.println(s.replaceAll("/",".").replace(".","/"));
//        System.out.println(resolveTransactionId(s, null));
    }

    protected  String resolveTransactionId(String path, HttpServletRequest request) {
        if(path==null||path.equals("/")){
            return null;
        }
        int lastindex=path.lastIndexOf(".");
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if(lastindex>=0) {
            return path.substring(0, path.lastIndexOf(".")).replaceAll("/", ".");
        }else{

            return path.replaceAll("/", ".");
        }
//
//        /*
//         * the path fetch from urlPathHelper, according servlet url pattern:
//         * for example: my servlet context is "context", full url will be:
//         * full url					url pattern      path
//         * /context/.../test.do		*.do			 /test.do
//         * /context/prefix/test/ss	/prefix/*		 /test/ss
//         */
//
//        String transactionId = null;
//
////		String transactionId = request.getParameter(idParameterName);
////		if(transactionId == null){
//        int s = 1;
//        int l1 = path.indexOf('/', s);
//        int l2 = path.lastIndexOf('.');
//
//
//        int l = -1;
//        if (l1 != -1 && l2 != -1) {
//            l = l1 > l2 ? l2 : l1;
//        } else {
//            if (l1 != -1)
//                l = l1;
//            else if (l2 != -1)
//                l = l2;
//        }
//
//        if (l == -1) l = path.length();
//
//        transactionId = path.substring(s, l);
//        //支持rest格式
////		transactionId=transactionId.replaceAll("/",".");
//
//
////		}
//
//        return transactionId;
    }

    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    private static final String DISPATCHER = WebApplicationContext.class.getName() + ".dispatchers";

    public MessageDispatcher getDispatcher(ServletContext sc) {


        if (sc.getAttribute(DISPATCHER) != null) {
            return (MessageRingBufferDispatcher) sc.getAttribute(DISPATCHER);
        }
        Map dispatcherMap=this.getWebApplicationContext().getBeansOfType(MessageDispatcher.class);
        /**
         * 取第一个
         */
        MessageDispatcher dispatcher=null;
        for(java.util.Iterator iter=dispatcherMap.entrySet().iterator();iter.hasNext();){
            Map.Entry entry=(Map.Entry)iter.next();
            dispatcher=(MessageDispatcher) entry.getValue();
            sc.setAttribute(DISPATCHER, dispatcher);

            break;
        }

        return dispatcher;




    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.setApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext()));
        super.init(config);
       String serrcode= config.getInitParameter("errcode");
       if(serrcode==null||serrcode.trim().equals("")){
       }else{
           try {
               errorcode=Integer.parseInt(serrcode);
           } catch (NumberFormatException e) {
               e.printStackTrace();
           }
       }
        urlPathHelper.setAlwaysUseFullPath(true);


        defaultResolveMessage = (DefaultResolveMessage) ActorUtils.getCacheBean(this.getWebApplicationContext(),"DefaultResolveMessage") ;
    }

}
