/*
 * @(#)ViewResolveActor.java	1.0 2014年9月10日 下午4:57:02
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.action;

import cn.ymotel.dactor.Constants;
import cn.ymotel.dactor.async.web.view.HttpView;
import cn.ymotel.dactor.message.ServletMessage;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.message.SpringControlMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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
public class ViewResolveActor implements Actor, InitializingBean {
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

    /* (non-Javadoc)
     * @see com.dragon.actor.Actor#HandleMessage(com.dragon.actor.Message)
     */
    @Override
    public Object HandleMessage(Message message) throws Exception {

        if (message instanceof ServletMessage) {
//			((LocalServletMessage)message).getAsyncContext().getRequest().setAttribute("_Message", message);

            Map dataMap = (Map) message.getContext();
            if (((ServletMessage) message).getAsyncContext().getResponse().isCommitted()) {
            } else {
                for (java.util.Iterator iter = dataMap.entrySet().iterator(); iter.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    ((ServletMessage) message).getAsyncContext().getRequest().setAttribute((String) entry.getKey(), entry.getValue());
                }
                if (message.getException() != null) {
                    ((ServletMessage) message).getAsyncContext().getRequest().setAttribute("_EXCEPTION", message.getException());
                }


            }
        }
        String result = null;


        result = (String) ((SpringControlMessage) message.getControlMessage()).getSourceCfg().getResults().get("success" + message.getControlMessage().getState());

        if (result == null) {
            result = (String) message.getControlMessage().getProcessStructure().getActorTransactionCfg().getResults().get("success" + message.getControlMessage().getState());
            ;
        }
        if(result==null){
            //尝试使用后缀
            HttpView view=suffixMap.get(message.getContext().get(Constants.SUFFIX));
            view.render(message,null);
        }
//			String result=WorkFlowData.getResults(message.getControlMessage().getSourceId(),"success"+message.getControlMessage().getState());
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
            //	resolverNames[1] = resolverNames[1]; // a bug 2004/10/18 larry
            resolverNames[1] = views[1];
        }
        HttpView view = this.getViewMap().get(resolverNames[0]);
        if (view == null) {
            view = this.getViewMap().get("default");
        }
        try {
            view.render(message, resolverNames[1]);
        } catch (Exception e) {
            if (logger.isTraceEnabled()) {
                logger.trace("HandleMessage(Message) - viewResolver-----" + ((ServletMessage) message) + "----" + ((ServletMessage) message).getAsyncContext()); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (logger.isTraceEnabled()) {
                logger.trace("HandleMessage(Message)"); //$NON-NLS-1$
            }
        }
        return message;
    }
    private Map<String,HttpView> suffixMap=new HashMap();
    @Override
    public void afterPropertiesSet() throws Exception {

        for(java.util.Iterator iter=viewMap.entrySet().iterator();iter.hasNext();){
            Map.Entry entry=(Map.Entry)iter.next();
            HttpView view=(HttpView)entry.getValue();
            if(view.getSuffix()!=null){
                String suffix=view.getSuffix();
                if(suffix.startsWith(".")){
                    suffix=suffix.substring(1);
                }
                suffixMap.put(suffix,view);
            }
        }
    }
}
