/*
 * @(#)HttpClientHelper.java	1.0 2014年9月17日 上午10:58:07
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.action.httpclient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.*;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.*;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.InitializingBean;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;


/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年9月17日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class HttpClientHelper implements InitializingBean {
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(HttpClientHelper.class);


    private CloseableHttpAsyncClient httpclient;
    /**
     * 默认是100
     */
    private int maxTotal = 100;

    /**
     * @return the maxTotal
     */
    public int getMaxTotal() {
        return maxTotal;
    }

    /**
     * @param maxTotal the maxTotal to set
     */
    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    /**
     * @return the httpclient
     */
    public CloseableHttpAsyncClient getHttpclient() {
        return httpclient;
    }

    public void shutdown() {
        connEvictor.shutdown();
        try {
            httpclient.close();
        } catch (IOException e) {
            if (logger.isTraceEnabled()) {
                logger.trace("shutdown()"); //$NON-NLS-1$
            }
        }
    }

    ;


    private IdleConnectionEvictor connEvictor;

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
        HostnameVerifier hostnameVerifier = new TrustAllHostnames();
//        SSLContext sslcontext = SSLContexts.createSystemDefault();
        SSLContext sslcontext =SSLContexts.custom()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();


        Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create()
                .register("http", NoopIOSessionStrategy.INSTANCE)
                .register("https", new SSLIOSessionStrategy(sslcontext, hostnameVerifier))
                .build();

        PoolingNHttpClientConnectionManager cm = new PoolingNHttpClientConnectionManager(ioReactor,sessionStrategyRegistry);

        cm.setMaxTotal(maxTotal);
        cm.setDefaultMaxPerRoute(maxTotal);
        if (logger.isTraceEnabled()) {
            logger.trace("afterPropertiesSet() - " + cm.getDefaultConnectionConfig()); //$NON-NLS-1$
        }

//        CookieSpecProvider easySpecProvider = new CookieSpecProvider() {
//
//            public CookieSpec create(HttpContext context) {
//
//                return new DefaultCookieSpec() {
//                    @Override
//                    public void validate(Cookie cookie, CookieOrigin origin)
//                            throws MalformedCookieException {
//                        // Oh, I am easy
//                    }
//                };
//            }
//
//        };

//        RequestConfig customizedRequestConfig = RequestConfig.custom()
//                .setCookieSpec("easy")
//                .build();
//        HttpClientBuilder customizedClientBuilder =
//                HttpClients.custom().setDefaultRequestConfig(customizedRequestConfig);
//
//        Registry<CookieSpecProvider> reg = RegistryBuilder.<CookieSpecProvider>create()
//                .register(CookieSpecs.DEFAULT,
//                        new org.apache.http.impl.cookie.DefaultCookieSpecProvider())
//                .register(CookieSpecs.BROWSER_COMPATIBILITY,
//                        new BrowserCompatSpecFactory())
//                .register("mySpec", easySpecProvider)
//                .register("mySpec",new BestMatchSpec(),new DefaultCookieSpecProvider())
//                .build();
//
//        RFC6265CookieSpecProvider cookieSpecProvider=new RFC6265CookieSpecProvider();
//
//        Registry<CookieSpecProvider> reg = RegistryBuilder.<CookieSpecProvider>create()
//                .register(CookieSpecs.DEFAULT,
//                        new DefaultCookieSpecProvider())
//                .register(CookieSpecs.STANDARD,
//                        new RFC6265CookieSpecProvider())
//                .register("easy", easySpecProvider)
//                .build();
//
//        RequestConfig requestConfig = RequestConfig.custom()
////                .setCookieSpec("easy")
//                .build();
        class EasyCookieSpec extends DefaultCookieSpec {
            @Override
            public void validate(Cookie arg0, CookieOrigin arg1) throws MalformedCookieException {
                //allow all cookies
            }
        }

        class EasySpecProvider implements CookieSpecProvider {
            @Override
            public CookieSpec create(HttpContext context) {
                return new EasyCookieSpec();
            }
        }

        Registry<CookieSpecProvider> r = RegistryBuilder.<CookieSpecProvider>create()
                .register("easy", new EasySpecProvider())
                .build();

        CookieStore cookieStore = new BasicCookieStore();

        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec("easy")
                .build();
        HttpAsyncClientBuilder builder = HttpAsyncClients.custom()
                .setDefaultCookieStore(cookieStore)
                .setDefaultCookieSpecRegistry(r)
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(cm).setSSLStrategy(SSLIOSessionStrategy.getDefaultStrategy());


        httpclient = builder.build();
//     httpclient = HttpAsyncClients.custom()
//     .setConnectionManager(cm).setSSLStrategy(SSLIOSessionStrategy.getDefaultStrategy()).setProxy(proxy)
//     .build();

        httpclient.start();
        connEvictor = new IdleConnectionEvictor(cm);
        connEvictor.start();

    }

}
