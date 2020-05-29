package cn.ymotel.dactor.core;

public interface DyanmicUrlPattern<T> {
    public default String[] getPatterns(T request){return null;};
    public default String[] getExcludePatterns(T request){return null;};

    /**
     * 此规则是否忽略
     * @return false 不忽略，true 忽略
     */
    public default boolean ignore() { return false;}
}
