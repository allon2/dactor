/*
 * @(#)ActorChainCfg.java	1.0 2014年9月19日 下午11:36:42
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.core;

import java.util.ArrayList;
import java.util.List;

/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年9月19日
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
public class ActorChainCfg {
	private String id;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	private List<ActorTransactionCfg> before=new ArrayList();

	 private List<ActorTransactionCfg> after=new ArrayList();

	public List<ActorTransactionCfg> getBefore() {
		return before;
	}

	public void setBefore(List<ActorTransactionCfg> before) {
		this.before = before;
	}

	public List<ActorTransactionCfg> getAfter() {
		return after;
	}

	public void setAfter(List<ActorTransactionCfg> after) {
		this.after = after;
	}
}
