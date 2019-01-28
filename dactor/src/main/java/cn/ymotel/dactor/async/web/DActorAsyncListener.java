package cn.ymotel.dactor.async.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class DActorAsyncListener implements AsyncListener {
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(DActorAsyncListener.class);

    @Override
    public void onComplete(AsyncEvent asyncEvent) throws IOException {

    }

    @Override
    public void onError(AsyncEvent asyncEvent) throws IOException {
        if (logger.isTraceEnabled()) {
            logger.trace("onError(AsyncEvent) - onError----" + asyncEvent.getSuppliedRequest().getParameterMap()); //$NON-NLS-1$
        }

    }

    @Override
    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {

    }

    @Override
    public void onTimeout(AsyncEvent asyncEvent) throws IOException {

        if (logger.isTraceEnabled()) {
            logger.trace("onTimeout(AsyncEvent) - timeout----" + asyncEvent.getSuppliedRequest().getParameterMap()); //$NON-NLS-1$
        }

        ServletResponse response = asyncEvent.getAsyncContext().getResponse();

        PrintWriter out = response.getWriter();

        out.write("处理超时");


    }

}
