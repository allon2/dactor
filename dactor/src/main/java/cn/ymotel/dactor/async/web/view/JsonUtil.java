package cn.ymotel.dactor.async.web.view;

import cn.ymotel.dactor.Constants;
import cn.ymotel.dactor.exception.DActorException;
import cn.ymotel.dactor.message.Message;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * json特殊操作
 * <p>
 *
 * @author 宋汝波
 * @version 1.0.0
 */
public class JsonUtil {
    /**
     * 对序列化的Long类型进行特殊处理,避免位数过大导致和js精度的丢失,只用于向页面发送json数据时使用
     */
    public static ObjectSerializer longSerializer = new ObjectSerializer() {


        @Override
        public void write(JSONSerializer serializer, Object object, Object fileName, Type fieldType, int arg4) throws IOException {
            SerializeWriter out = serializer.getWriter();
            if (object == null) {
                if (out.isEnabled(SerializerFeature.WriteNullNumberAsZero)) {
                    out.write('0');
                } else {
                    out.writeNull();
                }
                return;
            }
            /**
             * 超过Javascript的最大精度，自动转换为String
             */
            long value = ((Long) object).longValue();
            if (value > 9007199254740992L) {
                out.writeString(object.toString());
            } else {
                out.writeLong(value);
            }


        }

    };
    public static Object AppendHead(Message message, Object jsonObject) {
        String jsonhead=(String) message.getControlData().get(Constants.JSONHEAD);
        if(jsonhead==null||jsonhead.equals("TRUE")){//默认加head头


        }else{
            return jsonObject;
        }
        if (jsonObject instanceof Map) {

        }else{
            return jsonObject;
        }

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


        return rtnMap;
    }

    public static Object ConvertFastJsonArray2Object(Object obj){
        if(obj instanceof JSONArray){
            JSONArray b=(JSONArray)obj;
            Object[] array=new Object[b.size()];
            for(int i=0;i<b.size();i++){

                if(b.get(i) instanceof JSONObject||b.get(i) instanceof  JSONArray){
                    array[i]= ConvertFastJsonArray2Object(b.get(i));
                }else{
                    array[i]=b.get(i);

                }
            }
            return array;
        }
        if(obj instanceof JSONObject){
            JSONObject b=(JSONObject)obj;
            Map t=new HashMap();
            for(java.util.Iterator iter=b.entrySet().iterator();iter.hasNext();){
                Map.Entry entry=(Map.Entry)iter.next();
                if(entry.getValue() instanceof JSONObject||entry.getValue() instanceof    JSONArray){
                    t.put(entry.getKey(), ConvertFastJsonArray2Object(entry.getValue()));
                }else{
                    t.put(entry.getKey(),entry.getValue());
                }
            }
            return t;
        }
        return obj;

    }

}