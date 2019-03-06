/*
 * @(#)ActorTransactionCfg.java	1.0 2014年9月19日 下午11:34:42
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.ymotel.dactor.core;

import ognl.Node;
import ognl.Ognl;
import ognl.OgnlContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;

/**
 * {type specification, must edit}
 *
 * @author Administrator {must edit, use true name}
 * <p>
 * Created on 2014年9月19日
 * Modification history
 * {add your history}
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class ActorTransactionCfg implements InitializingBean {
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(ActorTransactionCfg.class);


    private ActorTransactionCfg parent;

    public ActorTransactionCfg getParent() {
        return parent;
    }

    private Map overridesMap = new HashMap();

    /**
     * 默认不处理Exception
     */
    private boolean handleException = false;

    /**
     * @return the handleException
     */
    public boolean isHandleException() {
        return handleException;
    }

    /**
     * @param handleException the handleException to set
     */
    public void setHandleException(boolean handleException) {
        this.handleException = handleException;
    }

    /**
     * @param overridesMap the overridesMap to set
     */
    public void setOverridesMap(Map overridesMap) {
        this.overridesMap = overridesMap;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(ActorTransactionCfg parent) {
        this.parent = parent;
    }

    private String id;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    private ActorChainCfg chain;
    private ActorGlobalCfg global;

    /**
     * @return the global
     */
    public ActorGlobalCfg getGlobal() {
        return global;
    }

    /**
     * @param global the global to set
     */
    public void setGlobal(ActorGlobalCfg global) {
        this.global = global;
    }

    /**
     * @return the chain
     */
    public ActorChainCfg getChain() {
        return chain;
    }

    /**
     * @param chain the chain to set
     */
    public void setChain(ActorChainCfg chain) {
        this.chain = chain;
    }

    private String beginBeanId;
    private String endBeanId;
    private String[] urlPattern;
    private Map steps;

    /**
     * @return the beginBeanId
     */
    public String getBeginBeanId() {
        return beginBeanId;
    }

    /**
     * @param beginBeanId the beginBeanId to set
     */
    public void setBeginBeanId(String beginBeanId) {
        this.beginBeanId = beginBeanId;
    }


    /**
     * @return the endBeanId
     */
    public String getEndBeanId() {
        return endBeanId;
    }

    /**
     * @param endBeanId the endBeanId to set
     */
    public void setEndBeanId(String endBeanId) {
        this.endBeanId = endBeanId;
    }


    /**
     * @return the urlPattern
     */
    public String[] getUrlPattern() {
        return urlPattern;
    }

    /**
     * @param urlPattern the urlPattern to set
     */
    public void setUrlPattern(String[] urlPattern) {
        this.urlPattern = urlPattern;
    }

    /**
     * @return the steps
     */
    public Map getSteps() {
        return steps;
    }

    /**
     * @param steps the steps to set
     */
    public void setSteps(Map steps) {
        this.steps = steps;
    }

    private Map results = new HashMap();

    /**
     * @return the results
     */
    public Map getResults() {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(Map results) {
        this.results = results;
    }

    /**
     * 保存可以异步处理的Step
     */
    private Map asyncSteps=new HashMap();

    public Map getAsyncSteps() {
        return asyncSteps;
    }

    public void setAsyncSteps(Map asyncSteps) {
        this.asyncSteps = asyncSteps;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.getBeginBeanId() == null || this.getBeginBeanId().trim().equals("")) {

            this.setBeginBeanId((String) this.getGlobal().getParams().get("beginBeanId"));

        }
        if (this.getEndBeanId() == null || this.getEndBeanId().trim().equals("")) {
            this.setEndBeanId((String) this.getGlobal().getParams().get("endBeanId"));
        }
        if(steps==null){
            steps=new HashMap();
        }

        if(this.getGlobal().getParams().get("beginBeanId").equals(this.getBeginBeanId())){
            //不添加
        }else {
            if (!steps.containsKey((String) this.getGlobal().getParams().get("beginBeanId"))) {
                //不添加，使用默认
                List ls = new ArrayList();
                Map tMap = new HashMap();
                tMap.put("fromBeanId", this.getGlobal().getParams().get("beginBeanId"));
                tMap.put("toBeanId", this.getBeginBeanId());
                tMap.put("conditon", null);
                ls.add(tMap);
                steps.put(this.getGlobal().getParams().get("beginBeanId"), ls);
                this.setBeginBeanId((String) this.getGlobal().getParams().get("beginBeanId"));
            }
        }
//        if(!steps.containsKey(this.getEndBeanId())){
//            //不添加，使用默认
//            List ls=new ArrayList();
//            Map tMap=new HashMap();
//            tMap.put("fromBeanId",this.getEndBeanId());
//            tMap.put("toBeanId",this.getGlobal().getParams().get("endBeanId"));
//            tMap.put("conditon",null);
//            ls.add(tMap);
//            steps.put(this.endBeanId,ls);
//
//            this.setEndBeanId((String) this.getGlobal().getParams().get("endBeanId"));
//        }
        compileCondtion(this.steps);
        compileCondtion(this.asyncSteps);
        /**
         * 如果子有使用子的，没有再使用父的condtions
         */

//		if(this.parent==null){
//			return ;
//		}
//
//		this.setBeginBeanId(getOverride(this.getBeginBeanId()));
//		this.setEndBeanId(getOverride(this.getEndBeanId()));
//
//
//		Map tmpCondtions=new HashMap();
//		tmpCondtions.putAll(parent.getSteps());
//
//		if(tmpCondtions==null||tmpCondtions.isEmpty()){
//			return ;
//		}
//		if(steps!=null) {
//			for (java.util.Iterator iter = this.steps.entrySet().iterator(); iter.hasNext(); ) {
//				Map.Entry entry = (Map.Entry) iter.next();
//				tmpCondtions.put(getOverride(entry.getKey()), getOverrideList((List) entry.getValue()));
//			}
//		}
//		/**
//		 * 有父
//		 */
//		this.steps =tmpCondtions;
    }
    private void compileCondtion(Map steps){
        if (steps == null || steps.isEmpty()) {
        } else {


            for (java.util.Iterator iter = steps.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();
                List ls = (List) entry.getValue();
                for (int i = 0; i < ls.size(); i++) {
                    Map tmpMap = (Map) ls.get(i);
                    String value = (String) tmpMap.get("conditon");
                    if (value == null || value.equals("")) {
                        continue;
                    }


//						Message  root = new HashMap();
                    OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null);
                    //
                    try {
                        Node node = (Node) Ognl.compileExpression(context, null, value);
                        tmpMap.put("conditon", node);
                        continue;
                    } catch (Exception e) {
                        if (logger.isTraceEnabled()) {
                            logger.trace("afterPropertiesSet()"); //$NON-NLS-1$
                        }
                    }


                }
             }


         }
    }
    public String getOverride(Object key) {
        if (overridesMap.containsKey(key)) {
            return (String) this.overridesMap.get(key);
        }
        return (String) key;
    }

    public List getOverrideList(List list) {
        List rtnList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Map rtnMap = new HashMap();
            Map tmpMap = (Map) list.get(i);
//			rtnMap.putAll(tmpMap);
            for (java.util.Iterator iter = tmpMap.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();
                String value = getOverride(entry.getValue());

                if (!entry.getKey().equals("conditon")) {

                    rtnMap.put(entry.getKey(), value);
                    continue;
                }
                if (value == null || value.trim().equals("")) {

                    rtnMap.put(entry.getKey(), value);
                    continue;
                }


                if (entry.getKey().equals("conditon")) {

//					Message  root = new HashMap();
                    OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null);
//
                    try {
                        Node node = (Node) Ognl.compileExpression(context, null, value);
                        rtnMap.put(entry.getKey(), node);
                        continue;
                    } catch (Exception e) {
                        if (logger.isTraceEnabled()) {
                            logger.trace("getOverrideList(List)"); //$NON-NLS-1$
                        }
                    }


                }
                rtnMap.put(entry.getKey(), value);


            }
            rtnList.add(rtnMap);
        }
        return rtnList;
    }

    @Override
    public String toString() {
        return "ActorTransactionCfg [parent=" + parent + ", overridesMap=" + overridesMap
                + ", handleException=" + handleException + ", id=" + id
                + ", chain=" + chain + ", global=" + global + ", beginBeanId="
                + beginBeanId + ", endBeanId=" + endBeanId + ", urlPattern="
                + Arrays.toString(urlPattern) + ", steps=" + steps
                + ", results=" + results + "]";
    }
}
