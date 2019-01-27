/*
 * @(#)Actor.java	1.0 2014年4月21日 下午1:01:16
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.action;

import cn.ymotel.dactor.message.Message;

/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年4月21日
 *   Modification history	
 *   {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public interface Actor {
	public Object  HandleMessage(Message message) throws Exception;
	public static String ORACLE="oracle";
	public static String MYSQL="mysql";
	


	
}
