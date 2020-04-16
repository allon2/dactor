package cn.ymotel.dactor.pattern;

import cn.ymotel.dactor.action.Actor;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.spring.annotaion.ActorCfg;

import java.util.HashMap;
import java.util.Map;

/**
 * 通过Pattern 通过指定其他类实现
 */
@ActorCfg(urlPatternClass = {DayNamicPattern.class})
public class DaynmicPaternActor2 implements Actor{

    @Override
    public Object HandleMessage(Message message) throws Exception {
        Map map=new HashMap<>();
        map.put("aaa","动态测试2");
        message.getContext().put("_Content",map);
        return message;
    }


}
