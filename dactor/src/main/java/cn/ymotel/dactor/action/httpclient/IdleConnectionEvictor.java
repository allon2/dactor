/*
 * @(#)IdleConnectionEvictor.java	1.0 2014年9月17日 上午10:56:50
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.action.httpclient;

import java.util.concurrent.TimeUnit;

import org.apache.http.nio.conn.NHttpClientConnectionManager;

/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年9月17日
 *   Modification history	
 *   {add your history}
 * </p>
 * <p>
 *
 *
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class IdleConnectionEvictor extends Thread {


    private final NHttpClientConnectionManager connMgr;

    private volatile boolean shutdown;

    public IdleConnectionEvictor(NHttpClientConnectionManager connMgr) {
        super();
        this.connMgr = connMgr;
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(5000);
                    // Close expired connections
                    connMgr.closeExpiredConnections();
                    // Optionally, close connections
                    // that have been idle longer than 5 sec
                    connMgr.closeIdleConnections(5, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            // terminate
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }


}
