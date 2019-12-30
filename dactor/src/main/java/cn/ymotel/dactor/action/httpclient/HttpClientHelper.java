/*
 * @(#)HttpClientHelper.java	1.0 2014年9月17日 上午10:58:07
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.action.httpclient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.*;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.InitializingBean;

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

        PoolingNHttpClientConnectionManager cm = new PoolingNHttpClientConnectionManager(ioReactor);

        cm.setMaxTotal(maxTotal);
        cm.setDefaultMaxPerRoute(maxTotal);
        if (logger.isTraceEnabled()) {
            logger.trace("afterPropertiesSet() - " + cm.getDefaultConnectionConfig()); //$NON-NLS-1$
        }

        CookieSpecProvider easySpecProvider = new CookieSpecProvider() {

            public CookieSpec create(HttpContext context) {

                return new BrowserCompatSpec() {
                    @Override
                    public void validate(Cookie cookie, CookieOrigin origin)
                            throws MalformedCookieException {
                        // Oh, I am easy
                    }
                };
            }

        };
        Registry<CookieSpecProvider> reg = RegistryBuilder.<CookieSpecProvider>create()
                .register(CookieSpecs.BEST_MATCH,
                        new BestMatchSpecFactory())
                .register(CookieSpecs.BROWSER_COMPATIBILITY,
                        new BrowserCompatSpecFactory())
                .register("mySpec", easySpecProvider)
                .build();

        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec("mySpec")
                .build();

        HttpAsyncClientBuilder builder = HttpAsyncClients.custom()
                .setDefaultCookieSpecRegistry(reg)
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
