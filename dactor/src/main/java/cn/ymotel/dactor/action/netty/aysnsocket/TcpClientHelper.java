package cn.ymotel.dactor.action.netty.aysnsocket;

import cn.ymotel.dactor.message.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.springframework.beans.factory.InitializingBean;

public class TcpClientHelper implements InitializingBean {
    private String host = "localhost";
    private int port = 8810;
    private TcpClientHanlder tcpClientHanlder;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public TcpClientHanlder getTcpClientHanlder() {
        return tcpClientHanlder;
    }

    public void setTcpClientHanlder(TcpClientHanlder tcpClientHanlder) {
        this.tcpClientHanlder = tcpClientHanlder;
    }

    private Bootstrap bootstrap;
    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();

    public static void main(String[] args) throws Exception {
        TcpClientHelper helper = new TcpClientHelper();
        TcpClientHanlder handler=new TcpClientHanlder();
        helper.setTcpClientHanlder(handler);
        helper.afterPropertiesSet();
        helper.AsyncSendMessage(null,"safafafcafaaa\r\n");
//        helper.getChannel().writeAndFlush("safafafcafaaa\r\n");
        System.out.println("success");
    }
    public  void AsyncSendMessage(final Message message, Object obj){
         bootstrap.connect(host, port).addListener(new ChannelFutureListener(){
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("connected");
                io.netty.channel.Channel channel= future.channel();
                channel.attr(TcpClientHanlder.MESSAGE).setIfAbsent(message);
                channel.writeAndFlush(obj+"\r\n");
            }
        });
    }
    public io.netty.channel.Channel getChannel() throws InterruptedException {
        return bootstrap.connect(host, port).sync().channel();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
//         .handler(new TcpClientHanlder());
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();


                        // Add the text line codec combination first,
                        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                         pipeline.addLast(DECODER);
                         pipeline.addLast(ENCODER);

                       pipeline.addLast(tcpClientHanlder);
                    }
                });


        bootstrap = b;

//        b.connect(host, port).channel()

    }

}
