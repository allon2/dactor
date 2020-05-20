package cn.ymotel.dactor.pattern;

import cn.ymotel.dactor.core.DyanmicUrlPattern;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

public class DayNamicPattern implements DyanmicUrlPattern , InitializingBean {
    @Override
    public String[] getPatterns(Object request) {
        String[] ss=new String[1];
        ss[0]="/c.json";
        return ss;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("init DayNamicPattern");
    }
}
