package cn.ymotel.dactor.action.netty.aysnsocket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
 import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import org.springframework.beans.factory.InitializingBean;

public class TcpClientHelper implements InitializingBean {
	private String host="localhost";
	private int port=810;
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
	    
	public static void main(String[] args) throws Exception{
		TcpClientHelper helper=new TcpClientHelper();
		helper.afterPropertiesSet();
		helper.getChannel().writeAndFlush("aa");
	}
	public  io.netty.channel.Channel getChannel() throws InterruptedException{
//		System.out.println(bootstrap.connect(host, port));
		return  bootstrap.connect(host, port).sync().channel();
	}
	@Override
	public void afterPropertiesSet() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
         .channel(NioSocketChannel.class)
//         .handler(new TcpClientHanlder());
        .handler( new ChannelInitializer<SocketChannel>()
                {
                    @Override
                    protected void initChannel( SocketChannel ch ) throws Exception
                    {
                    	 ChannelPipeline pipeline = ch.pipeline();

                         
                         // Add the text line codec combination first,
                         pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
//                         pipeline.addLast(DECODER);
//                         pipeline.addLast(ENCODER);
                         
                        ch.pipeline().addLast(tcpClientHanlder);
                    }
        } );
        
        
        bootstrap=b;
        
//        b.connect(host, port).channel()

	}

}
