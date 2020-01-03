/*
 * @(#)MessageEventHandler.java	1.0 2014年9月18日 上午12:47:46
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.core.disruptor;

import cn.ymotel.dactor.action.Actor;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.spring.SpringUtils;
import cn.ymotel.dactor.workflow.ActorProcessStructure;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.LifecycleAware;
import com.lmax.disruptor.WorkHandler;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.concurrent.*;

/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年9月18日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class MessageEventHandler implements EventHandler<MessageEvent>, WorkHandler<MessageEvent>, ApplicationContextAware, LifecycleAware {
    /**
     * 控制并发数，防止超过处理能力
     */
//    private ExecutorService cachedThreadPool=Executors.newCachedThreadPool();
//    private final CountDownLatch shutdownLatch = new CountDownLatch(1);
//    public static java.util.concurrent.atomic.AtomicInteger consumercount=new java.util.concurrent.atomic.AtomicInteger(0);
    private Sentinel sentinel;

    public void setSentinel(Sentinel sentinel) {
        this.sentinel = sentinel;
    }

    private final org.apache.commons.logging.Log logger = LogFactory.getLog(MessageEventHandler.class);
    ;
    private MessageRingBufferDispatcher dispatcher;
//    ExecutorService executor = null;
    private ApplicationContext appcontext = null;

//    public void setExecutor(ExecutorService executor) {
//        this.executor = executor;
//    }

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
        appcontext = applicationContext;

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
     * @version 1.0
     * @since 1.0
     */
    private void handleEvent(ActorProcessStructure struc, Message message,Actor actor ) {
        sentinel.getProcessingConsumerNumber().incrementAndGet();
//        consumercount.getAndIncrement();
//        System.out.println(consumercount.get());
//        Actor actor = (Actor) appcontext.getBean(struc.getFromBeanId());

//        System.out.println("beanId--" + struc.getFromBeanId() + "--Id--" + struc.getActorTransactionCfg().getId());

        ;
        if(logger.isDebugEnabled()) {
            logger.debug("beanId--" + struc.getFromBeanId() + "--Id--" + struc.getActorTransactionCfg().getId());
        }

        try {
            Object obj = actor.HandleMessage(message);
            if (struc.getActorTransactionCfg().getBeginBeanId().equals(struc.getFromBeanId())) {
                struc.setBeginExecute(true);
            }
            if (struc.getActorTransactionCfg().getEndBeanId().equals(struc.getFromBeanId())) {
                struc.setEndExecute(true);
            }
//				WorkFlowProcess.processGetToBeanId(message.getControlMessage(), message, appcontext);

            if (obj != null && obj instanceof Message) {
//                executor.submit((() ->   dispatcher.sendMessage(message)));
//                executor.submit(new Runnable() {
//                    @Override
//                    public void run() {
                        dispatcher.sendMessage(message);
//
//                    }
//                });
            }
        } catch (Throwable exception) {

            message.setException(exception);
            if (struc.getActorTransactionCfg().getBeginBeanId().equals(struc.getFromBeanId())) {
                struc.setBeginExecute(true);
            }
            if (struc.getActorTransactionCfg().getEndBeanId().equals(struc.getFromBeanId())) {
                struc.setEndExecute(true);
            }
            //已经执行的FromBeanId
//				WorkFlowProcess.processGetToBeanId(message.getControlMessage(), message, appcontext);
//            executor.submit((() ->   dispatcher.sendMessage(message)));

            dispatcher.sendMessage(message);
//				dispatcher.sendMessage(message);

        } finally {
//            System.out.println("handler--event");
            sentinel.getProcessingConsumerNumber().decrementAndGet();
//            consumercount.decrementAndGet();
        }


    }


    /* (non-Javadoc)
     * @see com.lmax.disruptor.WorkHandler#onEvent(java.lang.Object)
     */
    @Override
    public void onEvent(MessageEvent event) throws Exception {
        Message message = event.getMessage();
        if(message==null){
            return ;
        }

        ActorProcessStructure struc = message.getControlMessage().getProcessStructure();
        if (struc == null) {
            return;
        }
        if (struc.getFromBeanId() == null || struc.getFromBeanId().trim().equals("")) {
            return;
        }

        Actor actor = (Actor) SpringUtils.getCacheBean(appcontext,struc.getFromBeanId());

        handleEvent(struc, message,actor);


//        if(actor instanceof CpuAble){
//            handleEvent(struc, message,actor);
//        }else{
////            semaphore.acquire();
//            cachedThreadPool.submit(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        handleEvent(struc, message,actor);
//                    } finally {
////                        semaphore.release();
//                    }
//
//                }
//            });
//            //控制并发数
//            CompletableFuture.runAsync(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        handleEvent(struc, message,actor);
//                    } finally {
//                        semaphore.release();
//                    }
//
//                }
//            });
//        }


    }

    @Override
    public void onStart() {

    }

    @Override
    public void onShutdown()
    {
//        System.out.println("on shutdown"+Thread.currentThread());
//        shutdownLatch.countDown();
    }

//    public void awaitShutdown() throws InterruptedException
//    {
//        shutdownLatch.await();
//    }

}
