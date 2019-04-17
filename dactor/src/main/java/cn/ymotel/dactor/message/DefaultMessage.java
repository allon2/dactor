/*
 * @(#)DefaultMessage.java	1.0 2014年5月20日 下午2:40:41
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.message;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年5月20日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class DefaultMessage implements Message {
    private java.util.Date startDate = new java.util.Date();

    public DefaultMessage() {
        super();
    }

    /* (non-Javadoc)
     * @see com.ymotel.util.actor.Message#getStartDate()
     */
    @Override
    public Date getStartDate() {
        return startDate;
    }

    private Object origSource = null;

    /* (non-Javadoc)
     * @see com.ymotel.util.actor.Message#getOrigSource()
     */
    @Override
    public Object getOrigSource() {
        return origSource;
    }

    /* (non-Javadoc)
     * @see com.ymotel.util.actor.Message#setOrigSource(java.lang.Object)
     */
    @Override
    public void setOrigSource(Object obj) {
        origSource = obj;

    }

    private Map context = new HashMap();

    /* (non-Javadoc)
     * @see com.ymotel.util.actor.Message#getContext()
     */
    @Override
    public Map getContext() {
        return context;
    }

    public void setContext(Map context) {
        this.context = context;
    }

    private SpringControlMessage cMsg;

    /* (non-Javadoc)
     * @see com.ymotel.util.actor.Message#getControlMessage()
     */
    @Override
    public SpringControlMessage getControlMessage() {
        return cMsg;
    }

    /* (non-Javadoc)
     * @see com.ymotel.util.actor.Message#setControlMessage(com.ymotel.util.actor.ControlMessage)
     */
    @Override
    public void setControlMessage(SpringControlMessage message) {
        cMsg = message;

    }

    private Throwable exception;

    /* (non-Javadoc)
     * @see com.dragon.actor.Message#setException(java.lang.Exception)
     */
    @Override
    public void setException(Throwable exception) {
        this.exception = exception;

    }

    /* (non-Javadoc)
     * @see com.dragon.actor.Message#getException()
     */
    @Override
    public Throwable getException() {
        return exception;
    }

    /* (non-Javadoc)
     * @see Message#getTransactionId()
     */
    @Override
    public String getTransactionId() {
        return this.getControlMessage().getSourceCfg().getId();
//		return this.getControlMessage().getSourceId();
    }


    private Message parentMessage;


    /**
     * @return the parentMessage
     */
    public Message getParentMessage() {
        return parentMessage;
    }

    /**
     * @param parentMessage the parentMessage to set
     */
    public void setParentMessage(Message parentMessage) {
        this.parentMessage = parentMessage;
    }

    List<Message> childs = null;


    /**
     * @return the childs
     */
    public List<Message> getChilds() {
        return childs;
    }

    public void setChilds(List<Message> childs) {
        this.childs = childs;
    }

    public java.util.concurrent.atomic.AtomicInteger childCount;

    /**
     * @return the childCount
     */
    public java.util.concurrent.atomic.AtomicInteger getChildCount() {
        return childCount;
    }

    /**
     * @param childCount the childCount to set
     */
    public void setChildCount(int childCount) {
        this.childCount = new java.util.concurrent.atomic.AtomicInteger(childCount);
    }

    @Override
    public Map getCaseInsensitivegetContext() {
        return new CaseInsensitiveMap(context);
    }

    private Object user;
    public Object getUser(){
        return user;
    };
    public void setUser(Object user){
        this.user=user;
    }
    private Map controlMap=new HashMap();
    @Override
    public Map getControlData() {
        return controlMap;
    }

    @Override
    public Map getCaseInsensitivegetControlData() {
        return new CaseInsensitiveMap(controlMap);
    }

    ;
}
