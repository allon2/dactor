package cn.ymotel.dactor.core;

import cn.ymotel.dactor.message.Message;

import java.util.HashMap;
import java.util.Map;

public class ContextThreadLocal {
    private static java.lang.ThreadLocal<Map> context=new ThreadLocal<Map>(){
        @Override
        protected Map initialValue() {
            Map map=new HashMap();
            return map;
        }
    };
    public static Message getMessage(){
        return (Message) context.get().get("_Message");
    }
    public static void putMessage(Message message){
        context.get().put("_Message",message);
    }
    public static void cleanMessage(){
        context.get().put("_Message",null);

    }
}
