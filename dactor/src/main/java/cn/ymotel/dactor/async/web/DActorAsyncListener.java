package cn.ymotel.dactor.async.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class DActorAsyncListener implements AsyncListener {
    private MultipartResolver multipartResolver;
    public DActorAsyncListener() {
    }
    public DActorAsyncListener(MultipartResolver multipartResolver) {
        this.multipartResolver = multipartResolver;
    }

    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(DActorAsyncListener.class);

    @Override
    public void onComplete(AsyncEvent asyncEvent) throws IOException {
        clean((HttpServletRequest) asyncEvent.getAsyncContext().getRequest());
    }
    private void clean(HttpServletRequest processedRequest){
        if(multipartResolver==null){
            return ;
        }
        if (processedRequest instanceof MultipartHttpServletRequest) {

            multipartResolver.cleanupMultipart((MultipartHttpServletRequest) processedRequest);
        }
    }
    @Override
    public void onError(AsyncEvent asyncEvent) throws IOException {
        clean((HttpServletRequest) asyncEvent.getAsyncContext().getRequest());

        HttpServletResponse response = null;
        try {
            asyncEvent.getThrowable().printStackTrace();
            response = (HttpServletResponse)asyncEvent.getSuppliedResponse();
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (java.lang.Throwable e) {
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
        clean((HttpServletRequest) asyncEvent.getAsyncContext().getRequest());

        HttpServletResponse response =(HttpServletResponse)asyncEvent.getSuppliedResponse();
        response.sendError(HttpStatus.REQUEST_TIMEOUT.value(),HttpStatus.REQUEST_TIMEOUT.getReasonPhrase());

//        response.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT);
//        asyncEvent.getAsyncContext().complete();

    }

}
