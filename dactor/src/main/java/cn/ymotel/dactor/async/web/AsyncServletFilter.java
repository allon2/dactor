package cn.ymotel.dactor.async.web;

import cn.ymotel.dactor.Constants;
import cn.ymotel.dactor.core.ActorTransactionCfg;
import cn.ymotel.dactor.core.DyanmicUrlPattern;
import cn.ymotel.dactor.core.MessageDispatcher;
import cn.ymotel.dactor.core.UrlMapping;
import cn.ymotel.dactor.message.DefaultResolveMessage;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.ActorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static cn.ymotel.dactor.core.UrlMapping.getDynamicMapping;

//@WebFilter(
//        filterName = "AsyncServletFilter",
//        urlPatterns = {"/*"},asyncSupported = true)
public class AsyncServletFilter implements Filter {
    private static final Log logger = LogFactory.getLog(AsyncServletFilter.class);
    private WebApplicationContext applicationContext;

    public WebApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(WebApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private long timeout = 1000l * 60 * 15;
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private DefaultResolveMessage defaultResolveMessage = null;
    public static String messageSourceId = "messageSource";
    private int errorcode = 429;//请求数量太多

    public UrlPathHelper getUrlPathHelper() {
        return urlPathHelper;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        init(filterConfig.getServletContext(), filterConfig.getInitParameter("errcode"));
    }

    public void init(ServletContext servletContext, String errcode) {
        this.setApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext));
        String serrcode = errcode;
        if (serrcode == null || serrcode.trim().equals("")) {
        } else {
            try {
                errorcode = Integer.parseInt(serrcode);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        urlPathHelper.setAlwaysUseFullPath(true);

        defaultResolveMessage = (DefaultResolveMessage) ActorUtils.getCacheBean(this.getApplicationContext(), "DefaultResolveMessage");
        if (defaultResolveMessage == null) {
            defaultResolveMessage = new DefaultResolveMessage();
        }
    }

    @Override
    public void doFilter(ServletRequest request1, ServletResponse response1, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) request1;
        HttpServletResponse response = (HttpServletResponse) response1;
        if (doService(request, response)) {

        } else {
            chain.doFilter(request1, response1);
        }
        /**
         * Fmt:message可以直接访问MessageSource对应的属性
         */


    }

    /**
     * @param request  HttpServletRequest
     * @param response HttpResponse
     * @return 返回结果表示 请求是否已经开始处理
     * @throws IOException      IOException
     * @throws ServletException ServletException
     */
    public boolean doService(HttpServletRequest request,
                             HttpServletResponse response) throws IOException, ServletException {
        org.springframework.context.MessageSource messageSource = (org.springframework.context.MessageSource) ActorUtils.getCacheBean(this.getApplicationContext(), messageSourceId);
        JstlUtils.exposeLocalizationContext(request, messageSource);

        String UrlPath = urlPathHelper.getLookupPathForRequest(request);


        Object requestHandler = getRequestHandler(request, UrlPath);
        if (requestHandler == null) {
//                chain.doFilter(request1,response1);
            return false;
        } else if (requestHandler instanceof HttpRequestHandler) {

            ((HttpRequestHandler) requestHandler).handleRequest(request, response);
            return true;

        } else if (requestHandler instanceof ActorTransactionCfg) {
            ActorTransactionCfg cfg = null;
            cfg = (ActorTransactionCfg) requestHandler;
            HandleAsyncContext(request, response, cfg, UrlPath,null);
            return true;
        } else if(requestHandler instanceof  MatchPair){
             MatchPair pair=(MatchPair)requestHandler;

            if (pair.getValue() instanceof HttpRequestHandler) {

                ((HttpRequestHandler) pair.getValue()).handleRequest(request, response);
                return true;

            }
            if (pair.getValue() instanceof ActorTransactionCfg) {
                ActorTransactionCfg cfg = null;
                cfg = (ActorTransactionCfg) ((MatchPair) requestHandler).getValue();
                HandleAsyncContext(request, response, cfg, UrlPath,pair.getPattern());
                return true;
            }

        }
        //其他未处理的情况，应该不会发生
        return false;
    }

    public void HandleAsyncContext(HttpServletRequest request, HttpServletResponse response, ActorTransactionCfg cfg, String UrlPath,String matternPattern) throws IOException {
        String suffix = null;
        if (UrlPath.lastIndexOf(".") >= 0) {
            suffix = UrlPath.substring(UrlPath.lastIndexOf(".") + 1);
        }

        AsyncContext asyncContext = request.startAsync(request, response);

        asyncContext.addListener(new DActorAsyncListener());
        if(cfg.getTimeout()>0){
            asyncContext.setTimeout(cfg.getTimeout());


        }else {
            asyncContext.setTimeout(timeout);
        }

        Message message = defaultResolveMessage.resolveContext(asyncContext, request, response);

        Map params = getUrlmap(UrlPath, cfg,matternPattern);

        message.getContext().putAll(params);
        message.getContext().put(Constants.METHOD, request.getMethod());
        message.getContext().put(Constants.SUFFIX, suffix);
        addTransPort(message);
        try {
            boolean b = getDispatcher(request.getServletContext()).startMessage(message, cfg, false);
            if (!b) {
                //队列满
                ((HttpServletResponse) asyncContext.getResponse()).sendError(errorcode);
//                asyncContext.complete();

                return;
            }
        } catch (Exception e) {

            asyncContext.getResponse().setContentType("text/html; charset=utf-8");
            asyncContext.getRequest().setAttribute("_EXCEPTION", e);
            //输出空白页面
            asyncContext.getResponse().getWriter().print(e.getMessage());
            asyncContext.getResponse().getWriter().flush();

            asyncContext.complete();
        }
    }

