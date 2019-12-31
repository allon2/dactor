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
    private  Sentinel sentinel;
    public List<WorkProcessorExt> getProcessorList() {
        return processorList;
    }

    public RingBuffer<MessageEvent> getRingBuffer() {
        return ringBuffer;
    }

    public WorkProcessorManager(ExecutorService executor, MessageRingBufferDispatcher messageRingBufferDispatcher, ApplicationContext appcontext, RingBuffer<MessageEvent> ringBuffer,Sentinel sentinel) {
        this.executor = executor;
        this.messageRingBufferDispatcher = messageRingBufferDispatcher;
        this.appcontext = appcontext;
        this.ringBuffer = ringBuffer;
        this.sentinel=sentinel;
    }

    /**
     * 初始化 end
     */

    private List<WorkProcessorExt> processorList=new ArrayList();
//    private Map<WorkProcessorExt,MessageEventHandler> workHandlerMap=new HashMap();
    private final Sequence workSequence = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);
    private WorkProcessorExt<MessageEvent> createProcessor(RingBuffer<MessageEvent> ringBuffer, WorkHandler workHandler) {
//        SequenceBarrier barrier=null;
//            if(processorList.size()==0){
//                barrier=ringBuffer.newBarrier();
//            }else{
//                WorkProcessor processor=  processorList.get(processorList.size()-1);
//            }

        return new WorkProcessorExt<>(ringBuffer, ringBuffer.newBarrier(), workHandler, new IgnoreExceptionHandler(),workSequence);
    }

    /**
     * 增加一个
     */
    public  void incrOneConsumer(){
        MessageEventHandler handler=createWorkHandler();
        WorkProcessorExt processor=  createProcessor(this.ringBuffer,handler);
//        workHandlerMap.put(processor,handler);
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
        WorkProcessorExt<MessageEvent> tprocessor= processorList.get(0);

        tprocessor.halt();
//        try {
//            workHandlerMap.get(tprocessor).awaitShutdown();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        ringBuffer.removeGatingSequence(tprocessor.getSequence());

//            workHandlerMap.remove(tprocessor);
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
        handler.setSentinel(sentinel);
        return handler;
    }
}
