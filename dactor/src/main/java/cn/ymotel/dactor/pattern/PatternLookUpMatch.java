package cn.ymotel.dactor.pattern;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.*;

public class PatternLookUpMatch<T> {
    private PathMatcher pathMatcher=new AntPathMatcher();

    private List<PatternMatcher> patterns=new ArrayList<>();
    public void add(PatternMatcher<T> matcher){
        patterns.add(matcher);
    }
    public  T lookupMatchBean(String UrlPath,String method,String serverName){
        MatchPair pair= lookupMatchPair(UrlPath,method,serverName);
        if(pair==null){
            return null;
        }
        return  (T)pair.getBean();
    }

    public  MatchPair lookupMatchPair(String UrlPath,String method,String serverName){
        if(patterns.isEmpty()){
            return  null;
        }
        Comparator comparator= pathMatcher.getPatternComparator(UrlPath);
        List rtnList=new ArrayList();
        Map params=new HashMap();
        for(int i=0;i<patterns.size();i++){
            PatternMatcher patternMatcher=patterns.get(i);
            MatchPair pair=patternMatcher.matchePatterns(UrlPath,pathMatcher,method,serverName);
            if(pair==null){
                continue;
            }
            params.putAll(pair.convert2PatternMap());
            rtnList.addAll(pair.getMatchPatterns());
            if(pair.isCompleteMatch()){
                break;
            }
        }

        if(rtnList.size()==0){
            return null;
        }
        if(rtnList.size()==1){
            MatchPair pair=new MatchPair();
            pair.setBean(params.get(rtnList.get(0)));
            pair.setMatchPattern((String)rtnList.get(0));
            return pair ;
        }
        rtnList.sort(comparator);
        MatchPair pair=new MatchPair();
        pair.setBean(params.get(rtnList.get(0)));
        pair.setMatchPattern((String)rtnList.get(0));
        return pair ;


    }
}
