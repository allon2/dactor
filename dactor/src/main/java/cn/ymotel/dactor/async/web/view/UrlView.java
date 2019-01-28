/*
 * @(#)JspDispatcherActor.java	1.0 2014年9月9日 下午10:40:29
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.async.web.view;

import cn.ymotel.dactor.message.ServletMessage;
import cn.ymotel.dactor.message.Message;

/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年9月9日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class UrlView implements HttpView {
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

    /**
     * @return the suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * @param suffix the suffix to set
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    private String prefix;
    private String suffix;

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

    @Override
    public void render(Message message, String viewName) {


        if (message instanceof ServletMessage) {
        } else {
            return;
        }


        ServletMessage lMessage = (ServletMessage) message;
        if (lMessage.getAsyncContext().getResponse().isCommitted()) {
            return;
        }
        if (this.getContentType() != null) {
            lMessage.getAsyncContext().getResponse().setContentType(getContentType());
        }

        if (path == null) {

// 			String Result=WorkFlowData.getResults(message.getControlMessage().getSourceId(),"success"+message.getControlMessage().getState());


            ((ServletMessage) message).getAsyncContext().dispatch(prefix + viewName + suffix);

        } else {
            ((ServletMessage) message).getAsyncContext().dispatch(prefix + path + suffix);
        }
//  			((LocalServletMessage)message).getAsyncContext().;
//  			try {
//				((LocalServletMessage)message).getAsyncContext().complete();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
        return;


    }

}
