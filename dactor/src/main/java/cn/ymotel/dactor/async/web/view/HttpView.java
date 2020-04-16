/*
 * @(#)DragonView.java	1.0 2014年9月10日 下午5:12:57
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.async.web.view;

import cn.ymotel.dactor.message.Message;

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
public interface HttpView<T extends  Message> {
    public void render(T message, String viewName);
    /**
     * @return the contentType
     */
    public String getContentType();

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType);
    public default String getSuffix() {
        return null;
    }

    /**
     * @param suffix the suffix to set
     */
    public default void setSuffix(String suffix){};
}
