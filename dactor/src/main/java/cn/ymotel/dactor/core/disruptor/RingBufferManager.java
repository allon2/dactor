/*
 * @(#)RingBufferManager.java	1.0 2014年9月18日 上午12:58:17
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.core.disruptor;

import cn.ymotel.dactor.message.Message;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.*;
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
public class RingBufferManager implements InitializingBean, ApplicationContextAware {
    private MessageRingBufferDispatcher messageRingBufferDispatcher;

    public void setMessageRingBufferDispatcher(MessageRingBufferDispatcher messageRingBufferDispatcher) {
        this.messageRingBufferDispatcher = messageRingBufferDispatcher;
    }

    private ApplicationContext appcontext = null;

    private int minsize = -1;
    private int maxsize=-1;

    public void setMaxsize(int maxsize) {
        this.maxsize = maxsize;
    }

    public void setChecktime(int checktime) {
        this.checktime = checktime;
    }

    RingBuffer<MessageEvent> ringBuffer;

    public void setMinsize(int minsize) {
        this.minsize = minsize;
    }

    public RingBuffer<MessageEvent> getRingBuffer() {
        return ringBuffer;
    }

    public void setRingBuffer(RingBuffer<MessageEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    private int bufferSize = 1024;

    private WaitStrategy strategy = new BlockingWaitStrategy();
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

    public int getProcessorsize(){
        return this.workProcessorManager.getProcessorList().size();
    }
    private ConcurrentLinkedQueue <Message> quene=new ConcurrentLinkedQueue();
    private Semaphore semaphore=new Semaphore(0);
    public boolean putMessage(Message message, boolean blocked) {

        //非阻塞,并且队列不为空，说明ringBuffer为满，直接返回
        if(!quene.isEmpty()){
            if(!blocked){
                return false;
            }

        }
        long seq = 0;

        try {
            seq = ringBuffer.tryNext();
        } catch (InsufficientCapacityException e) {
            if(!blocked) {
                return false;
            }else{
                //阻塞

                    quene.add(message);
                    semaphore.release();

                return true;
            }
        }
            ringBuffer.get(seq).setMessage(message);

        ringBuffer.publish(seq);
        return true;
    }

    private void putQueneMessage(){

       long seq= ringBuffer.next();
        Message message=quene.poll();

        ringBuffer.get(seq).setMessage(message);
        ringBuffer.publish(seq);

    }
    /**
     * 毫秒为单位
     */
    private int checktime=1000;
    private WorkProcessorManager workProcessorManager=null;
   private  Sentinel sentinel=new Sentinel();

    public Sentinel getSentinel() {
        return sentinel;
    }
    private Disruptor disruptor=null;
    private ScheduledExecutorService scheduledExecutorService=null;
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        EventFactory<MessageEvent> factory =
                new EventFactory<MessageEvent>() {
                    @Override
                    public MessageEvent newInstance() {
                        return new MessageEvent();
                    }
                };
        executor= Executors.newCachedThreadPool();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                for(;;) {
                    try {
                        if(isshutdown){
                            break;
                        }
                        semaphore.acquire();
                        if(isshutdown){
                            break;
                        }
//                        Message message = quene.take();
                        putQueneMessage();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


         disruptor = new Disruptor<>(factory, bufferSize,  DaemonThreadFactory.INSTANCE, ProducerType.MULTI,strategy);
        ringBuffer= disruptor.start();


          workProcessorManager=new WorkProcessorManager(executor,messageRingBufferDispatcher,this.appcontext,ringBuffer,sentinel);
        sentinel.setMaxsize(maxsize);
        sentinel.setMinsize(minsize);
        sentinel.setWorkProcessorManager(workProcessorManager);
        workProcessorManager.incrConsumer(minsize);

        scheduledExecutorService=Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(sentinel,checktime,checktime, TimeUnit.MILLISECONDS);


    }

    ExecutorService executor = null;
    private boolean isshutdown=false;
    public void shutdown() {

        isshutdown=true;
        semaphore.release();
        workProcessorManager.shutdown();
        disruptor.shutdown();
        executor.shutdown();
        scheduledExecutorService.shutdown();

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appcontext=applicationContext;
    }
}
