/*
 * @(#)Actor.java	1.0 2014年4月21日 下午1:01:16
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.action;

import cn.ymotel.dactor.ActorUtils;
import cn.ymotel.dactor.Constants;
import cn.ymotel.dactor.message.Message;

import java.util.Map;

/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年4月21日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public interface Actor<T extends  Message> {
    /**
     *
     * @param message
     * @return 返回NuLL值，会一直等待异步返回，并做处理
     * @throws Exception
     */
    default  public <E> E  HandleMessage(T message) throws java.lang.Throwable{
        try {
            Object obj = Execute(message);
            if (obj != null) {
               String key= ActorUtils.getDataKey(message,Constants.CONTENT);

                message.getContext().put(key, obj);
            }
        } catch (Throwable e) {

            message.setException(e);
        }
        return (E) message;
    };



    default  public <E> E  Execute(T message) throws java.lang.Throwable{
        return null;
    }


}
