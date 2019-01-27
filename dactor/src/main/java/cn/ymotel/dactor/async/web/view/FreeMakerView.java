/*
 * @(#)FreeMakerView.java	1.0 2014年9月15日 上午1:35:14
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.async.web.view;

import cn.ymotel.dactor.message.LocalServletMessage;
import cn.ymotel.dactor.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.StringWriter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年9月15日
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
public class FreeMakerView implements HttpView {
	private String ContentType;
	 
	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return ContentType;
	}
	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		ContentType = contentType;
	}
	
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(FreeMakerView.class);

	private String suffix;

	/**
	 * @return the suffix
	 */
	public String getSuffix() {
		return suffix;
	}


	private String path;
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * @param suffix the suffix to set
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	private String prefix;
	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}
	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	private Configuration freeMarkerConfig;
	

	/**
	 * @return the freeMarkerConfig
	 */
	public Configuration getFreeMarkerConfig() {
		return freeMarkerConfig;
	}


	/**
	 * @param freeMarkerConfig the freeMarkerConfig to set
	 */
	public void setFreeMarkerConfig(Configuration freeMarkerConfig) {
		this.freeMarkerConfig = freeMarkerConfig;
	}


	/* (non-Javadoc)
	 * @see DragonView#render(Message, java.lang.String)
	 */
	@Override
	public void render(Message message, String viewName) {
		try {
			if(message instanceof LocalServletMessage){}else{
				return ;
			}
			
			LocalServletMessage lMessage=(LocalServletMessage)message;
			if(lMessage.getAsyncContext().getResponse().isCommitted()){
				return ;
			}
			if(this.getContentType()!=null){
	 			lMessage.getAsyncContext().getResponse().setContentType(getContentType());
	 		}
			String tplpath=null;
			if(path!=null){
				tplpath=prefix+path+this.suffix;
			}else{
				tplpath=prefix+viewName+this.suffix;
			}
			Template t=freeMarkerConfig.getTemplate(tplpath);
			StringWriter writer=new StringWriter();
			t.process(message.getContext(), writer);
			String sss=ProcessAfter(message,writer);
			lMessage.getAsyncContext().getResponse().getWriter().print(sss);
			
//			t.process(message.getContext(), lMessage.getAsyncContext().getResponse().getWriter());
			lMessage.getAsyncContext().getResponse().getWriter().flush();
			try {
				lMessage.getAsyncContext().complete();
			} catch (Exception e) {
				if (logger.isTraceEnabled()) {
					logger.trace("renderInner(LocalServletMessage, String)"); //$NON-NLS-1$
				}
			}
//			lMessage.getAsyncContext().getResponse().getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
			if (logger.isTraceEnabled()) {
				logger.trace("render(Message, String)"); //$NON-NLS-1$
			}
		} catch (TemplateException e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			if (logger.isTraceEnabled()) {
				logger.trace("render(Message, String)"); //$NON-NLS-1$
			}
		}catch(java.lang.Throwable e){
			e.printStackTrace();
		}

	}
	public String ProcessAfter(Message message,StringWriter writer){
		return writer.toString();
		
	}

}
