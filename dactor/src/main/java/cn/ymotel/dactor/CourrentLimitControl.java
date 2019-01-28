/*
 * @(#)CourrentLimitControl.java	1.0 2014年5月24日 上午9:18:21
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor;

import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年5月24日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class CourrentLimitControl {
    private static Map CourrentSemaphore = new java.util.concurrent.ConcurrentHashMap();

    public boolean isNotLimit(String actorId) {
        if (!CourrentSemaphore.containsKey(actorId)) {
            return true;
        }
        Semaphore semaphore = (Semaphore) CourrentSemaphore.get(actorId);
        /**
         * 没有可用，直接返回
         */
        return semaphore.tryAcquire();
//		return false;
    }

    public void release(String actorId) {
        if (!CourrentSemaphore.containsKey(actorId)) {
            return;
        }
        Semaphore semaphore = (Semaphore) CourrentSemaphore.get(actorId);
        semaphore.release();
    }

    public void init() {


    }
}
