package cn.ymotel.dactor.core;

import cn.ymotel.dactor.async.web.StaticResourceRequestHandler;

import java.util.Map;

public class UrlMapping {
    private static java.util.Map<String,Object> Urlmapping =new java.util.concurrent.ConcurrentHashMap<>();
    private static java.util.Map<DyanmicUrlPattern,ActorTransactionCfg> DyanmicUrlmapping =new java.util.concurrent.ConcurrentHashMap<>();

//    static{
////        Urlmapping.put("/static/**",new StaticResourceRequestHandler());
//        ///**
////        mapping.put("/**",new StaticResourceRequestHandler());
//    }
    public static void addDyanmicMapping(DyanmicUrlPattern urlpattern,ActorTransactionCfg cfg){
    if(urlpattern==null){
        return ;
    }
        DyanmicUrlmapping.put(urlpattern,cfg);
    }

    public static void addMapping(String[] urlpattern ,ActorTransactionCfg cfg){
        if(urlpattern==null||urlpattern.length==0){
            return ;
        }
//        System.out.println("add Mapping---"+urlpattern);
        for(int i=0;i<urlpattern.length;i++){
            if(urlpattern[i]==null||urlpattern[i].trim().equals("")){
                continue;
            }
            Urlmapping.put(urlpattern[i].trim(),cfg);

        }
    }
    public static Map<DyanmicUrlPattern,ActorTransactionCfg> getDynamicMapping(){
        return  DyanmicUrlmapping;
    }
    public static Object getMapping(String urlpattern){
       return  Urlmapping.get(urlpattern);
    }
    public static Map getMapping(){
        return Urlmapping;
    }
    public static void addStaticPath(String urlpath){
        Urlmapping.put(urlpath,new StaticResourceRequestHandler());
    }
}
