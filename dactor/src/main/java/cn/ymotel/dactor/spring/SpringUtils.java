package cn.ymotel.dactor.spring;

import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class SpringUtils {
    private static java.util.concurrent.ConcurrentHashMap cachedBean=new java.util.concurrent.ConcurrentHashMap();

    /**
     * 避免Spring getBean的锁
     * @param applicationContext
     * @param beanName
     * @return
     */
    public static Object getCacheBean(ApplicationContext applicationContext,String beanName ){
        Object bean=cachedBean.get(beanName);
        if(bean!=null){
            return bean;
        }
        if(bean==null){
            bean= applicationContext.getBean(beanName);
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
    public static void main(String[] args){
        System.out.println(cachedBean);
        Map t=new HashMap();
        t.put("k",null);
        cachedBean.put("k","");
    }
}
