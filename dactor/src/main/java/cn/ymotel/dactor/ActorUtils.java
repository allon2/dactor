package cn.ymotel.dactor;

import cn.ymotel.dactor.core.ActorChainCfg;
import cn.ymotel.dactor.core.ActorTransactionCfg;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.spring.annotaion.AfterChain;
import cn.ymotel.dactor.spring.annotaion.BaseChain;
import cn.ymotel.dactor.spring.annotaion.BeforeChain;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;

public class ActorUtils {
    private static java.util.concurrent.ConcurrentHashMap cachedBean=new java.util.concurrent.ConcurrentHashMap();

    public static Map<String, Object> getAnnotations(ApplicationContext applicationContext,Class<? extends Annotation> annotationType){
        Map map=applicationContext.getBeansWithAnnotation(annotationType);
        Map<String,  Object> rtnMap=new HashMap();
        for(java.util.Iterator iter=map.entrySet().iterator();iter.hasNext();){
            Map.Entry entry=(Map.Entry)iter.next();
            rtnMap.put((String)entry.getKey(),entry.getValue());
        }
        return  rtnMap;
    }
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
    /**
     * 获取客户端IP
     *
     * <p>
     * 默认检测的Header:
     *
     * <pre>
     * 1、X-Forwarded-For
     * 2、X-Real-IP
     * 3、Proxy-Client-IP
     * 4、WL-Proxy-Client-IP
     * </pre>
     *
     * <p>
     * otherHeaderNames参数用于自定义检测的Header<br>
     * 需要注意的是，使用此方法获取的客户IP地址必须在Http服务器（例如Nginx）中配置头信息，否则容易造成IP伪造。
     * </p>
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @return IP地址
     */
    public static String getClientIP(HttpServletRequest request) {
        String[] headers = { "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR" };

        return getClientIPByHeader(request, headers);
    }

    /**
     * 获取客户端IP
     *
     * <p>
     * headerNames参数用于自定义检测的Header<br>
     * 需要注意的是，使用此方法获取的客户IP地址必须在Http服务器（例如Nginx）中配置头信息，否则容易造成IP伪造。
     * </p>
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @param headerNames 自定义头，通常在Http服务器（例如Nginx）中配置
     * @return IP地址
     * @since 4.4.1
     */
    public static String getClientIPByHeader(HttpServletRequest request, String... headerNames) {
        String ip;
        for (String header : headerNames) {
            ip = request.getHeader(header);
            if(ip==null){
                continue;
            }
            if (false == isUnknow(ip)) {
                return getMultistageReverseProxyIp(ip);
            }
        }

        ip = request.getRemoteAddr();
        return getMultistageReverseProxyIp(ip);
    }

    /**
     * 检测给定字符串是否为未知，多用于检测HTTP请求相关<br>
     *
     * @param checkString 被检测的字符串
     * @return 是否未知
     */
    private static boolean isUnknow(String checkString) {
        return StringUtils.isBlank(checkString)||"unknown".equalsIgnoreCase(checkString);
    }
    // --------------------------------------------------------- Private methd start
    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     */
    private static String getMultistageReverseProxyIp(String ip) {
        // 多级反向代理检测
        if (ip != null && ip.indexOf(",") > 0) {
            final String[] ips = ip.trim().split(",");
            for (String subIp : ips) {
                if (false == isUnknow(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return ip;
    }
    public static ActorTransactionCfg createDefaultChainActor(ApplicationContext applicationContext, String defaultchain,String endBeanId) {
        ActorTransactionCfg cfg=new ActorTransactionCfg();
        cfg.setHandleException(true);
        if(endBeanId!=null) {
            cfg.setEndBeanId(endBeanId);
        }
        cfg.setApplicationContext(applicationContext);
//        cfg.setEndBeanId("TransportResponseViewActor");
//        cfg.setId("chainactor");
        Map map=new HashMap<>();
        map.putAll(createChainActor("beginActor","placeholderActor",defaultchain, BeforeChain.class,applicationContext));
        map.putAll(createChainActor("placeholderActor",null,defaultchain, AfterChain.class,applicationContext));
        cfg.setSteps(map);
        return cfg;
    }
    private static TreeMap shortChain(Class<? extends Annotation> annotationType,ApplicationContext applicationContext){
        Map<String,Object> annotationMap=(Map)ActorUtils.getAnnotations(applicationContext,annotationType);
        TreeMap treeMap=new TreeMap(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Order order1 = AnnotatedElementUtils.findMergedAnnotation(annotationMap.get(o1).getClass(), Order.class);
                ;
                Order order2=AnnotatedElementUtils.findMergedAnnotation(annotationMap.get(o2).getClass(), Order.class);
                if(order1.value()>=order2.value()){
                    return  -1;
                }else{
                    return  1;
                }

            }
        });
        treeMap.putAll(annotationMap);
        return treeMap;
    }
    private static Map createChainActor(String preBeanId, String endBeanId, String chainid,Class<? extends Annotation> annotationType,ApplicationContext applicationContext){

        Map<String,Object> annotationMap=shortChain(annotationType,applicationContext);
        Map rtnMap=new HashMap();
        for(java.util.Iterator iter=annotationMap.entrySet().iterator();iter.hasNext();){
            Map.Entry entry=(Map.Entry)iter.next();
            String key=(String)entry.getKey();
            Object value=entry.getValue();
            BaseChain baseChain= AnnotatedElementUtils.findMergedAnnotation(value.getClass(), BaseChain.class);
            String[] chains=baseChain.chain();
            if(chains!=null&&chains.length>0){
                Set set=new HashSet();
                for(int i=0;i<chains.length;i++){
                    set.add(chains[i]);
                }
                if(!set.contains(chainid)){
                    continue;
                }
            }

            List list=new ArrayList<>();
            Map property = new HashMap();
            property.put("fromBeanId",preBeanId);
            property.put("toBeanId",key);
            property.put("conditon","");
            list.add(property);
            rtnMap.put(preBeanId,list);
            preBeanId=key;
        }
        if(endBeanId!=null) {
            List list = new ArrayList<>();
            Map property = new HashMap();
            property.put("fromBeanId", preBeanId);
            property.put("toBeanId", endBeanId);
            property.put("conditon", "");
            list.add(property);
            rtnMap.put(preBeanId, list);
        }
        return rtnMap;
    }
    public static ActorChainCfg creatDefaultChain(ApplicationContext applicationContext,String beanId,String endBeanId){
        ActorChainCfg cfg=new ActorChainCfg();
        cfg.setId(beanId);
        List ls=new ArrayList();
        ActorTransactionCfg cfg1=  createDefaultChainActor(applicationContext,beanId,endBeanId);
        cfg1.setApplicationContext(applicationContext);
        try {
            cfg1.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ls.add(cfg1);
        cfg.setChain(ls);
        return cfg;
    }


}
