package cn.ymotel.dactor.async.web.view;

import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.message.ServletMessage;

import java.io.IOException;

public class RedirectView implements HttpView{
    @Override
    public void successRender(Message message, String viewName) {
       ServletMessage servletMessage=(ServletMessage) message;
        try {
            servletMessage.getResponse().sendRedirect(viewName);
            ((ServletMessage) message).getAsyncContext().complete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
