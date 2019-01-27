/*
 * @(#)JsonParserActor.java	1.0 2014年9月23日 下午3:15:51
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.transformer;

import cn.ymotel.dactor.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年9月23日
 *   Modification history	
 *   {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class JsonParserActor extends AbstractParserActor {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(JsonParserActor.class);
	

	

	/* (non-Javadoc)
	 * @see AbstractParserActor#handleInner(Message)
	 */
	@Override
	public Map handleInner(Message message, Object prepareMsg) {
		Map obj=(Map)JSON.parse((String)prepareMsg);
		if(obj.get("base_resp")!=null){
			obj.putAll((Map)obj.get("base_resp"));
		}
		
		return obj;
	}
	public static void main(String[] args){
		String ss="{\"base_resp\":{\"ret\":0,\"err_msg\":\"ok\"},\"redirect_url\":\"/cgi-bin/home?t=home/index&lang=zh_CN&token=1841168551\"}";
		Map map=(Map)JSON.parse(ss);
		if (logger.isTraceEnabled()) {
			logger.trace("main(String[]) - " + ((Map) map.get("base_resp")).get("err_msg")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

	}
	
}
