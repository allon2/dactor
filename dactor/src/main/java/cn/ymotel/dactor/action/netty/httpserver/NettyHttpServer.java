package cn.ymotel.dactor.action.netty.httpserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class NettyHttpServer {

    EventLoopGroup parentGroup = new NioEventLoopGroup();
    EventLoopGroup childGroup = new NioEventLoopGroup();

    private NettyHttpServerHandler serverHandler;
    private int port;

    public NettyHttpServerHandler getServerHandler() {
        return serverHandler;
    }

    public void setServerHandler(NettyHttpServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void init() throws Exception {



        try {
            ServerBootstrap server = new ServerBootstrap();
            // 1. 绑定两个线程组分别用来处理客户端通道的accept和读写时间
            server.group(parentGroup, childGroup)
                    // 2. 绑定服务端通道NioServerSocketChannel
                    .channel(NioServerSocketChannel.class)
                    // 3. 给读写事件的线程通道绑定handler去真正处理读写
                    // ChannelInitializer初始化通道SocketChannel
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 请求解码器
                            socketChannel.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            // 将HTTP消息的多个部分合成一条完整的HTTP消息
                            socketChannel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65535));
                            // 响应转码器
                            socketChannel.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            // 解决大码流的问题，ChunkedWriteHandler：向客户端发送HTML5文件
                            socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            // 自定义处理handler
                            socketChannel.pipeline().addLast("http-server", serverHandler);
                        }
                    });

            // 4. 监听端口（服务器host和port端口），同步返回
            // ChannelFuture future = server.bind(inetHost, this.inetPort).sync();
            server.bind(this.port);
//            ChannelFuture future = server.bind(this.inetPort).sync();
            // 当通道关闭时继续向后执行，这是一个阻塞方法
//            future.channel().closeFuture().sync();
        }catch(Throwable e) {
            e.printStackTrace();
//            childGroup.shutdownGracefully();
//            parentGroup.shutdownGracefully();
        }
    }
    public void close(){
        childGroup.shutdownGracefully();
            parentGroup.shutdownGracefully();
    }
    public static void main(String[] args) {
        NettyHttpServer server = new NettyHttpServer();
        server.setPort(8080);
        try {
            server.init();
        } catch (Throwable e) {
            e.printStackTrace();
            System.err.println("exception: " + e.getMessage());
        }
        System.out.println("server close!");

    }


}