/*
 * @(#)ControlMessage.java	1.0 2014年5月13日 下午5:15:23
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.message;

import java.util.Deque;


import cn.ymotel.dactor.core.MessageDispatcher;

import cn.ymotel.dactor.workflow.ActorProcessStructure;

/**
 * {type specification, must edit}
 * 
 * @author Administrator {must edit, use true name}
 *         <p>
 *         Created on 2014年5月13日 Modification history {add your history}
 *         </p>
 *         <p>
 *
 *         </p>
 * @version 1.0
 * @since 1.0
 */
public class ControlMessage {

	/**
	 * 
	 *优先使用责任链进行初始化，如果无责任链，则使用parent属性
	 *
	 * @param id
	 *
	 * @version 1.0
	 * @since 1.0
	 */
	
	
	public void init(String id) {

	}

	private String state="";
	
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	private MessageDispatcher messageDispatcher;

	public MessageDispatcher getMessageDispatcher() {
		return messageDispatcher;
	}
	public void setMessageDispatcher(MessageDispatcher messageDispatcher) {
		this.messageDispatcher = messageDispatcher;
	}
	public Deque<ActorProcessStructure> getActorsStack(){
		return null;
	}
	public ActorProcessStructure getProcessStructure(){
		return null;
	}

	private ControlMessage parent =null;

	public ControlMessage getParent() {
		return parent;
	}

	public void setParent(ControlMessage parent) {
		this.parent = parent;
	}
}
