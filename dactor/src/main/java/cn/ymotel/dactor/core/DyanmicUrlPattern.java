package cn.ymotel.dactor.core;

public interface DyanmicUrlPattern {
    public default String[] getPatterns(){return null;};
    public default String[] getExcludePatterns(){return null;};
}
