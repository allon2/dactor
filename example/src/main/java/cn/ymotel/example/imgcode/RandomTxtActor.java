package cn.ymotel.example.imgcode;

import cn.ymotel.dactor.action.AbstractJsonSupportActor;
import cn.ymotel.dactor.message.Message;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

public class RandomTxtActor extends AbstractJsonSupportActor {
    private final static org.apache.commons.logging.Log logger = LogFactory.getLog(RandomTxtActor.class);


    @Override
    public Object Execute(Message message) throws Exception {

        Map map=new HashMap();
        map.put("111","222");
        return map;
    }
    public static void main(String[] args) {

            logger.debug("debug");
            logger.info("info");

    }
}
