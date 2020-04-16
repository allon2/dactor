/*
 * @(#)ActorProcessStructure.java	1.0 2014年9月22日 下午11:54:26
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.workflow;

import cn.ymotel.dactor.core.ActorTransactionCfg;

import java.util.Map;

/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年9月22日
 * Modification history
 * {add your history}
 * </p>
 * <p>
 * 记录Actor当前执行状态
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class ActorProcessStructure {

    private ActorProcessStructure child;

    public ActorProcessStructure getChild() {
        return child;
    }

    public void setChild(ActorProcessStructure child) {
        this.child = child;
    }

    private String FromBeanId;
    /**
     * 此Bean是否需要被执行
     */
    private boolean isNeedExecute = true;

    /**
     * @return the isNeedExecute
     */
    public boolean isNeedExecute() {
        return isNeedExecute;
    }

    /**
     * @param isNeedExecute the isNeedExecute to set
     */
    public void setNeedExecute(boolean isNeedExecute) {
        this.isNeedExecute = isNeedExecute;
    }

    private ActorTransactionCfg actorTransactionCfg;

    /**
     * @return the fromBeanId
     */
    public String getFromBeanId() {
        return FromBeanId;
    }

    /**
     * @param fromBeanId the fromBeanId to set
     */
    public void setFromBeanId(String fromBeanId) {
        FromBeanId = fromBeanId;
    }

    /**
     * @return the actorTransactionCfg
     */
    public ActorTransactionCfg getActorTransactionCfg() {
        return actorTransactionCfg;
    }

    /**
     * @param actorTransactionCfg the actorTransactionCfg to set
     */
    public void setActorTransactionCfg(ActorTransactionCfg actorTransactionCfg) {
        this.actorTransactionCfg = actorTransactionCfg;
    }

    private Map stepMap;

    public Map getStepMap() {
        return stepMap;
    }

    public void setStepMap(Map stepMap) {
        this.stepMap = stepMap;
    }

    public boolean isBeginExecute() {
        return isBeginExecute;
    }

    public void setBeginExecute(boolean beginExecute) {
        isBeginExecute = beginExecute;
    }

    public boolean isEndExecute() {
        return isEndExecute;
    }

    public void setEndExecute(boolean endExecute) {
        isEndExecute = endExecute;
    }

    private boolean isBeginExecute = false;
    private boolean isEndExecute = false;

    private String stepBeanId;

    public String getStepBeanId() {
        return stepBeanId;
    }

    public void setStepBeanId(String stepBeanId) {
        this.stepBeanId = stepBeanId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ActorProcessStructure [FromBeanId=" + FromBeanId
                + ", isNeedExecute=" + isNeedExecute + ", actorTransactionCfg="
                + actorTransactionCfg + "]";
    }


}
