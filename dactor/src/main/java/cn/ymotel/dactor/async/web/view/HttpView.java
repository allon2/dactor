/*
 * @(#)DragonView.java	1.0 2014年9月10日 下午5:12:57
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.async.web.view;

import cn.ymotel.dactor.message.Message;

import java.util.List;

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
    /**
     * 以后使用 successRender 和 exceptionRender方法
     * @param message
     * @param viewName
     */
    @Deprecated
    public default  void render(T message, String viewName){};
    public default void successRender(T message,String viewName){
        render(message,viewName);
    }
    public default  void exceptionRender(T message,String viewName){
        render(message,viewName);
    }
    /**
     * @return the contentType
     */
    public default String getContentType(){return null;};

    /**
     * @param contentType the contentType to set
     */
    public default void setContentType(String contentType){};
    public default String getSuffix() {
        return null;
    }

    /**
     * @param suffix the suffix to set
     */
    public default void setSuffix(String suffix){};


//    public default  void setUrlSuffix(String urlSuffix){}
    /**
     *
     * @return    URL后缀,例如 a.do ,b.json,通过定制此，可覆盖默认选项
     */
    public default  String getUrlSuffix(){return null;}
//    public default  void setViewName(String viewName){}
    public default  String getViewName(){return null;}

    public default List<String> getUrlPatterns(){return null;};
    public default String getUrlPattern(){return null;};
    public default String getExcludeUrlPattern(){return null;}
    public default List<String> getExcludeUrlPatterns(){return null;};

}
