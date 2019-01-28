package cn.ymotel.dactor.action.netty.aysnsocket;

import cn.ymotel.dactor.message.Message;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Sharable
public class TcpClientHanlder extends ChannelHandlerAdapter {
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(TcpClientHanlder.class);

    public static AttributeKey MESSAGE = AttributeKey.valueOf("_MESSAGE_");


    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
//		System.out.println("client reand---"+msg);
//		Message message=(Message)ctx.attr(MESSAGE).get();
        Message message = (Message) ctx.attr(MESSAGE).get();
//		System.out.println("message----"+ctx+"-----------"+message);
//		
//		
//		
//		//报文解析，之前
        message.getControlMessage().getMessageDispatcher().sendMessage(message);


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
        message.getControlMessage().getMessageDispatcher().sendMessage(message);

        ctx.close();
    }


    public void write(ChannelHandlerContext ctx, Object msg,
                      ChannelPromise promise) throws Exception {

        Message message = (Message) msg;

        ctx.attr(MESSAGE).set(message);

        ctx.writeAndFlush(Unpooled.copiedBuffer("<xml></xml>\r\n", CharsetUtil.UTF_8));

    }

}
