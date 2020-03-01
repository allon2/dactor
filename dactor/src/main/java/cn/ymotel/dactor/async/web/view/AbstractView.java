/*
 * @(#)AbstractView.java	1.0 2014年9月19日 上午11:03:25
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
 * Created on 2014年9月19日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractView implements HttpView {
    private String ContentType = null;

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

    /* (non-Javadoc)
     * @see DragonView#render(Message, java.lang.String)
     */
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
        renderInner(lMessage, viewName);

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
    private String suffix;

    public abstract void renderInner(ServletMessage message, String viewName);

}
