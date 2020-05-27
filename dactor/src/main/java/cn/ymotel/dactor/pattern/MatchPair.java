package cn.ymotel.dactor.pattern;

import javax.servlet.DispatcherType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchPair<T> {
    private List<String> matchPatterns;
    private String matchPattern;
    private String method;
    private String serverName;
    private String dispatcherType;
    private  Integer httpStatus;
    private String chain;

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public String getDispatcherType() {
        return dispatcherType;
    }

    public void setDispatcherType(String dispatcherType) {
        this.dispatcherType = dispatcherType;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    private Map extractMap;

    public Map getExtractMap() {
        return extractMap;
    }

    public void setExtractMap(Map extractMap) {
        this.extractMap = extractMap;
    }

//    private  boolean completeMatch=false;
//
//    public boolean isCompleteMatch() {
//        return completeMatch;
//    }
//
//    public void setCompleteMatch(boolean completeMatch) {
//        this.completeMatch = completeMatch;
//    }

    public String getMatchPattern() {
        return matchPattern;
    }

    public void setMatchPattern(String matchPattern) {
        this.matchPattern = matchPattern;
    }

    private T bean;

    public List<String> getMatchPatterns() {
        return matchPatterns;
    }

    public void setMatchPatterns(List<String> matchPatterns) {
        this.matchPatterns = matchPatterns;
    }

    public T getBean() {
        return bean;
    }

    public void setBean(T bean) {
        this.bean = bean;
    }
    private Map patternMap=null;
    public Map convert2PatternMap(){
        if(patternMap!=null){
            return patternMap;
        }
        Map rtnMap=new HashMap();
        if(matchPatterns ==null){
            patternMap=rtnMap;
            return rtnMap;
        }
        for(int i = 0; i< matchPatterns.size(); i++){
            rtnMap.put(matchPatterns.get(i),bean);
        }
        patternMap=rtnMap;
        return rtnMap;
    }

}
