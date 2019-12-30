package cn.ymotel.dactor.core.disruptor;

import com.lmax.disruptor.*;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.concurrent.ExecutorService;

public class WorkProcessorManager {
    private ExecutorService executor = null;
    private MessageRingBufferDispatcher messageRingBufferDispatcher;
    private ApplicationContext appcontext = null;
    private     RingBuffer<MessageEvent> ringBuffer;

    public List<WorkProcessor> getProcessorList() {
        return processorList;
    }

    public RingBuffer<MessageEvent> getRingBuffer() {
        return ringBuffer;
    }

    public WorkProcessorManager(ExecutorService executor, MessageRingBufferDispatcher messageRingBufferDispatcher, ApplicationContext appcontext, RingBuffer<MessageEvent> ringBuffer) {
        this.executor = executor;
        this.messageRingBufferDispatcher = messageRingBufferDispatcher;
        this.appcontext = appcontext;
        this.ringBuffer = ringBuffer;
    }

    /**
     * 初始化 end
     */

    private List<WorkProcessor> processorList=new ArrayList();
    private Map<WorkProcessor,MessageEventHandler> workHandlerMap=new HashMap();
    private final Sequence workSequence = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);
    private WorkProcessor<MessageEvent> createProcessor(RingBuffer<MessageEvent> ringBuffer, WorkHandler workHandler) {
//        SequenceBarrier barrier=null;
//            if(processorList.size()==0){
//                barrier=ringBuffer.newBarrier();
//            }else{
//                WorkProcessor processor=  processorList.get(processorList.size()-1);
//            }

        return new WorkProcessor<>(ringBuffer, ringBuffer.newBarrier(), workHandler, new IgnoreExceptionHandler(),workSequence);
    }

    /**
     * 增加一个
     */
    public  void incrOneConsumer(){
        MessageEventHandler handler=createWorkHandler();
        WorkProcessor processor=  createProcessor(this.ringBuffer,handler);
        workHandlerMap.put(processor,handler);
        processorList.add(processor);
        ringBuffer.addGatingSequences(processor.getSequence());
        executor.execute(processor);

    }
    public void incrConsumer(int count){
        for(int i=0;i<count;i++){
            incrOneConsumer();
        }
    }
    public void decrOneConsumer(){
        WorkProcessor<MessageEvent> tprocessor= processorList.get(0);

        tprocessor.halt();

        ringBuffer.removeGatingSequence(tprocessor.getSequence());

            workHandlerMap.remove(tprocessor);
        processorList.remove(tprocessor);
//            return ;

//        }


    }
    public void shutdown(){
                while(processorList.size()>0){
                    decrOneConsumer();
                }

    }
    public MessageEventHandler createWorkHandler(){
        MessageEventHandler handler = new MessageEventHandler();
        handler.setApplicationContext(this.appcontext);
        handler.setDispatcher(this.messageRingBufferDispatcher);
        return handler;
    }
}
