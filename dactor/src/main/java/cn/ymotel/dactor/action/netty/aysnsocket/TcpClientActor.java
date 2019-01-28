package cn.ymotel.dactor.action.netty.aysnsocket;


import cn.ymotel.dactor.action.AbstractSupportActor;
import cn.ymotel.dactor.message.Message;

public class TcpClientActor extends AbstractSupportActor {
    private TcpClientHelper helper;

    public TcpClientHelper getHelper() {
        return helper;
    }

    public void setHelper(TcpClientHelper helper) {
        this.helper = helper;
    }

    public Object HandleMessage(Message message) throws Exception {

        io.netty.channel.Channel channel = helper.getChannel();
//		 channel.attr(Context).set(message);
        channel.writeAndFlush(message);
        return null;
    }


}
