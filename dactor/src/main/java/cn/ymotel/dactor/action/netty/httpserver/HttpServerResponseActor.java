package cn.ymotel.dactor.action.netty.httpserver;

import cn.ymotel.dactor.Constants;
import cn.ymotel.dactor.action.AbstractJsonSupportActor;
import cn.ymotel.dactor.action.Actor;
import cn.ymotel.dactor.exception.DActorException;
import cn.ymotel.dactor.message.Message;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class HttpServerResponseActor  implements Actor {
    @Override
    public Object HandleMessage(Message message) throws Exception {
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
//        ctx.writeAndFlush(JSON.toJSON(rtnMap)+"\r\n").addListener(ChannelFutureListener.CLOSE);

        ByteBuf content = copiedBuffer(JSON.toJSON(rtnMap)+"", CharsetUtil.UTF_8);
        FullHttpResponse response = null;
        response = responseOK(HttpResponseStatus.OK, content);

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

        return null;
    }
    private FullHttpResponse responseOK(HttpResponseStatus status, ByteBuf content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
        if (content != null) {
            response.headers().set("Content-Type", "text/plain;charset=UTF-8");
            response.headers().set("Content_Length", response.content().readableBytes());
        }
        return response;
    }


}
