/*
 * @(#)LocalServletMessage.java	1.0 2014年9月9日 下午8:37:10
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.message;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年9月9日
 *   Modification history	
 *   {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class LocalServletMessage extends DefaultMessage {
	private javax.servlet.AsyncContext AsyncContext;
	//HttpServletRequest request,
	//HttpServletResponse response
	private HttpServletRequest request;
	private HttpServletResponse response;

	/**
	 * @return the request
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * @return the response
	 */
	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * @return the asyncContext
	 */
	public javax.servlet.AsyncContext getAsyncContext() {
		return AsyncContext;
	}



	/**
	 * @param asyncContext the asyncContext to set
	 */
	public void setAsyncContext(javax.servlet.AsyncContext asyncContext) {
		AsyncContext = asyncContext;
	}
}
