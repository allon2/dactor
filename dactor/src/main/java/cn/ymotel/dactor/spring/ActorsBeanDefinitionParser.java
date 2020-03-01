package cn.ymotel.dactor.spring;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

public class ActorsBeanDefinitionParser implements BeanDefinitionParser {
    public static final String DACTOR_SCHEMA = "http://www.ymotel.cn/schema/dactor";
    private NamespaceHandlerSupport handler;

    public NamespaceHandlerSupport getHandler() {
        return handler;
    }

    public void setHandler(NamespaceHandlerSupport handler) {
        this.handler = handler;
    }

    private void appendIdNameSpace(Element elt,String namespace){
        if(namespace==null||namespace.trim().equals("")){
            return ;
        }
        String id=elt.getAttribute("id");
        if(id==null||id.trim().equals("")){
            return ;
        }
        /**
         * 已经追加过的，不再追加
         */
        if(id.startsWith(namespace+".")){
            return ;
        }
        elt.setAttribute("id", namespace + "." + id);


    }
    //	@Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String namespace = element.getAttribute("namespace");
        List<Element> childElts = DomUtils.getChildElements(element);
        for (Element elt : childElts) {
            appendIdNameSpace(elt,namespace);
                HandleBean(elt, parserContext);



        }
        return null;


    }

    public void HandleBean(Element elt, ParserContext parserContext) {
        String namespaceURI= parserContext.getDelegate().getNamespaceURI(elt);

        if(DACTOR_SCHEMA.equals(namespaceURI)){
           handler.parse(elt, parserContext);
            return ;
       }


        BeanDefinitionHolder bdHolder = parserContext.getDelegate().parseBeanDefinitionElement(elt);
        if (bdHolder != null) {
            bdHolder = parserContext.getDelegate().decorateBeanDefinitionIfRequired(elt, bdHolder);
            try {
                // Register the final decorated instance.
                BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, parserContext.getRegistry());
            } catch (BeanDefinitionStoreException ex) {
                parserContext.getReaderContext().error("Failed to register bean definition with name '" +
                        bdHolder.getBeanName() + "'", elt, ex);
            }
            // Send registration event.
            parserContext.getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
        }


    }


}
