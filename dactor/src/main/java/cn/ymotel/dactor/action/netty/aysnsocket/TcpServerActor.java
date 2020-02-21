package cn.ymotel.dactor.action.netty.aysnsocket;

import cn.ymotel.dactor.Constants;
import cn.ymotel.dactor.action.AbstractJsonSupportActor;
import cn.ymotel.dactor.action.Actor;
import cn.ymotel.dactor.exception.DActorException;
import cn.ymotel.dactor.message.Message;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

public class TcpServerActor implements Actor {
    @Override
    public Object HandleMessage(Message message) throws Exception {
//        System.out.println("in server"+message.getContext());
        ChannelHandlerContext ctx=(ChannelHandlerContext)message.getControlData().get("_ChannelHandlerContext");
        Map obj=null;
        if(message.getContext().get(Constants.CONTENT)!=null){
            obj=(Map)message.getContext().get(Constants.CONTENT);
        }else{
            obj=message.getContext();
        }
        Map rtnMap=new HashMap();
        Map head=new HashMap();
        rtnMap.put("head",head);
        if (message.getException() == null) {
            head.put("errcode", "0");
            head.put("errmsg", "成功");

        }else if (message.getException() instanceof DActorException) {
            DActorException ex = (DActorException) message.getException();
            head.put("errcode", ex.getErrorCode());//一般错误
            head.put("errmsg", ex.getMessage());

        } else {
            head.put("errcode", "10000");//一般错误
            head.put("errmsg", message.getException().getMessage());
        }
        rtnMap.put("body",obj);
        ctx.writeAndFlush(JSON.toJSON(rtnMap)+"\r\n").addListener(ChannelFutureListener.CLOSE);
    return null;
    }


}
