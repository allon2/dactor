package cn.ymotel.dactor.response;

import cn.ymotel.dactor.Constants;
import cn.ymotel.dactor.action.Actor;
import cn.ymotel.dactor.action.ViewResolveActor;
import cn.ymotel.dactor.action.netty.aysnsocket.TcpServerActor;
import cn.ymotel.dactor.action.netty.httpserver.HttpServerResponseActor;
import cn.ymotel.dactor.message.Message;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

public class TransportResponseViewActor implements Actor,Constants, ApplicationContextAware {
    private Map<String,Object> transportMap=new HashMap();

    public TransportResponseViewActor() {
        transportMap.put("netty_tcp",new TcpServerActor());
        transportMap.put("netty_http",new HttpServerResponseActor());
//        transportMap.put("http_servlet",new ViewResolveActor())
    }

    public Map<String, Object> getTransportMap() {
        return transportMap;
    }

    public void setTransportMap(Map transportMap) {
        this.transportMap = transportMap;
    }
    private Actor defaultViewActor=null;

    public void setDefaultViewActor(Actor defaultViewActor) {
        this.defaultViewActor = defaultViewActor;
    }

    @Override
    public Object HandleMessage(Message message) throws java.lang.Throwable {
       String transport=(String) message.getControlData().get(Constants.TRANSPORT);
       if(transport==null){
           if(defaultViewActor!=null) {
               return defaultViewActor.HandleMessage(message);
           }else{
               System.err.println("Can't find transport");
               return message;
           }
       }

       Object obj=transportMap.get(transport);
       if(obj instanceof  String){
           Actor actor= (Actor) applicationContext.getBean((String)obj);
           return   actor.HandleMessage(message);

       }
       if(obj instanceof  Actor){
           return  ((Actor)obj).HandleMessage(message);
       }
       return null;

    }
    private void addMapping(String transport,String beanId){
        transportMap.put(transport,beanId);
    }
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext= applicationContext;
    }
}
