/*
 * @(#)MessageDispatcher.java	1.0 2014年4月21日 上午10:59:26
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.core.disruptor;


import cn.ymotel.dactor.core.AbstractMessageDispatcher;
import cn.ymotel.dactor.message.Message;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkHandler;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;


/**
 * 取得消息队列放入线程中
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年4月21日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class MessageRingBufferDispatcher extends AbstractMessageDispatcher implements InitializingBean {
    private final org.apache.commons.logging.Log logger = LogFactory.getLog(getClass());
//	private MessageEventProducer producer;

    private RingBufferManager ringbufferManager;


    /* (non-Javadoc)
     * @see com.dragon.actor.core.AbstractMessageDispatcher#putMessageInDispatcher(com.dragon.actor.message.Message)
     */
    @Override
    public boolean putMessageInDispatcher(Message message, boolean blocked) {
        //为空，则说明，没有需要处理的数据，属于正常处理，属于处理完毕情况，所以返回True
        if (message.getControlMessage().getDownStack().isEmpty()) {
            return true;
        }
        if (message.getControlMessage().getProcessStructure().getFromBeanId() == null) {
            return true;
        }
        if(logger.isDebugEnabled()) {
            logger.debug("beanId--" + message.getControlMessage().getProcessStructure().getFromBeanId() + "--Id--" + message.getControlMessage().getProcessStructure().getActorTransactionCfg().getId());
        }

        return ringbufferManager.putMessage(message, blocked);

    }

    private int bufferSize = 1024;
    /**
     * 并行消费信号量
     */
    private WaitStrategy strategy = null;

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



    public void setMinsize(int minsize) {
        this.minsize = minsize;
    }

    private int minsize = -1;

    /**
     * use minsize instead
     * @param threadNumber 最小线程数，已经废弃，使用minsize
     */
    @Deprecated
    public void setThreadNumber(int threadNumber) {
        this.minsize = threadNumber;
    }

    public void setMaxsize(int maxsize) {
        this.maxsize = maxsize;
    }

    public void setChecktime(int checktime) {
        this.checktime = checktime;
    }

    private int maxsize=300;
    private int checktime=1000;
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        this.ringbufferManager = new RingBufferManager();
//		this.eventHandler=new MessageEventHandler();

        if (minsize == -1) {
            minsize = Runtime.getRuntime().availableProcessors();
        }
//        WorkHandler<MessageEvent>[] workHandlers = new WorkHandler[minsize];
//        for (int i = 0; i < workHandlers.length; i++) {
//            MessageEventHandler handler = new MessageEventHandler();
//            handler.setApplicationContext(this.getApplicationContext());
//            handler.setDispatcher(this);
//            workHandlers[i] = handler;
//        }
        this.ringbufferManager.setBufferSize(bufferSize);
        this.ringbufferManager.setMinsize(minsize);
//        this.ringbufferManager.setWorkhandler(workHandlers);
        if (strategy != null) {
            this.ringbufferManager.setStrategy(this.getStrategy());
        }
        this.ringbufferManager.setApplicationContext(this.getApplicationContext());
        this.ringbufferManager.setChecktime(checktime);
        this.ringbufferManager.setMaxsize(maxsize);
        this.ringbufferManager.setMessageRingBufferDispatcher(this);
        this.ringbufferManager.afterPropertiesSet();


    }

    public RingBufferManager getRingbufferManager() {
        return ringbufferManager;
    }

    public void setRingbufferManager(RingBufferManager ringbufferManager) {
        this.ringbufferManager = ringbufferManager;
    }

    public void shutdown() {
        this.ringbufferManager.shutdown();
    }

}
