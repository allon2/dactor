package cn.ymotel.dactor.action;

import cn.ymotel.dactor.Constants;
import cn.ymotel.dactor.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AbstractJsonSupportActor implements Actor, ApplicationContextAware {

    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(AbstractJsonSupportActor.class);


    @Override
    public Object HandleMessage(Message message) throws Exception {

        try {
            Object obj = Execute(message);
            if (obj != null) {
                message.getContext().put(Constants.CONTENT, obj);
            }
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("错误信息", e); //$NON-NLS-1$
            }

            message.setException(e);
        }
        return message;
    }


    public Object Execute(Message message) throws Exception {
        return null;
    }

    private ApplicationContext appcontext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appcontext = applicationContext;

    }


}
