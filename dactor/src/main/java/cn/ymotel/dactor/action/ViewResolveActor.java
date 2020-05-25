/*
 * @(#)ViewResolveActor.java	1.0 2014年9月10日 下午4:57:02
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.action;

import cn.ymotel.dactor.Constants;
import cn.ymotel.dactor.async.web.view.CustomHttpView;
import cn.ymotel.dactor.async.web.view.HttpView;
import cn.ymotel.dactor.message.ServletMessage;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.pattern.PatternLookUpMatch;
import cn.ymotel.dactor.pattern.PatternMatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.UrlPathHelper;

import java.util.*;

/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年9月10日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class ViewResolveActor implements Actor<ServletMessage>, InitializingBean, ApplicationContextAware {
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(ViewResolveActor.class);


    Map<String, HttpView> viewMap = new HashMap();


    /**
     * @return the viewMap
     */
    public Map<String, HttpView> getViewMap() {
        return viewMap;
    }

    /**
     * @param viewMap the viewMap to set
     */
    public void setViewMap(Map viewMap) {
        this.viewMap = viewMap;
    }

    public void setUrlSuffixMap(Map<String, HttpView> urlSuffixMap) {
        this.urlSuffixMap = urlSuffixMap;
    }



    @Override
    public Message  HandleMessage(ServletMessage message) throws Exception {

        if (message instanceof ServletMessage) {
            ServletMessage servletMessage=message;
            if (servletMessage.getAsyncContext().getResponse().isCommitted()) {
                return  message;
            } else {
                Map dataMap = message.getContext();

                for (java.util.Iterator iter = dataMap.entrySet().iterator(); iter.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    servletMessage.getAsyncContext().getRequest().setAttribute((String) entry.getKey(), entry.getValue());
                }
                if (message.getException() != null) {
                    servletMessage.getAsyncContext().getRequest().setAttribute("_EXCEPTION", message.getException());
                }
            }
        }
        if(UrlPathViewExecute(message)){
            return message;
        }
        if(viewExecute(message)){
            return message;
        };
        if(SuffixViewExecute(message)){
            return message;
        }



        System.err.println("can't find httpview");
        return message;
    }
    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    /**
     *
     * @param message
     * @return 执行返回true，不执行 返回false
      */
    public boolean UrlPathViewExecute(ServletMessage message){

        String UrlPath = urlPathHelper.getLookupPathForRequest(message.getRequest());

        HttpView httpView=  patternLookUpMatch.lookupMatchBean(UrlPath,message.getRequest().getMethod(),message.getRequest().getServerName(),message.getRequest());
        if(httpView==null){
            return false;
        }
        if(message.getException()==null){
            httpView.successRender(message,null);
        }else{
            httpView.exceptionRender(message,null);
        }
        return true;

    }
    public boolean SuffixViewExecute(ServletMessage message){
        String suffix=(String)message.getContext().get(Constants.SUFFIX);
        if(suffix==null){
            return false;
        }
        //优先使用后缀模式
            HttpView view = urlSuffixMap.get(suffix);
            if(view==null){
                return false;
            }
                if(message.getException()==null){
                    view.successRender(message,null);
                }else{
                    view.exceptionRender(message,null);
                }
                return true;


    }
    public boolean viewExecute(ServletMessage message){
        java.lang.Throwable exception=message.getException();
        String result=renderResultView(message);
        if(result==null){
            return false;
        }
        String[] resolverNames = getResolverNames(result);
        HttpView view = this.getViewMap().get(resolverNames[0]);
        if (view == null) {
            return false;
        }
        try {
            ReplaceVariable(message, resolverNames);
            if(exception==null){
                view.successRender(message,resolverNames[1]);
            }else{
                view.exceptionRender(message,resolverNames[1]);
            }
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
            message.getAsyncContext().complete();
        }
        return true;
    }

    private void ReplaceVariable(ServletMessage message, String[] resolverNames) {
        if(resolverNames[1].indexOf("{")>=0) {
        }else{
            return;
        }
            Object obj = message.getContextData(Constants.CONTENT);
        if(obj instanceof  Map){

        }else{
            return ;
        }
        Map tMap = (Map) obj;
        for (Object s : tMap.keySet()) {
            if (resolverNames[1].indexOf("{") >= 0) {
                resolverNames[1] = resolverNames[1].replaceAll("\\{".concat(s.toString()).concat("\\}"), tMap.get(s.toString()).toString());
            } else {
                break;
            }
        }


    }

    private String[] getResolverNames(String result) {
        String[] views = result.split(":");
        String[] resolverNames = new String[2];
        if (views.length == 1) {
            if (result.endsWith(":")) {
                resolverNames[0] = views[0];
                resolverNames[1] = null;
            } else {
                resolverNames[0] = "default";
                resolverNames[1] = views[0];
            }
        } else if (views.length >= 2) {
            resolverNames[0] = views[0];
            resolverNames[1] = result.substring((views[0]+":").length());
        }
        return resolverNames;
    }
    private boolean renderDefaultView(Message message){
        HttpView view = this.getViewMap().get("default");
        return false;
    }
    private String  renderResultView(Message message){
        {
            Object content = message.getContextData(Constants.CONTENT);
            if(content!=null&&content instanceof  String){
                return (String) content;
            }
        }
        String result = null;


        result = (String) message.getControlMessage().getSourceCfg().getResults().get("success" + message.getControlMessage().getState());

        if (result == null) {
            result = (String) message.getControlMessage().getProcessStructure().getActorTransactionCfg().getResults().get("success" + message.getControlMessage().getState());
        }
        return result;
    }
    private Map<String,HttpView> urlSuffixMap =new HashMap();
    @Override
    public void afterPropertiesSet() throws Exception {
        urlPathHelper.setAlwaysUseFullPath(true);
       Map maps= applicationContext.getBeansOfType(CustomHttpView.class);
        for(java.util.Iterator iter=maps.values().iterator();iter.hasNext();){
            CustomHttpView view=(CustomHttpView)iter.next();
            if(view.getUrlSuffix()!=null){
                urlSuffixMap.put(view.getUrlSuffix(),view);
            }
            if(view.getViewName()!=null){
                this.viewMap.put(view.getViewName(),view);
            }
            RegisterPattern(view);
        }

    }
    private PatternLookUpMatch<HttpView> patternLookUpMatch=new PatternLookUpMatch();
    private void RegisterPattern(CustomHttpView view){
        List includePattern=new ArrayList();
        if(view.getUrlPattern()!=null){
            includePattern.add(view.getUrlPattern());
        }
        if(view.getUrlPatterns()!=null&&view.getUrlPatterns().size()>0){
            includePattern.addAll(view.getUrlPatterns());
        }
        List excludePattern=new ArrayList();
        if(view.getExcludeUrlPattern()!=null){
            excludePattern.add(view.getExcludeUrlPattern());
        }
        if(view.getExcludeUrlPatterns()!=null&&view.getExcludeUrlPatterns().size()>0){
            excludePattern.addAll(view.getExcludeUrlPatterns());
        }
        patternLookUpMatch.add(new PatternMatcher<HttpView>(includePattern,excludePattern,view));
    }
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
