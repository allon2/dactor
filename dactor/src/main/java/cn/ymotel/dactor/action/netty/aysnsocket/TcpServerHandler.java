package cn.ymotel.dactor.action.netty.aysnsocket;

import cn.ymotel.dactor.core.disruptor.MessageRingBufferDispatcher;
import cn.ymotel.dactor.message.DefaultResolveMessage;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Sharable
public class TcpServerHandler extends ChannelHandlerAdapter implements ApplicationContextAware {
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(TcpServerHandler.class);

    private DefaultResolveMessage defaultResolveMessage;
    private MessageRingBufferDispatcher MessageDispatcher;


    public DefaultResolveMessage getDefaultResolveMessage() {
        return defaultResolveMessage;
    }

    public void setDefaultResolveMessage(DefaultResolveMessage defaultResolveMessage) {
        this.defaultResolveMessage = defaultResolveMessage;
    }

    public MessageRingBufferDispatcher getMessageDispatcher() {
        return MessageDispatcher;
    }

    public void setMessageDispatcher(MessageRingBufferDispatcher messageDispatcher) {
        MessageDispatcher = messageDispatcher;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
//		System.out.println("server----channelRead---aaaaaaaaaa--"+msg +"-----");
//		Message message=new DefaultMessage();
//		
//		message.getContext().put("_ChannelHandlerContext", ctx);
        String transactionId = "";
//		 ActorTransactionCfg cfg=(ActorTransactionCfg)applicationContext.getBean(transactionId);
//		
//		
//		MessageDispatcher.startMessage(message, cfg, false);
//		ctx.writeAndFlush("aaaaaaaaaaa.\r\n").addListener(ChannelFutureListener.CLOSE);
        ctx.writeAndFlush(Unpooled.copiedBuffer("111111sssssssssssssssssssssssssssssssss11 \r\n", CharsetUtil.UTF_8)).addListener(ChannelFutureListener.CLOSE);

//		System.out.println("server----writer---aaaaaaaaaa--");


    }


    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//		  ctx.flush();
    }


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        if (logger.isTraceEnabled()) {
            logger.trace("exceptionCaught(ChannelHandlerContext, Throwable) - " + cause); //$NON-NLS-1$
        }
        ctx.close();
    }


    private ApplicationContext applicationContext;


    public void setApplicationContext(ApplicationContext arg0)
            throws BeansException {
        applicationContext = arg0;
    }


}
