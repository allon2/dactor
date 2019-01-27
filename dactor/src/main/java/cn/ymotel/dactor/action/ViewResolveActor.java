/*
 * @(#)ViewResolveActor.java	1.0 2014年9月10日 下午4:57:02
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.action;

import cn.ymotel.dactor.message.LocalServletMessage;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.message.SpringControlMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

import cn.ymotel.dactor.async.web.view.HttpView;

/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年9月10日
 *   Modification history	
 *   {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class ViewResolveActor implements Actor {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(ViewResolveActor.class);
	 
 
	Map<String, HttpView> viewMap=new HashMap();
	
	
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
 
		if(message instanceof LocalServletMessage){
//			((LocalServletMessage)message).getAsyncContext().getRequest().setAttribute("_Message", message);
			
			Map dataMap=(Map)message.getContext();
			if(((LocalServletMessage)message).getAsyncContext().getResponse().isCommitted()){}else{
			for(java.util.Iterator iter=dataMap.entrySet().iterator();iter.hasNext();){
				Map.Entry entry=(Map.Entry)iter.next();
				((LocalServletMessage)message).getAsyncContext().getRequest().setAttribute((String)entry.getKey(), entry.getValue());
			}
			if(message.getException()!=null){
			((LocalServletMessage)message).getAsyncContext().getRequest().setAttribute("_EXCEPTION",message.getException());
			}
			

			}
		}
		String result=null;
			
		
		 result=(String)((SpringControlMessage)message.getControlMessage()).getSourceCfg().getResults().get("success"+message.getControlMessage().getState());
		 
		 if(result==null){
				result=(String)message.getControlMessage().getProcessStructure().getActorTransactionCfg().getResults().get("success"+message.getControlMessage().getState());;
		 }
//			String result=WorkFlowData.getResults(message.getControlMessage().getSourceId(),"success"+message.getControlMessage().getState());
			String[] views=result.split(":");
 			String[] resolverNames = new String[2];
			if(views.length==1){
				if(result.endsWith(":")){
					resolverNames[0]=views[0];
					resolverNames[1]=null;	
				}else{
				
				resolverNames[0]="default";
				resolverNames[1]=views[0];		
				}
			}else if (views.length == 2) {
				resolverNames[0] = views[0];
				//	resolverNames[1] = resolverNames[1]; // a bug 2004/10/18 larry
				resolverNames[1] = views[1];
			}
			HttpView view=this.getViewMap().get(resolverNames[0]);
			if(view==null){
				view=this.getViewMap().get("default");
			}
			try {
				view.render(message, resolverNames[1]);
			} catch (Exception e) {
			if (logger.isTraceEnabled()) {
				logger.trace("HandleMessage(Message) - viewResolver-----" + ((LocalServletMessage) message) + "----" + ((LocalServletMessage) message).getAsyncContext()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (logger.isTraceEnabled()) {
				logger.trace("HandleMessage(Message)"); //$NON-NLS-1$
			}
			}
		return message;
	}

}
