package cn.ymotel.dactor;

import cn.ymotel.dactor.core.ActorTransactionCfg;
import cn.ymotel.dactor.core.MessageDispatcher;
import cn.ymotel.dactor.message.DefaultMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/ringbuffer.xml","classpath:/workflow/workflow2.xml"})
public class WorkFlow2Test extends AbstractJUnit4SpringContextTests {
    /**
     * 测试Step中的beginBeanId是Actor标签
     */
//    @Test
    public void test1(){
        MessageDispatcher dispatcher= (MessageDispatcher)this.applicationContext.getBean("MessageRingBufferDispatcher");
        DefaultMessage defaultMessage=new DefaultMessage();
        CountDownLatch lock=new CountDownLatch(1);

        defaultMessage.getContext().put("lock",lock);
        try {
            dispatcher.startMessage(defaultMessage,(ActorTransactionCfg)this.applicationContext.getBean("randomTxt2"));
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

    /**
     * 测试Step中的endBeanId是Actor标签
     */
//    @Test
    public void test2(){
        MessageDispatcher dispatcher= (MessageDispatcher)this.applicationContext.getBean("MessageRingBufferDispatcher");
        DefaultMessage defaultMessage=new DefaultMessage();
        CountDownLatch lock=new CountDownLatch(1);

        defaultMessage.getContext().put("lock",lock);
        try {
            dispatcher.startMessage(defaultMessage,(ActorTransactionCfg)this.applicationContext.getBean("randomTxt3"));
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
