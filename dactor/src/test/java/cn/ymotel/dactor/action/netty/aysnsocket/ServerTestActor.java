package cn.ymotel.dactor.action.netty.aysnsocket;

import cn.ymotel.dactor.action.AbstractJsonSupportActor;
import cn.ymotel.dactor.message.Message;

import java.util.HashMap;
import java.util.Map;

public class ServerTestActor  extends AbstractJsonSupportActor {
    @Override
    public Object Execute(Message message) throws Exception {
        Map map=new HashMap();

        map.put("servertest","true");
        return map;
    }
}
