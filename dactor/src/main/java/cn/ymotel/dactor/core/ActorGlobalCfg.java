/*
 * @(#)ActorGlobalCfg.java	1.0 2014年9月19日 下午11:47:05
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.core;

import java.util.HashMap;
import java.util.Map;

/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年9月19日
 *   Modification history	
 *   {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class ActorGlobalCfg {
	private Map params=new HashMap();

	/**
	 * @return the params
	 */
	public Map getParams() {
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(Map params) {
		this.params = params;
	}
	
}
