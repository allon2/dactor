package cn.ymotel.dactor.action;

import cn.ymotel.dactor.message.Message;

import java.util.List;

public class ChildsEndActor implements Actor {

    @Override
    public Object HandleMessage(Message message) throws Exception {
        List ls = message.getChilds();
        for (int i = 0; i < ls.size(); i++) {
            /**
             * 处理Childs值结果
             */
        }

        ls.clear();
        return message;
    }

}
