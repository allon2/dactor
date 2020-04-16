package cn.ymotel.dactor.async.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
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

        HttpServletResponse response = null;
        try {
            asyncEvent.getThrowable().printStackTrace();
            response = (HttpServletResponse)asyncEvent.getSuppliedResponse();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        asyncEvent.getAsyncContext().complete();
//        ((HttpServletResponse) asyncContext.getResponse()).sendError(errorcode);
//        PrintWriter out = response.getWriter();
//
//        out.write("处理超时");
//        out.flush();
//        asyncEvent.getAsyncContext().complete();

    }

    @Override
    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {

    }

    @Override
    public void onTimeout(AsyncEvent asyncEvent) throws IOException {


        HttpServletResponse response =(HttpServletResponse)asyncEvent.getSuppliedResponse();;
        response.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT);
//        asyncEvent.getAsyncContext().complete();

    }

}
