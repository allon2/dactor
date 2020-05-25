package cn.ymotel.dactor.pattern;

import cn.ymotel.dactor.ActorUtils;
import cn.ymotel.dactor.Constants;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PatternLookUpMatch<T> {
    private PathMatcher pathMatcher=new AntPathMatcher();

    private List<PatternMatcher> patterns=new ArrayList<>();
    public void add(PatternMatcher<T> matcher){
        patterns.add(matcher);
    }
    public  T lookupMatchBean(String UrlPath, String method, String serverName, HttpServletRequest request){
        MatchPair pair= lookupMatchPair(UrlPath,method,serverName,request);
        if(pair==null){
            return null;
        }
        return  (T)pair.getBean();
    }

    public  MatchPair lookupMatchPair(String UrlPath, String method, String serverName, HttpServletRequest request){
        if(patterns.isEmpty()){
            return  null;
        }
        Comparator patterncomparator=pathMatcher.getPatternComparator(UrlPath);
        List<MatchPair> rtnList=Collections.synchronizedList(new ArrayList());
//        Map params=new ConcurrentHashMap();
//        Map treeMap=Collections.synchronizedSortedMap(new TreeMap<>(comparator));
        patterns.forEach(matcher -> {
            MatchPair pair=matcher.matchePatterns(UrlPath,pathMatcher,method,serverName,request.getDispatcherType().name(), ActorUtils.getHttpErrorStatus(request),patterncomparator);
            if(pair==null){
               return;
            }
            rtnList.add(pair);
//            params.putAll(pair.convert2PatternMap());
//            rtnList.addAll(pair.getMatchPatterns());
//                if(pair.isCompleteMatch()){
//                    return;
//                }
        });
//        for(int i=0;i<patterns.size();i++){
//            PatternMatcher patternMatcher=patterns.get(i);
//            MatchPair pair=patternMatcher.matchePatterns(UrlPath,pathMatcher,method,serverName);
//            if(pair==null){
//                continue;
//            }
//            params.putAll(pair.convert2PatternMap());
//            rtnList.addAll(pair.getMatchPatterns());
//            if(pair.isCompleteMatch()){
//                break;
//            }
//        }

        if(rtnList.size()==0){
            return null;
        }
        if(rtnList.size()==1){
            MatchPair pair=rtnList.get(0);
//            pair.setMatchPattern((String)pair.getMatchPatterns().get(0));
//            MatchPair pair=new MatchPair();
//            matchPattern=(String)rtnList.get(0);
//            pair.setBean(params.get(matchPattern));
//            pair.setMatchPattern(matchPattern);



            pair.setExtractMap(extractVariables(pair.getMatchPattern(),UrlPath));
            return pair ;
        }
        Comparator comparator= new PatternComparator(patterncomparator);
        rtnList.sort(comparator);
        MatchPair pair=rtnList.get(0);
//        MatchPair pair=new MatchPair();
//        matchPattern=(String)rtnList.get(0);
//        pair.setBean(params.get(matchPattern));
//        pair.setMatchPattern(matchPattern);
        pair.setExtractMap(extractVariables(pair.getMatchPattern(),UrlPath));
        return pair ;


    }
    public Map extractVariables(String MatchPattern,String urlPath){
        if(MatchPattern==null){
            return new HashMap();
        }
       Map map= pathMatcher.extractUriTemplateVariables(MatchPattern,urlPath);
       String path= pathMatcher.extractPathWithinPattern(MatchPattern,urlPath);
       map.put(Constants.EXTRACTPATH,path);
       return map;
    }
}
