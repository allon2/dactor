/*
 * @(#)BigStreamView.java	1.0 2014年9月13日 上午12:39:18
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.async.web.view;

import cn.ymotel.dactor.message.ServletMessage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletResponse;
import java.io.*;

/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年9月13日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class StreamView extends AbstractView {
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(StreamView.class);


    private String content = "_Content";

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

    /* (non-Javadoc)
     * @see DragonView#render(Message, java.lang.String)
     */
    @Override
    public void renderInner(ServletMessage message, String defaultMessage) {

        Object obj = message.getContext().get(content);

        if (obj == null) {
            obj = defaultMessage;
        }


        WriteString(obj, message.getAsyncContext().getResponse());
        WriteStream(obj, message.getAsyncContext().getResponse());
        WriteBytes(obj, message.getAsyncContext().getResponse());
        WriteFile(obj, message.getAsyncContext().getResponse());
        try {
            message.getAsyncContext().complete();
        } catch (Exception e) {
            if (logger.isTraceEnabled()) {
                logger.trace("renderInner(LocalServletMessage, String)"); //$NON-NLS-1$
            }
        }

    }

    private void WriteFile(Object obj, ServletResponse response) {
        if (obj instanceof File) {

        } else {
            return;
        }
        java.io.FileInputStream fin = null;
        try {
            fin = new FileInputStream((File) obj);
            IOUtils.copyLarge(fin, response.getOutputStream());
//			IOUtils.copyLarge(new FileInputStream((File)obj), response.getOutputStream());
            response.getOutputStream().flush();
        } catch (FileNotFoundException e) {
            if (logger.isTraceEnabled()) {
                logger.trace("WriteFile(Object, ServletResponse)"); //$NON-NLS-1$
            }
        } catch (IOException e) {
            if (logger.isTraceEnabled()) {
                logger.trace("WriteFile(Object, ServletResponse)"); //$NON-NLS-1$
            }
        } finally {
            IOUtils.closeQuietly(fin);
        }

    }

    public void WriteString(Object obj, javax.servlet.ServletResponse response) {
        if (obj instanceof String) {

        } else {
            return;
        }
        try {
            response.getWriter().print(obj);
            response.getWriter().flush();
//			response.getWriter().close();
        } catch (IOException e) {
            if (logger.isTraceEnabled()) {
                logger.trace("WriteString(Object, javax.servlet.ServletResponse)"); //$NON-NLS-1$
            }
        }
    }

    public void WriteStream(Object obj, javax.servlet.ServletResponse response) {
        InputStream input = null;
//		(InputStream)message.getContext().get(content);
        if (obj instanceof InputStream) {

        } else {
            return;
        }
        try {
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                response.getOutputStream().write(buffer, 0, n);
            }


            response.getOutputStream().flush();
            ;
//				message.getAsyncContext().complete();

            //response.getOutputStream().close();;
        } catch (IOException e) {
            if (logger.isTraceEnabled()) {
                logger.trace("WriteStream(Object, javax.servlet.ServletResponse)"); //$NON-NLS-1$
            }
        }
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                if (logger.isTraceEnabled()) {
                    logger.trace("WriteStream(Object, javax.servlet.ServletResponse)"); //$NON-NLS-1$
                }
            }
        }
    }

    public void WriteBytes(Object bs, javax.servlet.ServletResponse response) {
        if (bs instanceof byte[]) {

        } else {
            return;
        }
        try {
            response.getOutputStream().write((byte[]) bs);
            response.getOutputStream().flush();
            ;
//			response.getOutputStream().close();;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            if (logger.isTraceEnabled()) {
                logger.trace("WriteBytes(Object, javax.servlet.ServletResponse)"); //$NON-NLS-1$
            }
        }
    }

}
