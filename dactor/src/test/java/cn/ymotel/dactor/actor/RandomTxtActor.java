package cn.ymotel.dactor.actor;

import cn.ymotel.dactor.action.AbstractJsonSupportActor;
import cn.ymotel.dactor.action.Actor;
import cn.ymotel.dactor.message.Message;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

public class RandomTxtActor implements Actor {
    private final static org.apache.commons.logging.Log logger = LogFactory.getLog(RandomTxtActor.class);




    @Override
    public Object HandleMessage(Message message) throws Exception {
        message.getContext().put("111","222");
        return message;
    }
}
