/*
 * @(#)PdfView.java	1.0 2014年9月19日 上午10:42:26
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.async.web.view;

import cn.ymotel.dactor.Constants;
import cn.ymotel.dactor.exception.DActorException;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.message.ServletMessage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年9月19日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class JsonView extends StreamView {
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(JsonView.class);


    private String content =Constants.CONTENT;

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    private static SerializeConfig mapping = new SerializeConfig();

    //	 private static SerializerFeature[] features = {SerializerFeature.DisableCheckSpecialChar};
    static {
        /**
         *  对序列化的Long类型进行特殊处理,避免位数过大导致和js精度的丢失,只用于向页面发送json数据时使用
         */
        mapping.put(Long.class, JsonUtil.longSerializer);
//         mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd")); 
//         mapping.put(java.sql.Timestamp.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss")); 

    }

    /* (non-Javadoc)
     * @see AbstractView#render(LocalServletMessage, java.lang.String)
     */
    @Override
    public void renderInner(ServletMessage message, String viewName) {

        Object jsonObject = message.getContext().get(content);
        if(jsonObject==null){
            try {
                message.getAsyncContext().getResponse().getWriter().print("");
                message.getAsyncContext().getResponse().getWriter().flush();
                message.getAsyncContext().complete();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        jsonObject=JsonUtil.AppendHead(message,jsonObject);



        String jsonString = null;
        if (jsonObject instanceof String) {
            jsonString = (String) jsonObject;
        } else if (jsonObject instanceof byte[]) {
            try {
                jsonString = new String((byte[]) jsonObject, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                if (logger.isInfoEnabled()) {
                    logger.info("", e);
                }
            }
        } else {
            jsonString = JSON.toJSONString(jsonObject, mapping);
        }



        try {

//			message.getResponse().setHeader("Access-Control-Allow-Origin","*");
//			message.getResponse().setHeader("Access-Control-Allow-Methods","POST");
//			message.getResponse().setHeader("Access-Control-Allow-Headers","x-requested-with,content-type");
            message.getAsyncContext().getResponse().getWriter().print(jsonString);
            message.getAsyncContext().getResponse().getWriter().flush();
            message.getAsyncContext().complete();
//			message.getAsyncContext().getResponse().getWriter().close();
        } catch (IOException e) {
            if (logger.isTraceEnabled()) {
                logger.trace("renderInner(LocalServletMessage, String)"); //$NON-NLS-1$
            }
        }

    }

}
