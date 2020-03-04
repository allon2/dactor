package cn.ymotel.dactor.spring.annotaion;

import cn.ymotel.dactor.core.UrlMapping;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;
import java.util.function.Predicate;

public class StaticResourceImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Map<String, Object>  annotationAttributes=importingClassMetadata.getAnnotationAttributes(StaticResource.class.getName());
        String[] locations=(String[])annotationAttributes.get("locations");
        if(locations==null||locations.length<1){
            return null;
        }
        for(int i=0;i<locations.length;i++){
            UrlMapping.addStaticPath(locations[i]);
        }
        return new String[0];
    }

    @Override
    public Predicate<String> getExclusionFilter() {
        return null;
    }
}
