/*
 * @(#)HttpClientActor.java	1.0 2014年9月17日 上午1:42:11
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.action.httpclient;

import cn.ymotel.dactor.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;

import cn.ymotel.dactor.action.Actor;

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
public class HttpClientActor implements Actor  {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(HttpClientActor.class);

	private HttpClientHelper httpClientHelper;
	public static String URL="_HttpClient_URL";
	public static String CHARSET="_HTTPCLIENT_CHARSET";
	public static String PARAMS="_HTTPCLIENT_PARAMS";
	
	public static String METHOD="_HTTPCLIENT_METHOD";
	public static String RESPONSE="_HTTPCLIENT_RESPONSE";
	public static String HTTPCLIENT_CONTEXT="_HTTP_CLIENT_CONTEXT";
	public static String CONTENT="_HTTPCLIENT_CONTENT";
	private ActorHttpClientResponse actorHttpClientResponse;
	
	private String url=null;
	private String method=null;
	private String charset=null;
	private String referer=null;
	
	private String content=null;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the referer
	 */
	public String getReferer() {
		return referer;
	}
	/**
	 * @param referer the referer to set
	 */
	public void setReferer(String referer) {
		this.referer = referer;
	}
	private Map paramsMapping=new HashMap();
	private Map paramsDefaultValue=new HashMap();
	
	/**
	 * @return the paramsDefaultValue
	 */
	public Map getParamsDefaultValue() {
		return paramsDefaultValue;
	}
	/**
	 * @param paramsDefaultValue the paramsDefaultValue to set
	 */
	public void setParamsDefaultValue(Map paramsDefaultValue) {
		this.paramsDefaultValue = paramsDefaultValue;
	}
	/**
	 * @return the paramsMapping
	 */
	public Map getParamsMapping() {
		return paramsMapping;
	}
	/**
	 * @param paramsMapping the paramsMapping to set
	 */
	public void setParamsMapping(Map paramsMapping) {
		this.paramsMapping = paramsMapping;
	}
	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}
	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	/**
	 * @return the httpClientHelper
	 */
	public HttpClientHelper getHttpClientHelper() {
		return httpClientHelper;
	}
	/**
	 * @param httpClientHelper the httpClientHelper to set
	 */
	public void setHttpClientHelper(HttpClientHelper httpClientHelper) {
		this.httpClientHelper = httpClientHelper;
	}
	/**
	 * @return the actorHttpClientResponse
	 */
	public ActorHttpClientResponse getActorHttpClientResponse() {
		return actorHttpClientResponse;
	}
	/**
	 * @param actorHttpClientResponse the actorHttpClientResponse to set
	 */
	public void setActorHttpClientResponse(
			ActorHttpClientResponse actorHttpClientResponse) {
		this.actorHttpClientResponse = actorHttpClientResponse;
	}
	/* (non-Javadoc)
	 * @see Actor#HandleMessage(Message)
	 */
	@Override
	public Object HandleMessage(final Message message) throws Exception {
		Map context=message.getContext();
		
//		
		HttpUriRequest request=getHttpBuild(message.getContext()).build();
		
		if(referer!=null){
		request.addHeader("Referer", referer);	
		}
		
	 
		
//		if(charset==null){
//			tmpcharset=(String)context.get(CHARSET);
//		}else{
//			tmpcharset=charset;
//		}
		/**
		 * 通过此处可共用会话，进行类似登录后交易
		 */
		HttpClientContext tmplocalContext=null;
		if(context.containsKey(HTTPCLIENT_CONTEXT)){
			tmplocalContext=(HttpClientContext)context.get(HTTPCLIENT_CONTEXT);
		}else{
			tmplocalContext=HttpClientContext.create();
			 CookieStore cookieStore = new BasicCookieStore();
			 tmplocalContext.setCookieStore(cookieStore);
			 context.put(HTTPCLIENT_CONTEXT, tmplocalContext);
		}
		
        final HttpClientContext localContext = tmplocalContext;
//        CookieStore cookieStore = new BasicCookieStore();
//        localContext.setCookieStore(cookieStore);
		if (logger.isInfoEnabled()) {
			logger.info("HandleMessage(Message) - httpclient----" + request); //$NON-NLS-1$
		}
//		 final HttpGet httpget = new HttpGet(uri);
		
		final String tmpcontent=content;
		final String tmpcharset=charset!=null?charset:(String)context.get(CHARSET);

		
 		httpClientHelper.getHttpclient().execute(request,localContext, new FutureCallback<HttpResponse>() {

             public void completed(final HttpResponse response) {
             	 try {
             		 /**
             		  * 完成后及时清除
             		  */
             		 message.getContext().remove(tmpcontent);
             		 
            		 actorHttpClientResponse.handleResponse(response,localContext,tmpcharset, message);
//					String responseString=HandleResponse((String)message.getContext().get(CHARSET),response);
//					message.getContext().put(RESPONSE, responseString);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if (logger.isErrorEnabled()) {
						logger.error("$FutureCallback<HttpResponse>.completed(HttpResponse)",e); //$NON-NLS-1$
					}
					message.setException(e);
	            	 message.getControlMessage().getMessageDispatcher().sendMessage(message);
				}
//                  System.out.println(httpget.getRequestLine() + "->" + response.getStatusLine());
             }

             public void failed(final Exception ex) {
				if (logger.isErrorEnabled()) {
					logger.error("$FutureCallback<HttpResponse>.failed(Exception)",ex); //$NON-NLS-1$
				}
            	 message.setException(ex);
            	 message.getControlMessage().getMessageDispatcher().sendMessage(message);

//                  System.out.println(httpget.getRequestLine() + "->" + ex);
             }

             public void cancelled() {
            	 Exception exception=new Exception("已取消");
            	message.setException(exception);
            	 message.getControlMessage().getMessageDispatcher().sendMessage(message);

//                  System.out.println(httpget.getRequestLine() + " cancelled");
             }

         });
         return null;
	}
	public Map getMap(Map context){
		if(context.containsKey(PARAMS)){
			return (Map)context.get(PARAMS);
		}
		
		Map map=new HashMap();
		
		 for(java.util.Iterator iter=paramsMapping.entrySet().iterator();iter.hasNext();){
			 Map.Entry entry=(Map.Entry)iter.next();
			 String key=(String)entry.getKey();
			 String value=(String)entry.getValue();
//			 if(entry.getValue()==null||entry.getValue().equals("")){
//				 key=(String)entry.getKey();
//			 }else{
//				 key=(String)entry.getValue();
//			 }
			 
//			 if(paramsDefaultValue.containsKey(key)){
//				 
//			 }
			 
			 if(value==null||value.trim().equals("")){
				 map.put(key, context.get(key));
			 }else{
				 map.put(key, context.get(value));
			 }
//			 String key="";
			 
		 }
		 
		 for(java.util.Iterator iter=paramsDefaultValue.entrySet().iterator();iter.hasNext();){
			 Map.Entry entry=(Map.Entry)iter.next();
//			 String key=(String)entry.getKey();
			 if(map.containsKey(entry.getKey())){
				 continue;
			 }else{
				 map.put(entry.getKey(), entry.getValue());
			 }
		 }
		 
		 
		 return map;
		
//		for(int i=0;i<paramsKeyList.size();i++){
//			map.put(context.get(paramsKeyList.get(i))
//		}
		
		
	}
	
	public  HttpGet getHttpGet(Map context){
		String tmpUrl=null;
		if(url!=null){
			tmpUrl=url;
		}else{
			tmpUrl=(String)context.get(URL);
		}
		
		
		HttpGet httpget = new HttpGet(tmpUrl);
		return httpget;
	}
	public   org.apache.http.client.methods.RequestBuilder  getHttpBuild(Map context) throws UnsupportedEncodingException{
		
		String Method=(String)context.get(METHOD);
		RequestBuilder builder = null;
		if(Method==null){
			Method=method;
			;
		} 
		if(Method==null){
			Method="GET";
		}
		if(Method.equals("GET")){
			builder=RequestBuilder.get();
//			request=getHttpGet(context);
		}else if(Method.equals("POST")){
			builder=RequestBuilder.post();
//			request=getHttpPost(context);
		}
		
		
		if(content!=null){
			Object obcontent=context.get(content);
			
			if(obcontent!=null){
				if(obcontent instanceof String ){
					if (logger.isInfoEnabled()) {
						logger.info("HandleMessage(Message) - content----" + obcontent); //$NON-NLS-1$
					}
					builder.setEntity(new StringEntity((String)obcontent));
				}else if (obcontent instanceof byte[] ){
					if (logger.isInfoEnabled()) {
						logger.info("HandleMessage(Message) - content----" + new String((byte[])obcontent)); //$NON-NLS-1$
					}
					builder.setEntity(new ByteArrayEntity((byte[])obcontent));
				}
			}
		}
		
		 String tmpcharset=charset!=null?charset:(String)context.get(CHARSET);

		builder.setCharset(Charset.forName(tmpcharset));
		
		
		
		
		String tmpUrl=null;
		if(url!=null){
			tmpUrl=url;
		}else{
			tmpUrl=(String)context.get(URL);
		}
		
//		RequestBuilder builder = RequestBuilder.post();
		builder.setUri(tmpUrl);
		Map map=getMap(context);
		if (logger.isInfoEnabled()) {
			logger.info("getHttpBuild(Map, RequestBuilder) - " + map); //$NON-NLS-1$
		}
		for(java.util.Iterator iter=map.entrySet().iterator();iter.hasNext();){
			Map.Entry entry=(Map.Entry)iter.next();
			builder.addParameter((String)entry.getKey(),(String)entry.getValue());
			
		}
		return builder;
	}
