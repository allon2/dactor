/*
 * @(#)WorkFlowProcess.java	1.0 2014年9月21日 下午1:12:57
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.workflow;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ymotel.dactor.core.ActorTransactionCfg;
import cn.ymotel.dactor.message.AsyncMessage;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.message.SpringControlMessage;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import ognl.Ognl;
import ognl.OgnlContext;

import cn.ymotel.dactor.core.ActorChainCfg;

/**
 * {type specification, must edit}
 *
 * @author  Administrator {must edit, use true name}
 * <p>
 *   Created on 2014年9月21日
 *   Modification history	
 *   {add your history}
 * </p>
 * <p>
 *
 *
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class WorkFlowProcess {
	private final static org.apache.commons.logging.Log logger = LogFactory.getLog(WorkFlowProcess.class);

	public static void PushActorsToStackWithChain(Deque<ActorProcessStructure>  actorStack, ActorTransactionCfg cfg, ActorChainCfg chain){
		

		AppendCfg2Deque(cfg,actorStack);

		
	}
	public static void PushActorsToStack(Deque<ActorProcessStructure>  actorStack,ActorTransactionCfg cfg){

		AppendCfg2Deque(cfg,actorStack);
		

		
	}

	
	
	public static Deque <ActorProcessStructure>  createActorsStack(){
		/**
		 * ArrayDeque 比LinkedList更直接操作数组，所以效率高，因为无线程安全风险，所以使用ArrayDeque 不使用stack 
		 */
		return new ArrayDeque<ActorProcessStructure>()  ;
	}
	/**
	 * HandleMessage 后调用
	 * {method specification, must edit}
	 *
	 * @param control
	 * @param message
	 * @param appcontext
	 *
	 * @version 1.0
	 * @since 1.0
	 */
	public static void processGetToBeanId(SpringControlMessage control, Message message, ApplicationContext appcontext){

		Deque<ActorProcessStructure>  actorStack=control.getActorsStack();
		Deque<ActorProcessStructure>  downStack=control.getDownStack();
		//全部处理完毕
		if(actorStack.isEmpty()&&downStack.isEmpty()){
			return ;
		}


		FireNextMessage(actorStack,downStack,message,appcontext);

	}

	/**
	 * 优先执行Chain，再执行parent,再执行Actor
	 * @param cfg
	 * @param deque
	 */
	public static  void AppendCfg2Deque(ActorTransactionCfg cfg,Deque<ActorProcessStructure> deque){
		if(cfg.getChain()!=null){
			for(int i=cfg.getChain().getAfter().size();i>0;i--){
				deque.push(InitActorProcessStructure(cfg.getChain().getAfter().get(i-1)));
			}
		}
		deque.push(InitActorProcessStructure(cfg));
		if(cfg.getParent()!=null){
			AppendCfg2Deque(cfg.getParent(),deque);
		}
		if(cfg.getChain()!=null){
			for(int i=cfg.getChain().getBefore().size();i>0;i--){
				deque.push(InitActorProcessStructure(cfg.getChain().getBefore().get(i-1)));
			}
		}
	}

	private static ActorProcessStructure InitActorProcessStructure(ActorTransactionCfg cfg){
		ActorProcessStructure tmpstrunc=new ActorProcessStructure();
		tmpstrunc.setActorTransactionCfg(cfg);
		tmpstrunc.setFromBeanId(cfg.getBeginBeanId());
		return tmpstrunc;
	}

	/**
	 * 模式，调用子交易
	 * @param deque
	 * @param downdeque
	 * @param message
	 * @param appcontext
	 */
	public  static void  FireNextMessage(Deque<ActorProcessStructure> deque,Deque<ActorProcessStructure> downdeque,Message message, ApplicationContext appcontext){
//		if(deque.isEmpty()){
//			if(!downdeque.isEmpty()){
//				deque.push(downdeque.pop());
//			}
//		}

		ActorProcessStructure strunc=deque.peek();
		if(strunc==null){
			return  ;
		}
 			logger.info("beanId--"+strunc.getFromBeanId()+"--Id--"+strunc.getActorTransactionCfg().getId());


 		if(!strunc.isBeginExecute()){
			strunc.setFromBeanId( strunc.getActorTransactionCfg().getBeginBeanId());
			return ;
		}
		if(strunc.isEndExecute()){
			strunc.setFromBeanId(null);
			if(downdeque.isEmpty()&&deque.isEmpty()){ //全部执行完毕
				return ;
			}
			if(!downdeque.isEmpty()){
				deque.pop();
				deque.push(downdeque.pop());
//				FireNextMessage(deque,downdeque,message,appcontext);
				return ;
			}

			deque.pop();
			if(deque.isEmpty()){
				if(!downdeque.isEmpty()){
					deque.push(downdeque.pop());
				}
				return ;
			}
//			FireNextMessage(deque,downdeque,message,appcontext);
			return ;
		}

		Throwable throwable=message.getException();
		if(throwable!=null){
			if(!strunc.getActorTransactionCfg().isHandleException()){
				//处理异常;
				strunc.setFromBeanId(message.getControlMessage().getProcessStructure().getActorTransactionCfg().getEndBeanId());
//				 FireNextMessage(deque,downdeque,message,appcontext);
				return;
			}
		}
		String beanId=getBeanIdFromStep(strunc,message);
		strunc.setFromBeanId(beanId);


		if(appcontext.getBean(beanId) instanceof ActorTransactionCfg){
			AppendCfg2Deque((ActorTransactionCfg)appcontext.getBean(beanId),deque);
//			 FireNextMessage(deque,downdeque,message,appcontext);
			return;
		}
		return  ;


	}

    //得到toBeanId，并准备作为FromBeanId放入
    public static String getBeanIdFromStep(ActorProcessStructure struncture, Message message){
			if(struncture==null){
				return  null;
			}
			Map condtions= struncture.getActorTransactionCfg().getSteps();
		if(condtions==null||condtions.isEmpty()){

			//condtions 为空，直接执行EndActor
			return struncture.getActorTransactionCfg().getEndBeanId();
		}
			List fromBeanIdCondtions=(List)condtions.get(struncture.getFromBeanId());
			if(fromBeanIdCondtions==null||fromBeanIdCondtions.isEmpty()){
				//condtions 为空，直接执行EndActor
				return struncture.getActorTransactionCfg().getEndBeanId();
			}

			for(int i=0;i<fromBeanIdCondtions.size();i++){
				Map tmpMap=(Map)fromBeanIdCondtions.get(i);
				Object condtion=tmpMap.get("conditon");
				String toBeanId=(String)tmpMap.get("toBeanId");
				String async=(String)tmpMap.get("async");

				boolean isTrue = getConditonValue(message, condtion);
				if(isTrue){
 						logger.info("Condtion---"+condtion);

					return toBeanId;
				}

			}
			return null;

	}

	private static void SendAsyncMessage(Message message, ApplicationContext appcontext, String toBeanId) {
		ActorTransactionCfg cfg=(ActorTransactionCfg)appcontext.getBean(toBeanId);
		AsyncMessage asyncMessage=new AsyncMessage(message.getContext());
		try {
			message.getControlMessage().getMessageDispatcher().startMessage(asyncMessage, cfg,true);
		} catch (Exception e) {
			if (logger.isTraceEnabled()) {
				logger.trace("processGetToBeanId(SpringControlMessage, Message, ApplicationContext)"); //$NON-NLS-1$
			}
		}
		return;
	}

	private static boolean getConditonValue(Message message, Object condtion) {
		boolean isTrue;
		if(condtion==null||condtion.equals("")){
			return true;
		}
		ognl.Node  node=(ognl.Node)condtion;
		OgnlContext context =  (OgnlContext) Ognl.createDefaultContext(null);
		isTrue=((Boolean)node.getAccessor().get(context, message)).booleanValue();
		if (logger.isDebugEnabled()) {
			logger.debug("condtion-----"+condtion+"---"+isTrue);
		}


		return isTrue;
	}

	public static String  getPlaceHolderActorId(ActorTransactionCfg cfg){
		
		return (String)cfg.getGlobal().getParams().get("placeholderActorId");
	}


	public static void main(String[] args) throws Exception{
		Map map=new HashMap();
		map.put("Result", "2");
		String tree="Result!=1";
		if (logger.isTraceEnabled()) {
			logger.trace("main(String[]) - " + Ognl.getValue(tree, map)); //$NON-NLS-1$
		}
	}
}
