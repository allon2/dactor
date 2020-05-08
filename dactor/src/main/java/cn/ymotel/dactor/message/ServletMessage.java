/*
 * @(#)LocalServletMessage.java	1.0 2014年9月9日 下午8:37:10
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.message;

import cn.ymotel.dactor.ActorUtils;
import cn.ymotel.dactor.Constants;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;


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
public class ServletMessage extends DefaultMessage {
    private javax.servlet.AsyncContext AsyncContext;
    //HttpServletRequest request,
    //HttpServletResponse response
    private HttpServletRequest request;
    private HttpServletResponse response;

    /**
     * @return the request
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * @param request the request to set
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * @return the response
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    /**
     * @return the asyncContext
     */
    public javax.servlet.AsyncContext getAsyncContext() {
        return AsyncContext;
    }


    /**
     * @param asyncContext the asyncContext to set
     */
    public void setAsyncContext(javax.servlet.AsyncContext asyncContext) {
        AsyncContext = asyncContext;
    }

    @Override
    public Object getUser() {
         return request.getSession(true).getAttribute(Constants.USER);
    }

    @Override
    public void setUser(Object user) {
        request.getSession(true).setAttribute(Constants.USER, user);
    }

    public byte[] getFileBytes(String fileName) throws IOException {
        MultipartFile file= ((MultipartHttpServletRequest)AsyncContext.getRequest()).getFile(fileName);
        return file.getBytes();
    }
    public List getFileNames(){
        List ls=new ArrayList();
        Iterator<String> iterator= ((MultipartHttpServletRequest)AsyncContext.getRequest()).getFileNames();
        for(;iterator.hasNext();){
           ls.add(iterator.next());
        }
        return ls;
    }
    public List<MultipartFile> getFiles(String name){
        return ((MultipartHttpServletRequest)AsyncContext.getRequest()).getFiles(name);
    }
    public MultipartFile getFile(String name){
        return ((MultipartHttpServletRequest)AsyncContext.getRequest()).getFile(name);
    }


    public InputStream getFileStream(String fileName) throws IOException {
        MultipartFile file= ((MultipartHttpServletRequest)AsyncContext.getRequest()).getFile(fileName);
        return file.getInputStream();
    }
    public String getClientIp(){
       return  ActorUtils.getClientIP((HttpServletRequest)AsyncContext.getRequest());
    }
    public String getHeaderIgnoreCase(String nameIgnoreCase){
        HttpServletRequest request= (HttpServletRequest)AsyncContext.getRequest();
        final Enumeration<String> names = request.getHeaderNames();
        String name;
        while (names.hasMoreElements()) {
            name = names.nextElement();
            if (name != null && name.equalsIgnoreCase(nameIgnoreCase)) {
                return request.getHeader(name);
            }
        }

        return null;
    }
    public String getHeader(String name){
        HttpServletRequest request= (HttpServletRequest)AsyncContext.getRequest();
         return request.getHeader(name);
    }
}
