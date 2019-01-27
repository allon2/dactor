/*
 * @(#)BeginActor.java	1.0 2014年5月13日 下午1:20:02
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.action;

import cn.ymotel.dactor.message.Message;
import org.apache.commons.logging.LogFactory;

/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年5月13日
 *   Modification history	
 *   {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class BeginActor implements Actor {

	/* (non-Javadoc)
	 * @see com.ymotel.util.actor.Actor#HandleMessage(com.ymotel.util.actor.Message)
	 */
	@Override
	public Object HandleMessage(Message message) throws Exception {
		return message;
//		message.getControlMessage().getMessageDispatcher().sendMessage(message);
		
	}

}
