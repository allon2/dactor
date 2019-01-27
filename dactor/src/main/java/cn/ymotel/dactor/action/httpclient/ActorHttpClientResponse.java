/*
 * @(#)ActorHttpClientResponse.java	1.0 2014年9月17日 上午11:19:38
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.action.httpclient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

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
 * @version 1.0
 * @since 1.0
 */
public class ActorHttpClientResponse implements ActorHttpClientResponseHandler {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(ActorHttpClientResponse.class);

	/* (non-Javadoc)
	 * @see ActorHttpClientResponseHandler#handleResponse(org.apache.http.HttpResponse, java.lang.String)
	 */
	@Override
	public void handleResponse(HttpResponse response,HttpClientContext httpclientcontext, String charset,Message message) throws Exception {
		String result= handleInner(response, charset);
// 		Header headers[] = response.getAllHeaders();
//        int ii = 0;
//        while (ii < headers.length) {
//            System.out.println(headers[ii].getName() + ": " + headers[ii].getValue()+"----"+headers[ii].getValue());
//            ++ii;
//        }
//
//        
//        List<Cookie> cookies = httpclientcontext.getCookieStore().getCookies();
//        for (int i = 0; i < cookies.size(); i++) {
//            System.out.println("Local cookie: " + cookies.get(i));
//        }
        
        message.getContext().put(HttpClientActor.RESPONSE, result);
		message.getControlMessage().getMessageDispatcher().sendMessage(message);

	}
	/**
	 * {method specification, must edit}
	 *
	 * @param response HttpClient的响应
	 * @param charset 字符集
	 * @throws IOException 抛出IO异常
	 * @throws HttpResponseException 抛出HttpClient的错误响应
	 * @return 返回经过处理的HTML
	 * @version 1.0
	 * @since 1.0
	 */
	public static String handleInner(HttpResponse response, String charset)
			throws IOException, HttpResponseException {
		StatusLine statusLine = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		try {
			if (statusLine.getStatusCode() >= 300) {
				 EntityUtils.consume(entity);
				throw new HttpResponseException(statusLine.getStatusCode(),
						statusLine.getReasonPhrase());
			}

			

			if (entity == null) {
				return null;
			}
			 Charset charset1=null;
			if(charset!=null){
				charset1= Charset.forName(charset);
			}else{
			 ContentType contentType =ContentType.getOrDefault(entity);
			   charset1 = contentType.getCharset();
	            if (charset1 == null||"".equals(charset1.toString())) {
	            	byte[] bytes = EntityUtils.toByteArray(entity);
	            	 charset1= Charset.forName(getContentCharSet(bytes));
	            	 return new String(bytes,charset1);
	            }
	            
	            if(charset1==null||"".equals(charset1.toString())){
	            	charset1=Charset.forName(charset);
	            }
			}
	            
			String ss= EntityUtils.toString(entity, charset1);
			if (logger.isInfoEnabled()) {
				logger.info("handleInner(HttpResponse, String) - http----response ----" + ss); //$NON-NLS-1$
			}
			return ss;
		} catch (ParseException e) {
			if (logger.isErrorEnabled()) {
				logger.error("handleInner(HttpResponse, String)",e); //$NON-NLS-1$
			}
			throw e;
		}finally{
			 EntityUtils.consumeQuietly(entity);
		}
//		return "";
	}
	public static String getContentCharSet(byte[] bytes) throws ParseException {
		String charSet = "";
		//(?i)\\bcharset=\\s*\"?([^\\s;\"]*)
		String regEx = "(?=<meta).*?(?<=charset=[\\'|\\\"]?)([[a-z]|[A-Z]|[0-9]|-]*)";
		Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(new String(bytes)); // 默认编码转成字符串，因为我们的匹配中无中文，所以串中可能的乱码对我们没有影响
		boolean result = m.find();
		if(!result){
			return "gbk";
		}
		if (m.groupCount() == 1) {
			charSet = m.group(1);
		}
		if(charSet.trim().equals("")){
			charSet="gbk";
		}

		return charSet;
	}

}
