/*
 * @(#)MessageEventHandler.java	1.0 2014年9月18日 上午12:47:46
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.core.disruptor;

import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.workflow.ActorProcessStructure;


import cn.ymotel.dactor.workflow.WorkFlowProcess;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import cn.ymotel.dactor.action.Actor;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年9月18日
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
public class MessageEventHandler implements EventHandler<MessageEvent>,WorkHandler<MessageEvent>,ApplicationContextAware {
	
	
	private final org.apache.commons.logging.Log logger = LogFactory.getLog(MessageEventHandler.class);; 
	private MessageRingBufferDispatcher dispatcher;
	private ApplicationContext appcontext=null;

	/**
	 * @return the dispatcher
	 */
	public MessageRingBufferDispatcher getDispatcher() {
		return dispatcher;
	}






	/**
	 * @param dispatcher the dispatcher to set
	 */
	public void setDispatcher(MessageRingBufferDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}






	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		appcontext=applicationContext;
		
	}

	/* (non-Javadoc)
	 * @see com.lmax.disruptor.EventHandler#onEvent(java.lang.Object, long, boolean)
	 */
	@Override
	public void onEvent(MessageEvent event, long sequence, boolean endOfBatch)
			throws Exception {
		onEvent(event);
	}






	/**
	 * {method specification, must edit}
	 *
	 * @param struc
	 * @throws InterruptedException
	 *
	 * @version 1.0
	 * @since 1.0
	 */
	private void handleEvent(ActorProcessStructure struc, Message message)  {

	
		Actor actor = (Actor)appcontext.getBean(struc.getFromBeanId());;
 			logger.info("beanId--"+struc.getFromBeanId()+"--Id--"+struc.getActorTransactionCfg().getId());


			try {
				 Object obj=	actor.HandleMessage(message);

				} catch (Throwable exception) {

					message.setException(exception);
//				dispatcher.sendMessage(message);

			}finally{
				if(struc.getActorTransactionCfg().getBeginBeanId().equals(struc.getFromBeanId())){
					struc.setBeginExecute(true);
				}
				if(struc.getActorTransactionCfg().getEndBeanId().equals(struc.getFromBeanId())){
					struc.setEndExecute(true);
				}
				//已经执行的FromBeanId
 				WorkFlowProcess.processGetToBeanId(message.getControlMessage(), message, appcontext);

 				dispatcher.sendMessage(message);

				}

		

	}


	/* (non-Javadoc)
	 * @see com.lmax.disruptor.WorkHandler#onEvent(java.lang.Object)
	 */
	@Override
	public void onEvent(MessageEvent event) throws Exception {
		  Message message=event.getMessage();

		  ActorProcessStructure struc=message.getControlMessage().getProcessStructure();
			if(struc==null){
				return ;
			}
	 		if(struc.getFromBeanId()==null||struc.getFromBeanId().trim().equals("")){
	 			return ;
	 		}





		handleEvent(struc,message);


	}



}
