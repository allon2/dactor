/*
 * @(#)EndActor.java	1.0 2014年5月13日 下午1:20:14
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.action;

import cn.ymotel.dactor.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年5月13日 Modification history {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class FinishActor implements Actor {
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(FinishActor.class);

    /*
     * (non-Javadoc)
     *
     * @see
     * com.ymotel.util.actor.Actor#HandleMessage(com.ymotel.util.actor.Message)
     */
    @Override
    public Object HandleMessage(Message message) throws Exception {

        long endtime = System.currentTimeMillis();
        long begin = message.getStartDate().getTime();
        if (logger.isTraceEnabled()) {
            logger.trace("HandleMessage(Message) - eclipse----time--------" + message.getTransactionId() + "--------" + (endtime - begin) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        return message;
    }

}
