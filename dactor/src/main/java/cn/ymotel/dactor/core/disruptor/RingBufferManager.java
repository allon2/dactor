/*
 * @(#)RingBufferManager.java	1.0 2014年9月18日 上午12:58:17
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.core.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.ymotel.dactor.message.Message;
import com.lmax.disruptor.*;
import org.springframework.beans.factory.InitializingBean;


/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年9月18日
 *   Modification history
 *   {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class RingBufferManager implements InitializingBean{
	private int threadNumber=-1;

	RingBuffer<MessageEvent> ringBuffer;

	public void setThreadNumber(int threadNumber) {
		this.threadNumber = threadNumber;
	}

	public RingBuffer<MessageEvent> getRingBuffer() {
		return ringBuffer;
	}

	public void setRingBuffer(RingBuffer<MessageEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	private int bufferSize=1024;

	private WaitStrategy strategy=new BlockingWaitStrategy();

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
	public boolean putMessage(Message message,boolean blocked){
		;

		long seq= 0;
		if(blocked){
			seq = ringBuffer.next();
		}else {
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
	private WorkHandler<MessageEvent>[] workhandler;

	public void setWorkhandler(WorkHandler<MessageEvent>[] workhandler) {
		this.workhandler = workhandler;
	}

	private WorkerPool<MessageEvent> workerPool;
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		EventFactory<MessageEvent> factory=
				new EventFactory<MessageEvent>(){
					@Override
					public MessageEvent newInstance() {
						return new MessageEvent();
					}
				};

		ringBuffer  =RingBuffer.createMultiProducer(factory,bufferSize,strategy);
		SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
		 executor = Executors.newFixedThreadPool(this.threadNumber);

		workerPool = new WorkerPool<MessageEvent>(ringBuffer, sequenceBarrier, new IgnoreExceptionHandler(), workhandler);
		ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
		workerPool.start(executor);


	}
	ExecutorService executor=null;
	public void shutdown() {
		workerPool.halt();
		executor.shutdown();

	}

}
