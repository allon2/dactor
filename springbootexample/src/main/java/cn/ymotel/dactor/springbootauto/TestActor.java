package cn.ymotel.dactor.springbootauto;

import cn.ymotel.dactor.action.Actor;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.spring.annotaion.ActorCfg;

import java.util.HashMap;
import java.util.Map;

@ActorCfg(urlPatterns = "/a.json")
public class TestActor implements Actor {

    @Override
    public Object HandleMessage(Message message) throws Exception {
        Map map=new HashMap<>();
        map.put("aaa","bbb");
        message.getContext().put("_Content",map);
        return message;
    }
}
