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
            ServletMessage servletMessage=(ServletMessage)message;
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

        viewExecute(message);


//        String result=renderResultView(message);
//        if(result==null){
//            //
//            String suffix=(String)message.getContext().get(Constants.SUFFIX);
//            //优先使用后缀模式
//            if(suffix!=null) {
//                HttpView view = suffixMap.get(suffix);
//                if (view != null) {
//                    view.render(message, null);
//                    return message;
//                }
//            }
//        }
//        String[] resolverNames = getResolverNames(result);
//        HttpView view = this.getViewMap().get(resolverNames[0]);
//        if (view == null) {
//
//            ((ServletMessage)message).getAsyncContext().complete();
//
//            System.err.println("can't find view");
//            return message;
//        }
//        try {
//            view.render(message, resolverNames[1]);
//        } catch (Exception e) {
//           e.printStackTrace();
//        }
        return message;
    }
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
//    private List<HttpView> urlPatternView=new ArrayList<>();
    private Map<String,HttpView> urlPatternViewMap=new HashMap<>();

    /**
     *
     * @param message
     * @return 执行返回true，不执行 返回false
      */
    public boolean UrlPathViewExecute(ServletMessage message){
//        if(urlPatternViewMap==null||urlPatternViewMap.isEmpty()){
//            return false;
//        }
        String UrlPath = urlPathHelper.getLookupPathForRequest(message.getRequest());

        HttpView httpView=  patternLookUpMatch.lookupMatchBean(UrlPath,message.getRequest().getMethod(),message.getRequest().getServerName());
        if(httpView==null){
            return false;
        }
        if(message.getException()==null){
            httpView.successRender(message,null);
        }else{
            httpView.exceptionRender(message,null);
        }
//        Comparator comparator= antPathMatcher.getPatternComparator(UrlPath);
//
////        HttpView matchview=null;
////        String matchUrlPattern=null;
//        Map.Entry matchEntry=null;
//        for(java.util.Iterator iter=urlPatternViewMap.entrySet().iterator();iter.hasNext();){
//            Map.Entry entry=(Map.Entry)iter.next();
//            if(antPathMatcher.match((String)entry.getKey(), UrlPath)){
//                if(matchEntry==null){
//                    matchEntry=entry;
//                    continue;
//                }
//                //路由优先级
//                int compare=comparator.compare((String)entry.getKey(),(String)matchEntry.getKey());
//                if(compare<0) {
//                    matchEntry=entry;
//                }
//            }
//        }
//
//        if(matchEntry==null){
//            return false;
//        }
//        if(message.getException()==null){
//            ((HttpView)matchEntry.getValue()).successRender(message,null);
//        }else{
//            ((HttpView)matchEntry.getValue()).exceptionRender(message,null);
//        }
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
    public void viewExecute(ServletMessage message){
        java.lang.Throwable exception=message.getException();
        String result=renderResultView(message);
        if(result==null){
            //
            if(SuffixViewExecute(message)){
                return ;
            }
            if(UrlPathViewExecute(message)){
                return;
            };

            message.getAsyncContext().complete();

            System.err.println("can't find view");
            return ;
//            String suffix=(String)message.getContext().get(Constants.SUFFIX);
//            //优先使用后缀模式
//            if(suffix!=null) {
//                HttpView view = urlSuffixMap.get(suffix);
//                if (view != null) {
//                    if(exception==null){
//                        view.successRender(message,null);
//                    }else{
//                        view.exceptionRender(message,null);
//                    }
//                    return ;
//                }
//            }
        }
        String[] resolverNames = getResolverNames(result);
        HttpView view = this.getViewMap().get(resolverNames[0]);
        if (view == null) {

            message.getAsyncContext().complete();

            System.err.println("can't find view");
            return ;
        }
        try {
            if(exception==null){
                view.successRender(message,null);
            }else{
                view.exceptionRender(message,null);
            }
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
            message.getAsyncContext().complete();
        }
        return ;
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
        } else if (views.length == 2) {
            resolverNames[0] = views[0];
            resolverNames[1] = views[1];
        }
        return resolverNames;
    }
    private boolean renderDefaultView(Message message){
        HttpView view = this.getViewMap().get("default");
        return false;
    }
    private String  renderResultView(Message message){
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
//            if(view.getUrlPattern()!=null){
//                this.urlPatternViewMap.put(view.getUrlPattern(),view);
//            }
//            if(view.getUrlPatterns()!=null&&view.getUrlPatterns().size()>0){
//                for(int i=0;i<view.getUrlPatterns().size();i++){
//                    this.urlPatternViewMap.put((String)view.getUrlPatterns().get(i),view);
//                }
//            }
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
