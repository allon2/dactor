package cn.ymotel.dactor.spring.beandef;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class NameSpaceUtil {
    public static String getNameSpaceActorId(Element element){
        Element node=(Element) element.getParentNode();

        String id=element.getAttribute("id");
        if(id==null||id.equals("")){
            return id;
        }

        if(node!=null) {
            String namespace=node.getAttribute("namespace");
            if(namespace==null||namespace.equals("")){
                return id;
            }
            return namespace+"."+element.getAttribute("id");
        }
        return id;
    }
}
