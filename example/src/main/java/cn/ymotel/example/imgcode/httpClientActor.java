package cn.ymotel.example.imgcode;

import cn.ymotel.dactor.action.AbstractJsonSupportActor;
import cn.ymotel.dactor.action.httpclient.HttpClientActor;
import cn.ymotel.dactor.message.Message;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

public class httpClientActor extends AbstractJsonSupportActor {
    private final static org.apache.commons.logging.Log logger = LogFactory.getLog(httpClientActor.class);


    @Override
    public Object Execute(Message message) throws Exception {
       return  message.getContext().get(HttpClientActor.RESPONSE);
    }
    public static void main(String[] args) {

            logger.debug("debug");
            logger.info("info");

    }
}
