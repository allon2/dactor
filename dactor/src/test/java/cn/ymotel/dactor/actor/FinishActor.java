package cn.ymotel.dactor.actor;

import cn.ymotel.dactor.action.Actor;
import cn.ymotel.dactor.message.Message;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

public class FinishActor implements Actor {
    @Override
    public Object HandleMessage(Message message) throws Exception {
        CountDownLatch lock=(CountDownLatch)message.getContext().get("lock");
        lock.countDown();
        return message;
    }
}
