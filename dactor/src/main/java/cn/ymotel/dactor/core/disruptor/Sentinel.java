package cn.ymotel.dactor.core.disruptor;

public class Sentinel implements  Runnable{
    private WorkProcessorManager workProcessorManager=null;
    private int minsize=-1;
    private  int maxsize=-1;

    public WorkProcessorManager getWorkProcessorManager() {
        return workProcessorManager;
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


        long workingprocess=workProcessorManager.getRingBuffer().getBufferSize()-workProcessorManager.getRingBuffer().remainingCapacity();
        int processorListSize=workProcessorManager.getProcessorList().size();
        System.out.println(minsize+"--"+processorListSize+"---"+workingprocess);

        //正在处理的数小于minsize，并且队列数小于minsize，停止处理
        if(workingprocess<= minsize&&processorListSize<=minsize){
            return ;
        }
        if(workingprocess>processorListSize){
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

            long inum=processorListSize-workingprocess-MessageEventHandler.consumercount.get();
            if(processorListSize-inum<=minsize){
                inum=processorListSize-minsize;
            }
            System.out.println("decreame number--|||||"+inum+"|||"+processorListSize+"|||"+workingprocess+"|||"+minsize);
            if(inum<=0){
                return ;
            }
//            for(int i=0;i<inum;i++) {
                workProcessorManager.decrOneConsumer();
//            }
//            rfm.decrConsumer(inum);
            //需要减少
////                    rfm.decrConsumer();
        }
    }
}
