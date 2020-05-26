package cn.ymotel.dactor;

import cn.ymotel.dactor.core.ActorTransactionCfg;
import cn.ymotel.dactor.core.MessageDispatcher;
import cn.ymotel.dactor.message.DefaultMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertTrue;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:/ringbuffer.xml","classpath:/workflow/workflow1.xml"})
public class WorkFlow1Test extends AbstractJUnit4SpringContextTests {
//    @Test
    public void test1(){
        MessageDispatcher dispatcher= (MessageDispatcher)this.applicationContext.getBean("MessageRingBufferDispatcher");
        DefaultMessage defaultMessage=new DefaultMessage();
        CountDownLatch lock=new CountDownLatch(1);

        defaultMessage.getContext().put("lock",lock);
        try {
            dispatcher.startMessage(defaultMessage,(ActorTransactionCfg)this.applicationContext.getBean("randomTxt1"));
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }

        try {
            lock.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(defaultMessage.getContext().get("111").equals("222"));
    }

}
