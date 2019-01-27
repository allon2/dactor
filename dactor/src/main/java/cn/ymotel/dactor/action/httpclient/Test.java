/*
 * @(#)Test.java	1.0 2014年10月18日 上午10:33:58
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.action.httpclient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.concurrent.FutureCallback;

/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年10月18日
 *   Modification history	
 *   {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class Test {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(Test.class);

	/**
	 * {method specification, must edit}
	 *
	 * @param args 参数
	 * @throws Exception 抛出异常
	 * @version 1.0
	 * @since 1.0
	 */
	public static void main(String[] args) throws Exception{
		HttpClientHelper helper=new HttpClientHelper();
		helper.setMaxTotal(30000);
		helper.afterPropertiesSet();
		for(int i=0;i<1;i++){
	
		
			RequestBuilder builder=RequestBuilder.post();
//			builder.setUri("https://pbank.psbc.com/pweb/CheckUserUKServlet.do");
			builder.setUri("https://103.22.252.16/pweb/");

			//			builder.setUri("https://pbank.psbc.com/pweb/CheckUserUKServlet.do");

			//			builder.setUri("http://115.29.191.162:9000/weixin/wxutil.empty.do");
			builder.addParameter("a", i+"");
		helper.getHttpclient().execute(builder.build(), new FutureCallback<HttpResponse>() {

             public void completed(final HttpResponse response) {
              
//                  System.out.println(httpget.getRequestLine() + "->" + response.getStatusLine());
             }

             public void failed(final Exception ex) {
            	 
             }

             public void cancelled() {
             
             }

         });
		Thread.currentThread().sleep(10);
			if (logger.isTraceEnabled()) {
				logger.trace("main(String[]) - count" + i); //$NON-NLS-1$
			}
//		helper.shutdown();
		}
	}

}
