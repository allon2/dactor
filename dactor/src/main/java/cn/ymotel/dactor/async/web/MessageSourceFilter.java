package cn.ymotel.dactor.async.web;

import cn.ymotel.dactor.ActorUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.support.JstlUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 如果需要，请在配置中添加
 * compile group: 'taglibs', name: 'standard', version:'1.1.2'
 *     compile group: 'jstl', name: 'jstl', version:'1.2'
 */
public class MessageSourceFilter implements Filter {
    public static String messageSourceId = "messageSource";
    private WebApplicationContext applicationContext;
    public WebApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(WebApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.setApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext()));

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        org.springframework.context.MessageSource messageSource = (org.springframework.context.MessageSource) ActorUtils.getCacheBean(this.getApplicationContext(), messageSourceId);
        JstlUtils.exposeLocalizationContext((HttpServletRequest)request, messageSource);
        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
