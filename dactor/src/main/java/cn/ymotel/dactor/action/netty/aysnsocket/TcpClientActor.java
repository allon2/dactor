package cn.ymotel.dactor.action.netty.aysnsocket;


import cn.ymotel.dactor.action.AbstractJsonSupportActor;
import cn.ymotel.dactor.message.Message;
import com.alibaba.fastjson.JSON;

public class TcpClientActor extends AbstractJsonSupportActor {
    private TcpClientHelper helper;

    public TcpClientHelper getHelper() {
        return helper;
    }

    public void setHelper(TcpClientHelper helper) {
        this.helper = helper;
    }

    public Object HandleMessage(Message message) throws Exception {
        ;
        helper.AsyncSendMessage(message,JSON.toJSONString(message.getContext()));

        return null;
    }


}