//	public  HttpUriRequest getHttpPost(Map context){
//		String tmpUrl=null;
//		if(url!=null){
//			tmpUrl=url;
//		}else{
//			tmpUrl=(String)context.get(URL);
//		}
//		
//		RequestBuilder builder = RequestBuilder.post();
//		builder.setUri(tmpUrl);
//		Map map=getMap(context);
//		for(java.util.Iterator iter=map.entrySet().iterator();iter.hasNext();){
//			Map.Entry entry=(Map.Entry)iter.next();
//			builder.addParameter((String)entry.getKey(),(String)entry.getValue());
//			
//		}
//		return builder.build();
////		
////		
////		
////		 HttpPost httpost = new HttpPost(tmpUrl);
////		 Map params=getMap(context);
////		 HttpEntity entity=null;
////		
////			 
////			 List <NameValuePair> nvps = new ArrayList <NameValuePair>();
////
////		        for (java.util.Iterator iter=params.entrySet().iterator();iter.hasNext();) {
////		        	Map.Entry entry=(Map.Entry)iter.next();
////		        	if(entry.getValue() instanceof String[]){
////		        		
////		        		for(int i=0;i<((String)entry.getValue()).length();i++){
////		       	         nvps.add(new BasicNameValuePair((String)entry.getKey(),((String[])entry.getValue())[i]));
////
////		        			
////		        		}
////		        	}
////		        	
////		        	if(entry.getValue() instanceof String){
////		        		nvps.add(new BasicNameValuePair((String)entry.getKey(),(String)entry.getValue()));
////		        		
////		        	}
////		        }
////			 
////	         try {
////				httpost.setEntity(new UrlEncodedFormEntity(nvps, (String)context.get(CHARSET)));
////			} catch (UnsupportedEncodingException e) {
////				e.printStackTrace();
////			}
////	         return httpost;
//		
//		
//	}
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
