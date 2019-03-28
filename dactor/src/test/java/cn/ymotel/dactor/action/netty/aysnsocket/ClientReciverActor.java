package cn.ymotel.dactor.action.netty.aysnsocket;

import cn.ymotel.dactor.action.AbstractJsonSupportActor;
import cn.ymotel.dactor.message.Message;

public class ClientReciverActor extends AbstractJsonSupportActor {
    @Override
    public Object Execute(Message message) throws Exception {
        System.out.println("client-reciver"+message.getContext());
        return null;
    }
}
