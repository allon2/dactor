/*
 * @(#)SpringControlMessage.java	1.0 2014年9月21日 下午1:20:27
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.message;

import java.util.Deque;


import cn.ymotel.dactor.core.ActorTransactionCfg;
import cn.ymotel.dactor.core.ActorChainCfg;
import cn.ymotel.dactor.workflow.ActorProcessStructure;
import cn.ymotel.dactor.workflow.WorkFlowProcess;

/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年9月21日
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
public class SpringControlMessage extends ControlMessage {
	private ActorTransactionCfg sourceCfg;
	 
/**
	 * @return the sourceCfg
	 */
	public ActorTransactionCfg getSourceCfg() {
		return sourceCfg;
	}
 

	/**
	 * parent,child
	 */
	private Deque<ActorProcessStructure> actorsStacks=WorkFlowProcess.createActorsStack();
	/**
	 * child,parent
	 */
	private Deque<ActorProcessStructure> downStacks=WorkFlowProcess.createActorsStack();

	public Deque<ActorProcessStructure> getDownStack(){
		return downStacks;
	}
	//查看堆栈顶层对象
	public ActorProcessStructure getProcessStructure(){
		if(actorsStacks.isEmpty()){
			return null;
		}
 		return actorsStacks.peek();
	}
	public Deque<ActorProcessStructure> getActorsStack(){
		return actorsStacks;
	}
	public void init(ActorTransactionCfg actorcfg,ActorChainCfg chain) {
		sourceCfg=actorcfg;
		WorkFlowProcess.PushActorsToStackWithChain(actorsStacks, actorcfg, chain);
	}


}
