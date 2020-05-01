/*
 * @(#)DefaultResolveMessage.java	1.0 2014年9月10日 下午11:46:33
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.message;

import cn.ymotel.dactor.Constants;
import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年9月10日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class DefaultResolveMessage {
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(DefaultResolveMessage.class);

    public Message resolveContext(AsyncContext context, HttpServletRequest request, HttpServletResponse response) {
        ServletMessage message = new ServletMessage();
//		  message
        message.setAsyncContext(context);
        message.setRequest(request);
        message.setResponse(response);
        init(message, request, response);
        return message;
    }

    public Message resolveContext(Message message) {
        return new DefaultMessage();
    }

    private void init(Message message, HttpServletRequest request, HttpServletResponse response) {
        Map parameterMap = new HashMap();
        for (java.util.Enumeration enum1 = request.getParameterNames(); enum1.hasMoreElements(); ) {
            String obj = (String) enum1.nextElement();

            String[] values = request.getParameterValues(obj);
            if (values == null) {
                parameterMap.put(obj, null);

            } else if (values.length == 1) {
                parameterMap.put(obj, values[0]);
            } else {
                parameterMap.put(obj, values);
            }

        }
       Object obj= request.getAttribute(Constants.EXPARAMETER);
        if(obj==null){

        }else
        if(obj instanceof  Map){
            parameterMap.putAll((Map)obj);
        }


        /**
         * json
         */
        if (request.getContentType() != null && request.getContentType().toLowerCase().startsWith("application/json")) {

            Map rtnMap = null;
            List array=null;
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
                StringBuffer sb = new StringBuffer("");
                String temp;
                while ((temp = br.readLine()) != null) {
                    sb.append(temp);
                }
                br.close();
                Object jsonobj=JSON.parse(sb.toString());
                if(jsonobj instanceof Map) {
                    rtnMap = (Map)jsonobj;
                }else if(jsonobj instanceof  List){
                    array=(List)jsonobj;
                }
            } catch (UnsupportedEncodingException e) {
                if (logger.isTraceEnabled()) {
                    logger.trace("init(Message, HttpServletRequest, HttpServletResponse)"); //$NON-NLS-1$
                }
            } catch (IOException e) {
                if (logger.isTraceEnabled()) {
                    logger.trace("init(Message, HttpServletRequest, HttpServletResponse)"); //$NON-NLS-1$
                }
            }
            if(rtnMap!=null) {
                parameterMap.putAll(rtnMap);
            }
            if(array!=null){
                message.setContextList(array);
            }
        }


        ((DefaultMessage) message).setContext(parameterMap);

        message.setOrigSource(parameterMap);
    }
}
