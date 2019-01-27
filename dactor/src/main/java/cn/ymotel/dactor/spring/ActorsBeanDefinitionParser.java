package cn.ymotel.dactor.spring;

import java.util.List;

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

public class ActorsBeanDefinitionParser  implements BeanDefinitionParser {
 private NamespaceHandlerSupport handler;

public NamespaceHandlerSupport getHandler() {
	return handler;
}

public void setHandler(NamespaceHandlerSupport handler) {
	this.handler = handler;
}
 
//	@Override
//	protected boolean shouldGenerateId() {
//	  return true;
//	}

//	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String namespace=element.getAttribute("namespace");
		List<Element> childElts = DomUtils.getChildElements(element);
		for (Element elt: childElts) {
			
			if(namespace!=null&&!namespace.trim().equals("")){
				elt.setAttribute("id", namespace+"."+elt.getAttribute("id"));
			}else{
				handler.parse(elt, parserContext);
				continue;
			}
			 

				HandleBean(elt,parserContext);
			 
		 }
		return null;
		 

	        

	}
	public void HandleBean(Element elt, ParserContext parserContext){

		if(elt.getLocalName().equals("bean")&&parserContext.getDelegate().isDefaultNamespace(elt)){
		}else{
			handler.parse(elt, parserContext);

			return ;
		}
		
		
		BeanDefinitionHolder bdHolder = parserContext.getDelegate().parseBeanDefinitionElement(elt);
		if (bdHolder != null) {
			bdHolder = parserContext.getDelegate().decorateBeanDefinitionIfRequired(elt, bdHolder);
			try {
				// Register the final decorated instance.
				BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder,parserContext.getRegistry());
			}
			catch (BeanDefinitionStoreException ex) {
				parserContext.getReaderContext().error("Failed to register bean definition with name '" +
						bdHolder.getBeanName() + "'", elt, ex);
			}
			// Send registration event.
			parserContext.getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
		}
		
 		
	}


	 
}
