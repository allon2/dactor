package cn.ymotel.dactor.action;

import cn.ymotel.dactor.async.web.view.StreamView;
import cn.ymotel.dactor.exception.DActorException;
import cn.ymotel.dactor.message.ServletMessage;
import cn.ymotel.dactor.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

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

        if (jsonObject instanceof Map) {
            Map rtnMap=(Map)jsonObject;
            /**
             * 兼容上下文中有直接
             */
            if(rtnMap.containsKey("errcode")){
//                if(rtnMap.get("errcode").equals("0")){
//                    //成功
//                }
//                //有错误信息

            }else
            if (message.getException() == null) {
                rtnMap.put("errcode", "0");
                rtnMap.put("errmsg", "成功");

            } else if (message.getException() instanceof DActorException) {
                DActorException ex = (DActorException) message.getException();
                rtnMap.put("errcode", ex.getErrorCode());//一般错误
                rtnMap.put("errmsg", ex.getMessage());

            } else {
                rtnMap.put("errcode", "10000");//一般错误
                rtnMap.put("errmsg", message.getException().getMessage());
            }
            if (message.getException() != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("HandleMessage(Message)"); //$NON-NLS-1$
                }

            }


        }


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
