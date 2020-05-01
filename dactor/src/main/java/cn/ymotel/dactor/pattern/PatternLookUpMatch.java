package cn.ymotel.dactor.pattern;

import cn.ymotel.dactor.Constants;
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
        String matchPattern=null;
        if(rtnList.size()==1){
            MatchPair pair=new MatchPair();
            matchPattern=(String)rtnList.get(0);
            pair.setBean(params.get(matchPattern));
            pair.setMatchPattern(matchPattern);



            pair.setExtractMap(extractVariables(matchPattern,UrlPath));
            return pair ;
        }
        rtnList.sort(comparator);
        MatchPair pair=new MatchPair();
        matchPattern=(String)rtnList.get(0);
        pair.setBean(params.get(matchPattern));
        pair.setMatchPattern(matchPattern);
        pair.setExtractMap(extractVariables(matchPattern,UrlPath));
        return pair ;


    }
    public Map extractVariables(String MatchPattern,String urlPath){
       Map map= pathMatcher.extractUriTemplateVariables(MatchPattern,urlPath);
       String path= pathMatcher.extractPathWithinPattern(MatchPattern,urlPath);
       map.put(Constants.EXTRACTPATH,path);
       return map;
    }
}
