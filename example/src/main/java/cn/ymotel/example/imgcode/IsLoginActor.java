package cn.ymotel.example.imgcode;

import cn.ymotel.dactor.action.Actor;
import cn.ymotel.dactor.exception.DActorException;
import cn.ymotel.dactor.message.Message;

public class IsLoginActor implements Actor {
    @Override
    public Object HandleMessage(Message message) throws Exception {
        throw new DActorException("1000","未登录");
//        return message;
    }
}
