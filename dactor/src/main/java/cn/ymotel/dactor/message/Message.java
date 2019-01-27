/*
 * @(#)Message.java	1.0 2014年4月21日 下午1:08:28
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.message;

import java.util.List;
import java.util.Map;


/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年4月21日
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
public interface Message {
//	/**
//	 * 
//	 * 一个消息对应一个处理的Id号
//	 *
//	 * @return
//	 *
//	 * @version 1.0
//	 * @since 1.0
//	 */
//	public String getActorId();
//	
//	
//	public String setActorId(String actorId);
	/**
	 * 
	 * 得到Message放入队列中的时间,用于判断整个交易是否处理超时,在将Message放入Actor之前判断
	 *
	 * @return
	 *
	 * @version 1.0
	 * @since 1.0
	 */
	public java.util.Date getStartDate();
	
	/**
	 * 
	 *  产生Message的原始对象，一般也会负责处理错误等结果,可以扩展WebActor
	 *
	 * @return
	 *
	 * @version 1.0
	 * @since 1.0
	 */
	public Object getOrigSource();
	
	
	public void setOrigSource(Object obj);
	/**
	 * 
	 * 保存上下文的内容
	 *
	 * @return
	 *
	 * @version 1.0
	 * @since 1.0
	 */
	public Map getContext();
	
	/**
	 * 得到忽略大小写的Map
	 * @return
	 */
	public Map getCaseInsensitivegetContext();
	
	/**
	 * 存储逻辑控制信息,程序员不能修改
	 * 
	 *
	 * @return
	 *
	 * @version 1.0
	 * @since 1.0
	 */
	public SpringControlMessage getControlMessage();
	
	public void setControlMessage(SpringControlMessage message);
	
	public void setException(Throwable exception);
	public Throwable getException();
	public String getTransactionId();

	
	public Message getParentMessage();
	public void setParentMessage(Message parentMessage) ;
	public List<Message> getChilds() ;
	public void setChilds(List<Message> childs);
	public java.util.concurrent.atomic.AtomicInteger getChildCount();
	public void setChildCount(int childCount) ;
	
	
}
