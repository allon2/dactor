package cn.ymotel.dactor.action;

import cn.ymotel.dactor.async.web.view.JsonUtil;
import cn.ymotel.dactor.async.web.view.JsonView;
import cn.ymotel.dactor.async.web.view.StreamView;
import cn.ymotel.dactor.exception.DActorException;
import cn.ymotel.dactor.message.ServletMessage;
import cn.ymotel.dactor.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
@Deprecated
public class JsonViewResolverActor implements Actor {
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(JsonViewResolverActor.class);

    Map<String, StreamView> viewMap = new HashMap();


    /**
     * @return the viewMap
     */
    public Map<String, StreamView> getViewMap() {
        return viewMap;
    }

    /**
     * @param viewMap the viewMap to set
     */
    public void setViewMap(Map viewMap) {
        this.viewMap = viewMap;
    }


    @Override
    public Object HandleMessage(Message message) throws Exception {


        if (message instanceof ServletMessage) {

            Map dataMap = (Map) message.getContext();
            for (java.util.Iterator iter = dataMap.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();
                ((ServletMessage) message).getAsyncContext().getRequest().setAttribute((String) entry.getKey(), entry.getValue());
            }
            if (message.getException() != null) {
                ((ServletMessage) message).getAsyncContext().getRequest().setAttribute("_EXCEPTION", message.getException());
            }


        }

        StreamView view = this.getViewMap().get("default");


        Object jsonObject = message.getContext().get(view.getContent());


        if (jsonObject == null) {
            jsonObject = new HashMap();
            message.getContext().put(view.getContent(), jsonObject);
        }
         JsonUtil.AppendHead(message,jsonObject);



        try {
            view.render(message, "");
        } catch (Exception e) {
            if (logger.isTraceEnabled()) {
                logger.trace("HandleMessage(Message) - viewResolver-----" + ((ServletMessage) message) + "----" + ((ServletMessage) message).getAsyncContext()); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (logger.isTraceEnabled()) {
                logger.trace("HandleMessage(Message)"); //$NON-NLS-1$
            }
        }
        return message;

    }

}
