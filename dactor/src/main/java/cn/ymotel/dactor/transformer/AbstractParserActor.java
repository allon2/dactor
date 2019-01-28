/*
 * @(#)AbstractParserActor.java	1.0 2014年10月4日 下午11:44:06
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.transformer;

import cn.ymotel.dactor.action.AbstractSupportActor;
import cn.ymotel.dactor.message.Message;
import ognl.Ognl;

import java.util.Map;

/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年10月4日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractParserActor extends AbstractSupportActor {
    private boolean cleanSource = true;
    private boolean putContext = true;

    private String errorKey;
    private String errorMsg;

    /**
     * @return the errorKey
     */
    public String getErrorKey() {
        return errorKey;
    }


    /**
     * @param errorKey the errorKey to set
     */
    public void setErrorKey(String errorKey) {
        this.errorKey = errorKey;
    }


    /**
     * @return the errorMsg
     */
    public String getErrorMsg() {
        return errorMsg;
    }


    /**
     * @param errorMsg the errorMsg to set
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }


    /**
     * @return the cleanSource
     */
    public boolean isCleanSource() {
        return cleanSource;
    }


    /**
     * @param cleanSource the cleanSource to set
     */
    public void setCleanSource(boolean cleanSource) {
        this.cleanSource = cleanSource;
    }


    /**
     * @return the putContext
     */
    public boolean isPutContext() {
        return putContext;
    }


    /**
     * @param putContext the putContext to set
     */
    public void setPutContext(boolean putContext) {
        this.putContext = putContext;
    }


    private String fromKey;

    private String toKey;


    /**
     * @return the fromKey
     */
    public String getFromKey() {
        return fromKey;
    }


    /**
     * @param fromKey the fromKey to set
     */
    public void setFromKey(String fromKey) {
        this.fromKey = fromKey;
    }


    /**
     * @return the toKey
     */
    public String getToKey() {
        return toKey;
    }


    /**
     * @param toKey the toKey to set
     */
    public void setToKey(String toKey) {
        this.toKey = toKey;
    }

    private String condtion;


    /**
     * @return the condtion
     */
    public String getCondtion() {
        return condtion;
    }


    /**
     * @param condtion the condtion to set
     */
    public void setCondtion(String condtion) {
        this.condtion = condtion;
    }


    /* (non-Javadoc)
     * @see AbstractSupportActor#Execute(Message)
     */
    @Override
    public void Execute(Message message) throws Exception {
        if (!message.getContext().containsKey(fromKey)) {
            return;
        }
        Object prepareMsg = message.getContext().get(this.getFromKey());
        Map obj = handleInner(message, prepareMsg);


        if (obj.containsKey(errorKey)) {

            /**
             * True表示正常，false表示异常
             */
            java.lang.Boolean rtn = (java.lang.Boolean) Ognl.getValue(condtion, obj);


            if (!rtn) {
                throw new Exception(obj.get(this.errorKey).toString());
            }

        }
        if (putContext) {
            message.getContext().putAll(obj);
        } else {
            message.getContext().put(toKey, obj);
        }
        if (cleanSource) {
            message.getContext().remove(fromKey);
        }

    }

    public abstract Map handleInner(Message message, Object prepareMsg);
}
