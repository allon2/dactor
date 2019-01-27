package cn.ymotel.dactor.async.web.view;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;


import java.io.IOException;
import java.lang.reflect.Type;

/**
 * json特殊操作
 * <p>
 *
 * @author 宋汝波
 * @date 2014年11月24日
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
	            if ( object == null ) {
	                if ( out.isEnabled(SerializerFeature.WriteNullNumberAsZero) ) {
	                    out.write('0');
	                } else {
	                    out.writeNull();
	                }
	                return;
	            }
	            /**
	             * 超过Javascript的最大精度，自动转换为String
	             */
	            long value=((Long)object).longValue();
	            if(value>9007199254740992L){
	            	 out.writeString(object.toString());
	            }else{
	            	 out.writeLong(value);
	            }
	            
	          
			
		}
 
    };
 
  
}