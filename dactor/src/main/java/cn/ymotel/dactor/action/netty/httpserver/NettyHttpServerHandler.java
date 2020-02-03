package cn.ymotel.dactor.action.netty.httpserver;

import cn.ymotel.dactor.core.ActorTransactionCfg;
import cn.ymotel.dactor.core.disruptor.MessageRingBufferDispatcher;
import cn.ymotel.dactor.message.DefaultMessage;
import cn.ymotel.dactor.message.Message;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.CharsetUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpResponseStatus.TOO_MANY_REQUESTS;

/*
 * 自定义处理的handler
 */
@ChannelHandler.Sharable
public class NettyHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest>  implements ApplicationContextAware {
    private MessageRingBufferDispatcher MessageDispatcher;




    public MessageRingBufferDispatcher getMessageDispatcher() {
        return MessageDispatcher;
    }

    public void setMessageDispatcher(MessageRingBufferDispatcher messageDispatcher) {
        MessageDispatcher = messageDispatcher;
    }

    /*
     * 处理请求
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        String transactionId=getTransactionId(fullHttpRequest.uri());
        Message message=new DefaultMessage();
//        System.out.println(fullHttpRequest.uri());
         Map params=getParams(fullHttpRequest);
        if(params!=null){
            message.getContext().putAll(params);
        }
        message.getControlData().put("_ChannelHandlerContext", channelHandlerContext);
        message.getControlData().put("transport","http");
        if(applicationContext==null){
            channelHandlerContext.writeAndFlush( responseOK(HttpResponseStatus.OK, "")).addListener(ChannelFutureListener.CLOSE);
            return ;
        }
//        System.out.println("transactionId---"+transactionId);
        if(!applicationContext.containsBean(transactionId)){
            channelHandlerContext.writeAndFlush( responseOK(HttpResponseStatus.OK, "")).addListener(ChannelFutureListener.CLOSE);
            return ;
        }
        ActorTransactionCfg cfg=(ActorTransactionCfg)applicationContext.getBean(transactionId);
//        if(cfg==null){
//            channelHandlerContext.writeAndFlush( responseOK(HttpResponseStatus.OK, null)).addListener(ChannelFutureListener.CLOSE);
//            return ;
//        }
//
       boolean b= getMessageDispatcher().startMessage(message, cfg, false);
       if(!b){
           FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, TOO_MANY_REQUESTS);
           channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
       }

    }
    public String getTransactionId(String url){
        return url.substring(1,url.lastIndexOf("."));
    }
    public Map getParams(FullHttpRequest fullHttpRequest){
        if (fullHttpRequest.method() == HttpMethod.GET) {
            return getGetParamsFromChannel(fullHttpRequest);


        } else if (fullHttpRequest.method() == HttpMethod.POST) {
            return getPostParamsFromChannel(fullHttpRequest);

        } else {
            return null;
        }
    }

    /*
     * 获取GET方式传递的参数
     */
    private Map<String, Object> getGetParamsFromChannel(FullHttpRequest fullHttpRequest) {

        Map<String, Object> params = new HashMap<String, Object>();

        if (fullHttpRequest.method() == HttpMethod.GET) {
            // 处理get请求
            QueryStringDecoder decoder = new QueryStringDecoder(fullHttpRequest.uri());
            Map<String, List<String>> paramList = decoder.parameters();
            for (Map.Entry<String, List<String>> entry : paramList.entrySet()) {
                params.put(entry.getKey(), entry.getValue().get(0));
            }
            return params;
        } else {
            return null;
        }

    }

    /*
     * 获取POST方式传递的参数
     */
    private Map<String, Object> getPostParamsFromChannel(FullHttpRequest fullHttpRequest) {

        Map<String, Object> params = new HashMap<String, Object>();

        if (fullHttpRequest.method() == HttpMethod.POST) {
            // 处理POST请求
            String strContentType = fullHttpRequest.headers().get("Content-Type").trim();
            if (strContentType.contains("x-www-form-urlencoded")) {
                params  = getFormParams(fullHttpRequest);
            } else if (strContentType.contains("application/json")) {
                try {
                    params = getJSONParams(fullHttpRequest);
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            } else {
                return null;
            }
            return params;
        } else {
            return null;
        }
    }

    /*
     * 解析from表单数据（Content-Type = x-www-form-urlencoded）
     */
    private Map<String, Object> getFormParams(FullHttpRequest fullHttpRequest) {
        Map<String, Object> params = new HashMap<String, Object>();

        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), fullHttpRequest);
        List<InterfaceHttpData> postData = decoder.getBodyHttpDatas();

        for (InterfaceHttpData data : postData) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                MemoryAttribute attribute = (MemoryAttribute) data;
                params.put(attribute.getName(), attribute.getValue());
            }
        }

        return params;
    }

    /*
     * 解析json数据（Content-Type = application/json）
     */
    private Map<String, Object> getJSONParams(FullHttpRequest fullHttpRequest) throws UnsupportedEncodingException {
        Map<String, Object> params = new HashMap<String, Object>();

        ByteBuf content = fullHttpRequest.content();
        byte[] reqContent = new byte[content.readableBytes()];
        content.readBytes(reqContent);
        String strContent = new String(reqContent, "UTF-8");
        Map jsonParams=(Map)JSON.parse(strContent);
//        JSONObject jsonParams = JSONObject.fromObject(strContent);
        for (Object key : jsonParams.keySet()) {
            params.put(key.toString(), jsonParams.get(key));
        }

        return params;
    }

    private FullHttpResponse responseOK(HttpResponseStatus status, String content) {
        ByteBuf byteBufcontent = copiedBuffer(content, CharsetUtil.UTF_8);

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, byteBufcontent);
        if (content != null) {
            response.headers().set("Content-Type", "application/json;charset=UTF-8");
            response.headers().set("Content_Length", response.content().readableBytes());
        }
        return response;
    }
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }
}