    /**
     * 设置自定义技术渠道，可继承
     *
     * @param message 已经做好的message对象
     */
    protected void addTransPort(Message message) {
        message.getControlData().put(Constants.TRANSPORT, Constants.TRANSPORT_HTTPSERVLET);

    }

    public Object getRequestHandler(HttpServletRequest request, String UrlPath) {
        String transactionId = resolveTransactionId(UrlPath, request);

        /**
         * 找不到交易码，直接输出空白结果
         */
        transactionId = ActorUtils.getBeanFromTranstionId(this.getApplicationContext(), transactionId);
        if (transactionId != null) {

            Object bean = ActorUtils.getCacheBean(this.getApplicationContext(), transactionId);
            if (bean instanceof ActorTransactionCfg) {
                if(matchDomain((ActorTransactionCfg) bean,request.getServerName())){
                    return bean;
                }
            }
        }
        return UrlPatternHandler(UrlPath, request);

    }

    private MatchPair UrlPatternHandler(String UrlPath, HttpServletRequest request) {
        //使用UrlPattern进行查找
//        Map.Entry matchentry = null;
        Map mapping = UrlMapping.getMapping();
        String serverName = request.getServerName();
        Comparator comparator= antPathMatcher.getPatternComparator(UrlPath);
        MatchPair pair=null;
        for (java.util.Iterator iter = mapping.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            if (!matchDomain(entry, serverName)) {
                continue;
            }
            if (antPathMatcher.match((String) entry.getKey(), UrlPath)) {
                    if (pair == null) {
                        pair=new MatchPair();
                        pair.setPattern((String)entry.getKey());
                        pair.setValue(entry.getValue());
                    } else {
                        //路由优先级
                        int i=comparator.compare(entry.getKey(),pair.getPattern());
                        if(i<0){
                            pair=new MatchPair();
                            pair.setPattern((String)entry.getKey());
                            pair.setValue(entry.getValue());
                        }
                }

            }
            ;
        }

        for(java.util.Iterator iter=UrlMapping.getDynamicMapping().entrySet().iterator();iter.hasNext();){
            Map.Entry entry=(Map.Entry)iter.next();
            if(!matchDomain((ActorTransactionCfg)entry.getValue(),serverName)){
                continue;
            };
           String[] patterns= ((DyanmicUrlPattern)entry.getKey()).getPatterns();
            for(int i=0;i<patterns.length;i++){
                if(antPathMatcher.match(patterns[i], UrlPath)){
                    if (pair == null) {
                        pair=new MatchPair();
                        pair.setPattern(patterns[i]);
                        pair.setValue(entry.getValue());
                    }
                    int j=comparator.compare(patterns[i],pair.getPattern());
                    if(j<0){
                        pair=new MatchPair();
                        pair.setPattern(patterns[i]);
                        pair.setValue(entry.getValue());
                    }
                };
            }

        }

        if (pair == null) {
            return null;
        }
        return pair;
    }
    private boolean matchDomain(Map.Entry  entry, String serverName) {
        if(entry.getValue() instanceof  ActorTransactionCfg) {

            ActorTransactionCfg cfg = (ActorTransactionCfg) entry.getValue();
            return matchDomain(cfg, serverName);
        }
        return true;
    }

    private boolean matchDomain(ActorTransactionCfg cfg, String serverName) {
         if (cfg.getDomain() == null||cfg.getDomain().trim().equals("")) {
            return true;
        }
        String[] domains = cfg.getDomain().split(",");
        for (int i = 0; i < domains.length; i++) {
            if (domains[i].equals(serverName)) {
                return true;
            }
        }
        return false;
    }

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public Map getUrlmap(String urlPath, ActorTransactionCfg cfg, String matternPattern) {
        if(matternPattern!=null){
            return antPathMatcher.extractUriTemplateVariables(matternPattern, urlPath);
        }
        if (cfg.getUrlPattern() == null || cfg.getUrlPattern().length==0) {
            return new HashMap();
        }
        String[] pattern = cfg.getUrlPattern();
        for (int i = 0; i < pattern.length; i++) {
            if (antPathMatcher.match(pattern[i], urlPath)) {
                return antPathMatcher.extractUriTemplateVariables(pattern[i], urlPath);
            }
            ;
        }
        return new HashMap();

    }

    private static final String DISPATCHER = WebApplicationContext.class.getName() + ".dispatchers";

    protected String resolveTransactionId(String path, HttpServletRequest request) {
        if (path == null || path.equals("/")) {
            return null;
        }
        int lastindex = path.lastIndexOf(".");
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (lastindex >= 0) {
            return path.substring(0, path.lastIndexOf(".")).replaceAll("/", ".");
        } else {

            return path.replaceAll("/", ".");
        }

    }

    public MessageDispatcher getDispatcher(ServletContext sc) {


        if (sc.getAttribute(DISPATCHER) != null) {
            return (MessageDispatcher) sc.getAttribute(DISPATCHER);
        }
        Map dispatcherMap=this.getApplicationContext().getBeansOfType(MessageDispatcher.class);
        /**
         * 取第一个
         */
        MessageDispatcher dispatcher=null;
        for(java.util.Iterator iter=dispatcherMap.entrySet().iterator();iter.hasNext();){
            Map.Entry entry=(Map.Entry)iter.next();
              dispatcher=(MessageDispatcher) entry.getValue();
              sc.setAttribute(DISPATCHER, dispatcher);

            break;
        }
//        MessageDispatcher dispatcher = (MessageDispatcher) SpringUtils.getCacheBean(this.getApplicationContext(), "MessageRingBufferDispatcher");
//        sc.setAttribute(DISPATCHER, dispatcher);

        return dispatcher;


    }
    public  class MatchPair{
        private String pattern;
        private Object value;

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
    @Override
    public void destroy() {

    }
}
