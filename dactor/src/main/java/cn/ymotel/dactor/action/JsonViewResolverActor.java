package cn.ymotel.dactor.action;

import cn.ymotel.dactor.exception.DActorException;
import cn.ymotel.dactor.message.LocalServletMessage;
import cn.ymotel.dactor.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

import cn.ymotel.dactor.async.web.view.StreamView;

public class JsonViewResolverActor implements Actor {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(JsonViewResolverActor.class);
	 
		Map<String,StreamView> viewMap=new HashMap();
		
		
		/**
		 * @return the viewMap
		 */
		public Map<String,StreamView> getViewMap() {
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

		 
		if(message instanceof LocalServletMessage){

			Map dataMap=(Map)message.getContext();
			for(java.util.Iterator iter=dataMap.entrySet().iterator();iter.hasNext();){
				Map.Entry entry=(Map.Entry)iter.next();
				((LocalServletMessage)message).getAsyncContext().getRequest().setAttribute((String)entry.getKey(), entry.getValue());
			}
			if(message.getException()!=null){
			((LocalServletMessage)message).getAsyncContext().getRequest().setAttribute("_EXCEPTION",message.getException());
			}
			

			
		}
		
		StreamView view=this.getViewMap().get("default");

		
		Object jsonObject= message.getContext().get(view.getContent());
		
		
		if(jsonObject==null){
			jsonObject=new HashMap();
			message.getContext().put(view.getContent(), jsonObject);
		}
		
		if(jsonObject instanceof Map){
			
			if(message.getException()==null){
				((Map)jsonObject).put("errcode", "0");
				((Map)jsonObject).put("errmsg", "成功");
				
			}else if(message.getException() instanceof DActorException){
				DActorException ex=(DActorException)message.getException();
				((Map)jsonObject).put("errcode", ex.getErrorCode());//一般错误
				((Map)jsonObject).put("errmsg", ex.getMessage());
				
			}else{
				((Map)jsonObject).put("errcode", "10000");//一般错误
				((Map)jsonObject).put("errmsg", message.getException().getMessage());
			}
			if(message.getException()!=null){
				if (logger.isTraceEnabled()) {
					logger.trace("HandleMessage(Message)"); //$NON-NLS-1$
				}
				
 			}
			
			
		}
		
		
	 
			
			try {
				view.render(message,"");
			} catch (Exception e) {
			if (logger.isTraceEnabled()) {
				logger.trace("HandleMessage(Message) - viewResolver-----" + ((LocalServletMessage) message) + "----" + ((LocalServletMessage) message).getAsyncContext()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (logger.isTraceEnabled()) {
				logger.trace("HandleMessage(Message)"); //$NON-NLS-1$
			}
			}
		return message;
	
	}

}
