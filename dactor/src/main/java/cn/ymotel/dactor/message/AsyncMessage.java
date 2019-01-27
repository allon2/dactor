/*
 * @(#)AsyncMessage.java	1.0 2014年9月29日 下午11:31:24
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.message;

import java.util.Map;

/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年9月29日
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
public class AsyncMessage extends DefaultMessage {
//	private Map context=new HashMap();
	/**
	 * 
	 */
	public AsyncMessage(Map context) {
//		this.context.putAll(context);
		this.getContext().putAll(context);
	}

}
