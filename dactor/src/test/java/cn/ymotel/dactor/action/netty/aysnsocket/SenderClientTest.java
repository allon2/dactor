//package cn.ymotel.dactor.action.netty.aysnsocket;
//
//import cn.ymotel.dactor.action.AbstractJsonSupportActor;
//import cn.ymotel.dactor.core.ActorTransactionCfg;
//import cn.ymotel.dactor.core.MessageDispatcher;
//import cn.ymotel.dactor.core.disruptor.MessageRingBufferDispatcher;
//import cn.ymotel.dactor.message.DefaultMessage;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.CountDownLatch;
//
//import static org.junit.Assert.assertTrue;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:/ringbuffer.xml","classpath:/tcp/tcp1.xml"})
//public class SenderClientTest extends AbstractJUnit4SpringContextTests {
//    private MessageRingBufferDispatcher MessageDispatcher;
//
//    @Test
//    public void test1(){
//        cn.ymotel.dactor.core.MessageDispatcher dispatcher= (MessageDispatcher)this.applicationContext.getBean("MessageRingBufferDispatcher");
//        DefaultMessage defaultMessage=new DefaultMessage();
//        defaultMessage.getContext().put("tt","tt");
//        defaultMessage.getContext().put("actorId","server1");
//        CountDownLatch lock=new CountDownLatch(1);
//
//        defaultMessage.getContext().put("lock",lock);
//
//        try {
//            dispatcher.startMessage(defaultMessage,(ActorTransactionCfg)this.applicationContext.getBean("client"));
//        } catch (Exception e) {
//            e.printStackTrace();
//            assert false;
//        }
////        try {
////            Thread.sleep(10000);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
//        try {
//            lock.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Map bodyMap=(Map)defaultMessage.getContext().get("body");
//        System.out.println(bodyMap.get("servertest"));
//        assertTrue(bodyMap.get("servertest").equals("true"));
//    }
//
//
//
//}
