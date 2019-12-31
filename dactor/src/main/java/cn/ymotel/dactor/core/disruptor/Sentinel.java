package cn.ymotel.dactor.core.disruptor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 整体策略，快速增加，缓慢减
 */
public class Sentinel implements  Runnable{
    private WorkProcessorManager workProcessorManager=null;
    /**
     * 线程正在处理的消费者的个数
     */
    private java.util.concurrent.atomic.AtomicInteger processingConsumerNumber=new java.util.concurrent.atomic.AtomicInteger(0);



    private int minsize=-1;
    private  int maxsize=-1;


    public AtomicInteger getProcessingConsumerNumber() {
        return processingConsumerNumber;
    }

    public void setWorkProcessorManager(WorkProcessorManager workProcessorManager) {
        this.workProcessorManager = workProcessorManager;
    }

    public int getMinsize() {
        return minsize;
    }

    public void setMinsize(int minsize) {
        this.minsize = minsize;
    }

    public int getMaxsize() {
        return maxsize;
    }

    public void setMaxsize(int maxsize) {
        this.maxsize = maxsize;
    }

    @Override
    public void run() {

        /**
         * ringbuffer中待处理队列数
         */
        long workingprocess=workProcessorManager.getRingBuffer().getBufferSize()-workProcessorManager.getRingBuffer().remainingCapacity();
        int processorListSize=workProcessorManager.getProcessorList().size();
        int processingConsumersize=processingConsumerNumber.get();
//        System.out.println(minsize+"--"+processorListSize+"---"+workingprocess);

        //正在处理的数小于minsize，并且队列数小于minsize，停止处理
        if(workingprocess<= minsize&&processorListSize<=minsize){
            return ;
        }
        if(workingprocess>processorListSize){
            //正在处理的小于线程数，不增加
            if(processorListSize>processingConsumersize){
                return ;
            }
            //需要增加
            if(maxsize==-1){
            }else{
                if(workingprocess>maxsize){
                    //只能增加到最大
                    workingprocess=maxsize;
                }
            }
            long incrn=workingprocess-processorListSize;
            if(incrn<=0){
                return ;
            }
            /**
             * 每次增加二分之一
             */
            incrn=incrn/2;
            long mod=incrn%2;
            if(mod!=0){
                incrn++;
            }
            for(int i=0;i<incrn;i++){
                workProcessorManager.incrOneConsumer();
            }

        }else {
            /**
             * 最小处理数不能小于minsize
             */
            if(processorListSize<=minsize){
                return ;
            }
            /**
             * 全部都在处理，不减
             */
            if(processorListSize==processingConsumersize){
                return ;
            }
            long inum=processorListSize-workingprocess-processingConsumersize;
            if(processorListSize-inum<=minsize){
                inum=processorListSize-minsize;
            }
//            System.out.println("decreame number--|||||"+inum+"|||"+processorListSize+"|||"+workingprocess+"|||"+minsize);
            if(inum<=0){
                return ;
            }
//            inum=inum/2;
//            for(int i=0;i<inum;i++) {
                workProcessorManager.decrOneConsumer();
//            }
//            rfm.decrConsumer(inum);
            //需要减少
////                    rfm.decrConsumer();
        }
    }
}
