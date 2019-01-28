package cn.ymotel.example.imgcode;

import cn.ymotel.dactor.action.AbstractJsonSupportActor;
import cn.ymotel.dactor.message.ServletMessage;
import cn.ymotel.dactor.message.Message;

public class ImgCodeGeneratorActor extends AbstractJsonSupportActor {
    @Override
    public Object Execute(Message message) throws Exception {
        ValidateCode vCode = new ValidateCode(160,40,5,150);
        ServletMessage lsm=(ServletMessage)message;
//        lsm.getRequest().getSession(false).setAttribute("_ImgCode",vCode.getCode().toUpperCase());
        return  vCode.writeToBytes();


    }

}
