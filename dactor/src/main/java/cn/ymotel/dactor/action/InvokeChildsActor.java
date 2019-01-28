package cn.ymotel.dactor.action;

import cn.ymotel.dactor.core.MessageDispatcher;
import cn.ymotel.dactor.message.Message;

import java.util.Map;

public class InvokeChildsActor implements Actor {

    @Override
    public Object HandleMessage(Message message) throws Exception {
        Map map = null;
        MessageDispatcher dispathcer = null;
        message.setChildCount(map.size());
        for (java.util.Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {

            Map.Entry entry = (Map.Entry) iter.next();

//			dispathcer.startMessage((Message)entry.getValue(),(String)entry.getKey());

        }
        return null;
    }


}
