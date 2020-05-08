package cn.ymotel.dactor;

import cn.ymotel.dactor.message.Message;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ActorUtils {
    private static java.util.concurrent.ConcurrentHashMap cachedBean=new java.util.concurrent.ConcurrentHashMap();

    /**
     * 避免Spring getBean的锁
     * @param applicationContext application对象
     * @param beanName  上下文名称
     * @return bean对象
     */
    public static Object getCacheBean(ApplicationContext applicationContext,String beanName ){
        Object bean=cachedBean.get(beanName);
        if(bean!=null){
            return bean;
        }
        if(bean==null){
            if(applicationContext.containsBean(beanName)){
              bean= applicationContext.getBean(beanName);
            }
         }

        if(bean==null){
            return null;
        }
        cachedBean.put(beanName,bean);

        return bean;

    }
    public static boolean containBean(ApplicationContext applicationContext,String beanName){
        Object bean=getCacheBean(applicationContext,beanName);
        if(bean==null){
            return false;
        }
        return true;
    }
    public static String getBeanFromTranstionId(ApplicationContext applicationContext,String beanName){
       Object obj=null;
        if(beanName==null){
            return null;
        }
        for(;;){
//            System.out.println("beanName---"+beanName);
            obj= getCacheBean(applicationContext,beanName);
            if(obj!=null){
                return beanName;
            }
            if(beanName.indexOf(".")>=0){
                beanName=beanName.substring(0,beanName.lastIndexOf("."));
            }else{
                return null;
            }
        }

    }
    public static String getDataKey(Message message,String defaultKey){
        Map stempMap= message.getControlMessage().getProcessStructure().getStepMap();
        if(stempMap==null||stempMap.get("data")==null){
            return defaultKey;
        }
        return (String)stempMap.get("data");
    }

    public static <T> T ConvertData(Object value,Class<T> clazz){
        if(value ==null){
            return null;
        }
        if(Long.class.isAssignableFrom(clazz)){
            return (T) new Long(value.toString());
        }
        if(Short.class.isAssignableFrom(clazz)){
            return (T) new Short(value.toString());
        }
        if(Integer.class.isAssignableFrom(clazz)){
            return (T) new Integer(value.toString());
        }
        if(BigDecimal.class.isAssignableFrom(clazz)){
            return (T) new BigDecimal(value.toString());
        }
        if(String.class.isAssignableFrom(clazz)){
            return (T)  value.toString();
        }
        if(Double.class.isAssignableFrom(clazz)){
            return (T) new Double(value.toString());
        }
        if(Float.class.isAssignableFrom(clazz)){
            return (T) new Float(value.toString());
        }
        if(Enum.class.isAssignableFrom(clazz)){
            return  (T) Enum.valueOf((Class<? extends Enum>)clazz,value.toString());
        }
        return (T) value;
    }

}
