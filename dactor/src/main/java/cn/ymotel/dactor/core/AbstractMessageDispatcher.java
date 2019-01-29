/*
 * @(#)AbstractMessageDispatcher.java	1.0 2014年9月18日 下午12:43:44
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.core;

import cn.ymotel.dactor.message.ControlMessage;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.message.SpringControlMessage;
import cn.ymotel.dactor.workflow.WorkFlowProcess;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年9月18日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractMessageDispatcher implements MessageDispatcher, ApplicationContextAware {
    private ApplicationContext appcontext = null;
    private final org.apache.commons.logging.Log logger = LogFactory.getLog(AbstractMessageDispatcher.class);


    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.appcontext = applicationContext;

    }

    public ApplicationContext getApplicationContext() {
        return appcontext;
    }

    /**
     * 是否阻塞
     *
     * @param message 需要处理的信息
     * @param blocked 是否阻塞
     * @return 是否阻塞
     * @version 1.0
     * @since 1.0
     */
    public abstract boolean putMessageInDispatcher(Message message, boolean blocked);

    /* (non-Javadoc)
     * @see MessageDispatcher#sendMessage(Message)
     */
    @Override
    public void sendMessage(Message message) {
        ControlMessage cMsg = message.getControlMessage();

        if (cMsg == null) {
            return;
        }
        //为空，则说明，没有需要处理的数据
        if (message.getControlMessage().getProcessStructure()==null) {
            return;

        }
        WorkFlowProcess.processGetToBeanId(message.getControlMessage(), message, appcontext);


        this.putMessageInDispatcher(message, true);

    }

    /* (non-Javadoc)
     * @see MessageDispatcher#startMessage(Message, java.lang.String)
     */


    /* (non-Javadoc)
     * @see MessageDispatcher#startMessage(Message, ActorTransactionCfg, boolean)
     */
    @Override
    public void startMessage(Message message, ActorTransactionCfg actorcfg,
                             boolean blocked) throws Exception {
        this.startMessage(message, actorcfg, actorcfg.getChain(), blocked);

    }

    /* (non-Javadoc)
     * @see MessageDispatcher#startMessage(Message, ActorTransactionCfg, ActorChainCfg)
     */
    @Override
    public void startMessage(Message message, ActorTransactionCfg actorcfg, ActorChainCfg chain) throws Exception {
        startMessage(message, actorcfg, chain, true);

    }

    /* (non-Javadoc)
     * @see MessageDispatcher#startMessage(Message, ActorTransactionCfg, ActorChainCfg, boolean)
     */
    @Override
    public void startMessage(Message message, ActorTransactionCfg actorcfg, ActorChainCfg chain, boolean blocked)
            throws Exception {

        SpringControlMessage cMsg = new SpringControlMessage();
        cMsg.setMessageDispatcher(this);
        cMsg.init(actorcfg, chain);

        message.setControlMessage(cMsg);
        boolean isSuccess = putMessageInDispatcher(message, blocked);

        if (!isSuccess) {
            throw new Exception("系统繁忙");
        }


    }

    public void startMessage(Message message, ActorTransactionCfg actorcfg) throws Exception {
        startMessage(message, actorcfg, true);
    }
}
