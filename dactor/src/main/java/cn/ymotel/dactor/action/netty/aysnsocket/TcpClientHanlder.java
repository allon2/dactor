package cn.ymotel.dactor.action.netty.aysnsocket;

import cn.ymotel.dactor.message.Message;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

@Sharable
public class TcpClientHanlder extends SimpleChannelInboundHandler {
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(TcpClientHanlder.class);

    public static AttributeKey MESSAGE = AttributeKey.valueOf("_MESSAGE_");


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("client reand---"+msg);
        Map obj=(Map)JSON.parse((String)msg);
        Message message=(Message) ctx.channel().attr(MESSAGE).get();
        if(message!=null) {
            message.getContext().putAll(obj);
            message.getControlMessage().getMessageDispatcher().sendMessage(message);
        }
        ctx.channel().close();
    }


    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//		 System.out.println("client-----channelReadComplete");
        ctx.close();
    }


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        if (logger.isTraceEnabled()) {
            logger.trace("exceptionCaught(ChannelHandlerContext, Throwable)"); //$NON-NLS-1$
        }
        Message message = (Message) ctx.attr(MESSAGE).get();
        message.setException(cause);
        message.getControlMessage().getMessageDispatcher().sendMessage(message);

        ctx.close();
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            Message message = (Message) ctx.attr(MESSAGE).get();
            message.getControlMessage().getMessageDispatcher().sendMessage(message);

            ctx.close();
        }

    }



}
