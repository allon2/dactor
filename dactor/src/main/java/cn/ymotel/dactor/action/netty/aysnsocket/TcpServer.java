package cn.ymotel.dactor.action.netty.aysnsocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.InitializingBean;

public class TcpServer implements InitializingBean {
    private int port = 8810;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private TcpServerHandler tcpServerhandler;

    public TcpServerHandler getTcpServerhandler() {
        return tcpServerhandler;
    }

    public void setTcpServerhandler(TcpServerHandler tcpServerhandler) {
        this.tcpServerhandler = tcpServerhandler;
    }

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();

    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        TcpServer helper = new TcpServer();
        TcpServerHandler handler=new TcpServerHandler();
        helper.setTcpServerhandler(handler);
        helper.afterPropertiesSet();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//		 EventLoopGroup bossGroup = new NioEventLoopGroup(1);
//	        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(
                            new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
//                                	ch.pipeline().addLast(new XmlFrameDecoder(8192));
                                    // Add the text line codec combination first,
                                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                                    // the encoder and decoder are static as these are sharable
                                    ch.pipeline().addLast(DECODER);
                                    ch.pipeline().addLast(ENCODER);
                                    ch.pipeline().addLast(tcpServerhandler);
                                }
                            } //
                    );


//	             .childHandler(new TcpServerHandler());
            b.bind(port);
//	            channel= b.bind(port).channel();
//	            b.bind(port).sync().channel().closeFuture().sync();
        } finally {
//	            bossGroup.shutdownGracefully();
//	            workerGroup.shutdownGracefully();
        }

    }

}
