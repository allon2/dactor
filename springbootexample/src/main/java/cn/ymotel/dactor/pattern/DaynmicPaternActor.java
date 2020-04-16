package cn.ymotel.dactor.pattern;

import cn.ymotel.dactor.action.Actor;
import cn.ymotel.dactor.core.DyanmicUrlPattern;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.spring.annotaion.ActorCfg;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态Pattern，通过自身继承接口实现
 */
@ActorCfg()
public class DaynmicPaternActor implements Actor, DyanmicUrlPattern {

    @Override
    public Object HandleMessage(Message message) throws Exception {
        Map map=new HashMap<>();
        map.put("aaa","动态测试");
        message.getContext().put("_Content",map);
        return message;
    }

    @Override
    public String[] getPatterns() {
        String[] ss=new String[1];
        ss[0]="/b.json";
        return ss;
    }
}
