package cn.ymotel.dactor.core;

import cn.ymotel.dactor.async.web.StaticResourceRequestHandler;

import java.util.Map;

public class UrlMapping {
    private static java.util.Map<String,Object> Urlmapping =new java.util.concurrent.ConcurrentHashMap<>();
//    static{
////        Urlmapping.put("/static/**",new StaticResourceRequestHandler());
//        ///**
////        mapping.put("/**",new StaticResourceRequestHandler());
//    }

    public static void addMapping(String urlpattern ,ActorTransactionCfg cfg){
        if(urlpattern==null||urlpattern.trim().equals("")){
            return ;
        }
//        System.out.println("add Mapping---"+urlpattern);
        String[] patterns=urlpattern.split(",");
        for(int i=0;i<patterns.length;i++){
            if(patterns[i]==null||patterns[i].trim().equals("")){
                continue;
            }
            Urlmapping.put(patterns[i].trim(),cfg);

        }
    }

    public static Object getMapping(String urlpattern){
       return  Urlmapping.get(urlpattern);
    }
    public static Map getMapping(){
        return Urlmapping;
    }
    public static void addstaticpath(String urlpath){

    }
}
