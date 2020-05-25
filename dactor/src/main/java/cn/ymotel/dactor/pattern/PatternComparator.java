package cn.ymotel.dactor.pattern;

import javax.servlet.DispatcherType;
import java.util.Comparator;

public class PatternComparator implements Comparator<MatchPair> {
    private Comparator comparator=null;

    public PatternComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    /**
     *
     * @param pair1
     * @param pair2
     * @return 1表示两个数位置交换，-1表示不交换
     */
    @Override
    public int compare(MatchPair pair1, MatchPair pair2) {


        if(DispatcherType.ERROR.name().equals(pair1.getDispatcherType())&&DispatcherType.ERROR.name().equals(pair2.getDispatcherType())){
            if(pair1.getHttpStatus()!=null){
                return -1;
            }
            if(pair2.getHttpStatus()!=null){
                return 1;
            }
            return -1;
        }
        if(DispatcherType.ERROR.name().equals(pair1.getDispatcherType())&&DispatcherType.REQUEST.name().equals(pair2.getDispatcherType())) {
            return  -1;
        }
        if(DispatcherType.REQUEST.name().equals(pair1.getDispatcherType())&&DispatcherType.ERROR.name().equals(pair2.getDispatcherType())) {
            return  1;
        }
        if(pair1.getMatchPattern().equals(pair2.getMatchPattern())){
            return compareServerNameAndMethod(pair1, pair2);
        }

        int i=comparator.compare(pair1.getMatchPattern(),pair2.getMatchPattern());
        if(i==0){
            return compareServerNameAndMethod(pair1, pair2);
        }
        return i;
    }

    private int compareServerNameAndMethod(MatchPair pair1, MatchPair pair2) {
        int left = 0;
        int right = 0;
        if (pair1.getServerName() != null) {
            left++;
        }
        if (pair2.getServerName() != null) {
            right++;
        }
        if (pair1.getDispatcherType()== null) {
            left++;
        }
        if (pair2.getDispatcherType() != null) {
            right++;
        }
        if (pair1.getHttpStatus()== null) {
            left++;
        }
        if (pair2.getHttpStatus() != null) {
            right++;
        }

        if (pair1.getMethod() != null) {
            left++;
        }
        if (pair2.getMethod() != null) {
            right++;
        }
        int rtn = right - left;
        if (rtn <= 0) {
            return -1;
        }
        return 1;
    }
}
