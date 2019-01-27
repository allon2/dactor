package cn.ymotel.example.imgcode;

import cn.ymotel.dactor.action.AbstractJsonSupportActor;
import cn.ymotel.dactor.message.Message;

import java.util.HashMap;
import java.util.Map;

public class exceptionTestActor extends AbstractJsonSupportActor {
    @Override
    public Object Execute(Message message) throws Exception {

        throw new Exception("错误测试");
    }
}
