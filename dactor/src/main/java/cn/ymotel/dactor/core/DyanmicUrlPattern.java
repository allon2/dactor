package cn.ymotel.dactor.core;

public interface DyanmicUrlPattern<T> {
    public default String[] getPatterns(T request){return null;};
    public default String[] getExcludePatterns(T request){return null;};
}
