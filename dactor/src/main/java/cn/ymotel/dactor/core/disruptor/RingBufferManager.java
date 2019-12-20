/*
 * @(#)RingBufferManager.java	1.0 2014年9月18日 上午12:58:17
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.core.disruptor;

import cn.ymotel.dactor.message.Message;
import com.lmax.disruptor.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


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
    private List<WorkProcessor> processorList=new ArrayList();
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
        return processorList.size();
    }
    public boolean putMessage(Message message, boolean blocked) {
        ;

        long seq = 0;
        if (blocked) {
            seq = ringBuffer.next();
        } else {
            try {
                seq = ringBuffer.tryNext();
            } catch (InsufficientCapacityException e) {
                e.printStackTrace();
                return false;
            }
        }
        ringBuffer.get(seq).setMessage(message);
        ringBuffer.publish(seq);
        return true;
    }

//    private WorkHandler<MessageEvent>[] workhandler;
//
//    public void setWorkhandler(WorkHandler<MessageEvent>[] workhandler) {
//        this.workhandler = workhandler;
//    }

//    private WorkerPool<MessageEvent> workerPool;
    private WorkProcessor<MessageEvent> createProcessor( RingBuffer<MessageEvent> ringBuffer,WorkHandler workHandler) {
        return new WorkProcessor<>(ringBuffer, ringBuffer.newBarrier(), workHandler, new IgnoreExceptionHandler(),workSequence);
    }
    private final Sequence workSequence = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);
    public void initConsumer(){
        for(int i = 0; i< minsize; i++){
            ExecuteProcessor();
        }
    }
    private void ExecuteProcessor(){
        WorkProcessor processor=  createProcessor(this.ringBuffer,createWorkHandler());
        processorList.add(processor);
        ringBuffer.addGatingSequences(processor.getSequence());
        executor.execute(processor);

    }
    public void incrConsumer(long incrnum){

            for(int i=0;i<incrnum;i++){
                ExecuteProcessor();
            }
//        WorkProcessor processor=  createProcessor(this.ringBuffer,createWorkHandler());
//       ringBuffer.addGatingSequences(processor.getSequence());
//        executor.execute(processor);
    }
    public WorkHandler<MessageEvent> createWorkHandler(){
        MessageEventHandler handler = new MessageEventHandler();
        handler.setApplicationContext(this.appcontext);
        handler.setDispatcher(this.messageRingBufferDispatcher);
        return handler;
    }
    public void decrConsumer(long reducenum){

        for(Iterator iter = processorList.iterator(); iter.hasNext();){
            if(reducenum>=0){
                break;
            }
            reducenum=reducenum-1;
            WorkProcessor<MessageEvent> tprocessor=(WorkProcessor<MessageEvent>)iter.next();
            tprocessor.halt();
            ringBuffer.removeGatingSequence(tprocessor.getSequence());
            iter.remove();
        }


    }

    /**
     * 毫秒为单位
     */
    private int checktime=1000;
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
//        Disruptor disruptor=new Disruptor(factory,bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.MULTI,this.strategy);
//        ringBuffer=disruptor.getRingBuffer();
//        disruptor.shutdown();


        executor= Executors.newCachedThreadPool();


        //初始化队列数
        ringBuffer = RingBuffer.createMultiProducer(factory, bufferSize, strategy);
        initConsumer();

        RingBufferManager rfm=this;
        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                long workingprocess=ringBuffer.getBufferSize()-ringBuffer.remainingCapacity();
                if(workingprocess<= minsize){
                    return ;
                }
                if(workingprocess>processorList.size()){
                    //需要增加
                    if(maxsize==-1){

                    }else{
                        if(workingprocess>maxsize){
                            //只能增加到最大
                            workingprocess=maxsize;
                        }
                    }
                    long incrn=workingprocess-processorList.size();
                    if(incrn<=0){
                        return ;
                    }
                    rfm.incrConsumer(incrn);

                }else {

                    long inum=processorList.size()-workingprocess- minsize;
                    if(inum<=0){
                        return ;
                    }


                    rfm.decrConsumer(inum);
                    //需要减少
//                    rfm.decrConsumer();
                }
            }
        },checktime, TimeUnit.MILLISECONDS);

//        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
//        executor = Executors.newFixedThreadPool(this.threadNumber);

//        workerPool = new WorkerPool<MessageEvent>(ringBuffer, sequenceBarrier, new IgnoreExceptionHandler(), workhandler);
//        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
//        workerPool.start(executor);

//        WorkProcessor<HandlerEvent> processor = createProcessor(disruptorHandler);
//        processors[nextUnUsed] = processor;
//        handlers[nextUnUsed] = disruptorHandler;
//
//        ringBuffer.addGatingSequences(processor.getSequence());
//        executor.execute(processor);

    }

    ExecutorService executor = null;

    public void shutdown() {
        for(Iterator iter = processorList.iterator(); iter.hasNext();){
             WorkProcessor<MessageEvent> tprocessor=(WorkProcessor<MessageEvent>)iter.next();
            tprocessor.halt();
            ringBuffer.removeGatingSequence(tprocessor.getSequence());
            iter.remove();
        }
//        workerPool.halt();
        executor.shutdown();

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appcontext=applicationContext;
    }
}
