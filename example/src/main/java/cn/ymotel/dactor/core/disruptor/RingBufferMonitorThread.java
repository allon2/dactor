package cn.ymotel.dactor.core.disruptor;

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
		 for(;;){
			 try {
				sleep(10l*1000);
				java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyyMMddHHmmss");

				System.out.println(sdf.format(new java.util.Date())+"总队列数"+messageRingBufferDispatcher.getRingbufferManager().getRingBuffer().getBufferSize()+"剩余可用队列数"+messageRingBufferDispatcher.getRingbufferManager().getRingBuffer().remainingCapacity()+"消费者"+messageRingBufferDispatcher.getRingbufferManager().getProcessorsize());
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			 
		 }
	}

}
