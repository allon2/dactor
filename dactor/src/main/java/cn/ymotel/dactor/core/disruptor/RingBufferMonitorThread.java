package cn.ymotel.dactor.core.disruptor;

import com.lmax.disruptor.RingBuffer;

public class RingBufferMonitorThread extends Thread {
	private MessageRingBufferDispatcher messageRingBufferDispatcher;

	public MessageRingBufferDispatcher getMessageRingBufferDispatcher() {
		return messageRingBufferDispatcher;
	}

	public void setMessageRingBufferDispatcher(MessageRingBufferDispatcher messageRingBufferDispatcher) {
		this.messageRingBufferDispatcher = messageRingBufferDispatcher;
	}

	@Override
	public void run() {
		RingBuffer ringBuffer=messageRingBufferDispatcher.getRingbufferManager().getRingBuffer();
		RingBufferManager ringBufferManager=	messageRingBufferDispatcher.getRingbufferManager();
		 for(;;){
			 try {
				sleep(10l*1000);
				java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyyMMddHHmmss");

				System.out.println(sdf.format(new java.util.Date())+"总队列数"+ringBuffer.getBufferSize()+"剩余可用队列数"+ringBuffer.remainingCapacity()+"消费者"+ringBufferManager.getProcessorsize()+"正在处理消费者个数"+ringBufferManager.getSentinel().getProcessingConsumerNumber().get());
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			 
		 }
	}

}
