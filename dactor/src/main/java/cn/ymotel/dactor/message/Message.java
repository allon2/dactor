/*
 * @(#)Message.java	1.0 2014年4月21日 下午1:08:28
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.message;

import java.util.HashMap;
import java.util.List;
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
public interface Message {
    /**
     * 在Message准备执行时注入，在Message执行完毕后，清空
     * @return
     */
    public Map getAttributes() ;

    public <T> T  getAttribute(Object key);

    public <T> T getAttribute(Object key,T defaultValue) ;

    /**
     * 得到Message放入队列中的时间,用于判断整个交易是否处理超时,在将Message放入Actor之前判断
     *
     * @return 返回生成消息的时间
     * @version 1.0
     * @since 1.0
     */
    public java.util.Date getStartDate();

    /**
     * 产生Message的原始对象，一般也会负责处理错误等结果,可以扩展WebActor
     *
     * @return 返回原始对象
     * @version 1.0
     * @since 1.0
     */
    public Object getOrigSource();


    public void setOrigSource(Object key);

    /**
     * 保存上下文的内容
     *
     * @return 返回请求信息
     * @version 1.0
     * @since 1.0
     */
    public Map getContext();

    public <T> T getContextData(Object obj);
    public <T> T getContextData(Object obj,T defaultValue);

    /**
     * @return 得到忽略大小写的Map
     */
    public Map getCaseInsensitivegetContext();

    /**
     * 存储逻辑控制信息,程序员不能修改
     *
     * @return 返回存储逻辑控制信息
     * @version 1.0
     * @since 1.0
     */
    public SpringControlMessage getControlMessage();

    public void setControlMessage(SpringControlMessage message);

    public void setException(Throwable exception);

    public Throwable getException();

    public String getTransactionId();


    public Message getParentMessage();

    public void setParentMessage(Message parentMessage);

    public List<Message> getChilds();

    public void setChilds(List<Message> childs);

    public java.util.concurrent.atomic.AtomicInteger getChildCount();

    public void setChildCount(int childCount);

    public Object getUser();
    public void setUser(Object user);

    /**
     * 获得控制数据，将控制信息和处理数据分离
     * @return 控制数据
     */
    public Map getControlData();
    public <T> T getControlData(Object obj);


    /**
     * @return 得到忽略大小写的Map
     */
    public Map getCaseInsensitivegetControlData();
}
