package cn.ymotel.dactor.springboot;

import cn.ymotel.dactor.action.ViewResolveActor;
import cn.ymotel.dactor.async.web.AsyncServletFilter;
import cn.ymotel.dactor.async.web.view.*;
import cn.ymotel.dactor.response.TransportResponseViewActor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
//@ConditionalOnClass(Servlet.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfigureAfter({DactorAutoConfiguration.class})
public class DactorServletAutoConfiguration implements InitializingBean {
    private TransportResponseViewActor transportResponseViewActor=null;
    public DactorServletAutoConfiguration( TransportResponseViewActor transportResponseViewActor) {
        this.transportResponseViewActor=transportResponseViewActor;
    }
//    @Bean   //相当于spring中<bean>标签
//    @ConditionalOnMissingBean(value = AsyncServletFilter.class)
//    @ConditionalOnMissingFilterBean(AsyncServletFilter.class)
////    @ConditionalOnBean(value = AsyncServletFilter.class)
//    public FilterRegistrationBean AsyncServletFilterRegistration() {
//        FilterRegistrationBean bean = new FilterRegistrationBean();
//        bean.setFilter(new AsyncServletFilter());
//        bean.addUrlPatterns("/*");
//        return bean;
//
//    }
@Bean
@ConditionalOnMissingBean
public AsyncServletFilter asyncServletFilter() {
    AsyncServletFilter filter=new AsyncServletFilter();
    return filter;
}
//    @Bean
//    @ConditionalOnMissingBean
//    public ViewResolveActor getViewResolveActor(){
//        ViewResolveActor actor=new ViewResolveActor();
//        Map viewMap=new HashMap<>();
//        actor.setViewMap(viewMap);
//        viewMap.put("default",getDefaultView());
//        viewMap.put("forward",getForwardView());
//        viewMap.put("htmlstream",getStreamView("text/html; charset=utf-8"));
//        viewMap.put("xmlstream",getStreamView("text/xml; charset=utf-8"));
//        viewMap.put("pdfstream",getStreamView("application/pdf; charset=utf-8"));
//        viewMap.put("json",getJsonView());
//        viewMap.put("download",getDownLoadView());
//        viewMap.put("stream",new StreamView());
//        viewMap.put("csv",new cn.ymotel.dactor.async.web.view.CsvView());
//        viewMap.put("img",getStreamView("images/*"));
//
//        Map suffixViewMap=new HashMap();
//        suffixViewMap.put("json",getJsonView());
//        suffixViewMap.put("xml",getStreamView("text/xml; charset=utf-8"));
//        suffixViewMap.put("html",getStreamView("text/html; charset=utf-8"));
//        actor.setSuffixMap(suffixViewMap);
//        transportResponseViewActor.getTransportMap().put("http_servlet",actor);
//        return actor;
//    }
    private DownloadView getDownLoadView(){
        DownloadView view=new DownloadView();
        return view;
    }

    private JsonView getJsonView(){
        JsonView view=new JsonView();
        view.setContentType("application/json;charset=UTF-8");
        view.setSuffix(".json");
        return view;
    }
    private UrlView getDefaultView(){
        UrlView view=new UrlView();
        view.setContentType("text/html; charset=utf-8");
        view.setPrefix("/WEB-INF/jsp/");
        view.setSuffix(".jsp");
        return view;
    }
    private ZipView getZipView(){
        ZipView view=new ZipView();
        return view;
    }
    private UrlView getForwardView(){
        UrlView view=new UrlView();
        view.setSuffix(".do");
        return view;
    }
    private StreamView getStreamView(String contenttype){
        StreamView view=new StreamView();
        view.setContentType(contenttype);
        return view;
     }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(transportResponseViewActor==null){
            return ;
        }
        ViewResolveActor actor=new ViewResolveActor();
        Map viewMap=new HashMap<>();
        actor.setViewMap(viewMap);
        viewMap.put("default",getDefaultView());
        viewMap.put("forward",getForwardView());
        viewMap.put("htmlstream",getStreamView("text/html; charset=utf-8"));
        viewMap.put("xmlstream",getStreamView("text/xml; charset=utf-8"));
        viewMap.put("pdfstream",getStreamView("application/pdf; charset=utf-8"));
        viewMap.put("json",getJsonView());
        viewMap.put("download",getDownLoadView());
        viewMap.put("stream",new StreamView());
        viewMap.put("csv",new cn.ymotel.dactor.async.web.view.CsvView());
        viewMap.put("img",getStreamView("images/*"));
        viewMap.put("zip",getZipView());

        Map suffixViewMap=new HashMap();
        suffixViewMap.put("json",getJsonView());
        suffixViewMap.put("xml",getStreamView("text/xml; charset=utf-8"));
        suffixViewMap.put("html",getStreamView("text/html; charset=utf-8"));
        actor.setSuffixMap(suffixViewMap);
        transportResponseViewActor.getTransportMap().put("http_servlet",actor);
    }
}