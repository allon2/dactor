/*
 * @(#)AbstractSupportActor.java	1.0 2014年9月16日 上午11:03:21
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.action;

import cn.ymotel.dactor.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 *         <p>
 *         Created on 2014年9月16日 Modification history {add your history}
 *         </p>
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractSupportActor implements Actor {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(AbstractSupportActor.class);

	private org.mybatis.spring.SqlSessionTemplate sqlSession;

	/**
	 * @return the sqlSession
	 */
	public org.mybatis.spring.SqlSessionTemplate getSqlSession() {
		return sqlSession;
	}

	/**
	 * @param sqlSession
	 *            the sqlSession to set
	 */
	public void setSqlSession(org.mybatis.spring.SqlSessionTemplate sqlSession) {
		this.sqlSession = sqlSession;
	}



	/* (non-Javadoc)
	 * @see Actor#HandleMessage(Message)
	 */
	@Override
	public Object HandleMessage(Message message) throws Exception {
		 
			try {
				Execute(message);
			} catch (java.lang.Throwable e) {
			if (logger.isErrorEnabled()) {
				logger.error("错误信息",e);
			}
 				message.setException(e);
			}
		 return message;
	}
	
	public   void Execute(Message message) throws Exception{
		//子类可继承
	};

}
