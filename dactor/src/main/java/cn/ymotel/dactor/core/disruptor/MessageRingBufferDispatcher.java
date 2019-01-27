/*
 * @(#)MessageDispatcher.java	1.0 2014年4月21日 上午10:59:26
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.core.disruptor;


import com.lmax.disruptor.WorkHandler;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import cn.ymotel.dactor.core.AbstractMessageDispatcher;
import cn.ymotel.dactor.message.Message;
import com.lmax.disruptor.WaitStrategy;

/**
 * 取得消息队列放入线程中
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年4月21日
 *   Modification history
 *   {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class MessageRingBufferDispatcher extends AbstractMessageDispatcher implements InitializingBean{
	private final org.apache.commons.logging.Log logger = LogFactory.getLog(getClass());
//	private MessageEventProducer producer;

	private RingBufferManager ringbufferManager;


	/* (non-Javadoc)
	 * @see com.dragon.actor.core.AbstractMessageDispatcher#putMessageInDispatcher(com.dragon.actor.message.Message)
	 */
	@Override
	public boolean putMessageInDispatcher(Message message, boolean blocked) {
		//为空，则说明，没有需要处理的数据，属于正常处理，属于处理完毕情况，所以返回True
		if(message.getControlMessage().getActorsStack().isEmpty()){
			return true;
		}
		if(message.getControlMessage().getProcessStructure().getFromBeanId()==null){
			return true ;
		}
			logger.info("beanId--"+message.getControlMessage().getProcessStructure().getFromBeanId()+"--Id--"+message.getControlMessage().getProcessStructure().getActorTransactionCfg().getId());


		return ringbufferManager.putMessage(message,blocked);

	}
	private int bufferSize=1024;
	private WaitStrategy strategy=null;

	/**
	 * @return the bufferSize
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * @param bufferSize the bufferSize to set
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	/**
	 * @return the strategy
	 */
	public WaitStrategy getStrategy() {
		return strategy;
	}

	/**
	 * @param strategy the strategy to set
	 */
	public void setStrategy(WaitStrategy strategy) {
		this.strategy = strategy;
	}
	private int threadNumber=-1;

	public void setThreadNumber(int threadNumber) {
		this.threadNumber = threadNumber;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

		this.ringbufferManager=new RingBufferManager();
//		this.eventHandler=new MessageEventHandler();

		if(threadNumber==-1){
			threadNumber=Runtime.getRuntime().availableProcessors();
		}
		WorkHandler<MessageEvent>[] workHandlers = new WorkHandler[threadNumber];
		for(int i=0;i<workHandlers.length;i++){
			MessageEventHandler handler=new MessageEventHandler();
			handler.setApplicationContext(this.getApplicationContext());
			handler.setDispatcher(this);
			workHandlers[i]=handler;
		}
		this.ringbufferManager.setBufferSize(bufferSize);
		this.ringbufferManager.setThreadNumber(threadNumber);
		this.ringbufferManager.setWorkhandler(workHandlers);
		if(strategy!=null){
			this.ringbufferManager.setStrategy(this.getStrategy());;

		}
		this.ringbufferManager.afterPropertiesSet();



	}
	public void shutdown(){
		this.ringbufferManager.shutdown();
	}

}
