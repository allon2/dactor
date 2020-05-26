package cn.ymotel.dactor.async.web;

import javax.servlet.*;

public class AsyncContextWrapper implements AsyncContext {
    private AsyncContext asyncContext;
    private boolean complete=false;
    public AsyncContextWrapper(AsyncContext asyncContext) {
        this.asyncContext = asyncContext;
    }

    @Override
    public ServletRequest getRequest() {
        return asyncContext.getRequest();
    }

    @Override
    public ServletResponse getResponse() {
        return asyncContext.getResponse();
    }

    @Override
    public boolean hasOriginalRequestAndResponse() {
        return asyncContext.hasOriginalRequestAndResponse();
    }

    @Override
    public void dispatch() {
        asyncContext.dispatch();
    }

    @Override
    public void dispatch(String path) {
        asyncContext.dispatch(path);
    }

    @Override
    public void dispatch(ServletContext context, String path) {
        asyncContext.dispatch(context,path);
    }

    @Override
    public void complete() {
        if(!complete) {
            asyncContext.complete();
            complete=true;
        }
    }
    public boolean isComplete(){
        return complete;
    }

    @Override
    public void start(Runnable run) {
        asyncContext.start(run);
    }

    @Override
    public void addListener(AsyncListener listener) {
        asyncContext.addListener(listener);
    }

    @Override
    public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse) {
        asyncContext.addListener(listener,servletRequest,servletResponse);
    }

    @Override
    public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
        return asyncContext.createListener(clazz);
    }

    @Override
    public void setTimeout(long timeout) {
        asyncContext.setTimeout(timeout);
    }

    @Override
    public long getTimeout() {
        return asyncContext.getTimeout();
    }
}
