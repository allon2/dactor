/*
 * @(#)ActorHttpClientResponseHandler.java	1.0 2014年9月17日 上午11:18:38
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.action.httpclient;

import org.apache.http.HttpResponse;
import org.apache.http.client.protocol.HttpClientContext;

import cn.ymotel.dactor.message.Message;

/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年9月17日
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
public interface ActorHttpClientResponseHandler {
	public void handleResponse(HttpResponse response,HttpClientContext httpclientcontext,String charset,Message message) throws Exception;

}
