package cn.ymotel.dactor.async.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StaticResourceRequestHandler implements HttpRequestHandler {
    private UrlPathHelper urlPathHelper=new UrlPathHelper();
    private static final Log logger = LogFactory.getLog(StaticResourceRequestHandler.class);

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String UrlPath=  urlPathHelper.getLookupPathForRequest(request);
        //404 forbidden
        if(isInvalidPath(UrlPath)){
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return ;
        }
        ClassPathResource classPathResource=new ClassPathResource("/META-INF/resources"+UrlPath);
//        System.out.println(classPathResource.getPath()+"---"+classPathResource.getURL());
//        System.out.println("exists---"+classPathResource.exists());
        byte[] out=null;
        try {
            out=StreamUtils.copyToByteArray(classPathResource.getInputStream());
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return ;
        }

        String mediaType = getMediaType(request, UrlPath);
        if(mediaType!=null){
            response.setContentType(mediaType);
        }
        response.getOutputStream().write(out);
        response.getOutputStream().flush();

    }
    protected boolean isInvalidPath(String path) {
        if (path.contains("WEB-INF") || path.contains("META-INF")) {
            if (logger.isWarnEnabled()) {
                logger.warn("Path with \"WEB-INF\" or \"META-INF\": [" + path + "]");
            }
            return true;
        }
        if (path.contains(":/")) {
            String relativePath = (path.charAt(0) == '/' ? path.substring(1) : path);
            if (ResourceUtils.isUrl(relativePath) || relativePath.startsWith("url:")) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Path represents URL or has \"url:\" prefix: [" + path + "]");
                }
                return true;
            }
        }
        if (path.contains("..") && StringUtils.cleanPath(path).contains("../")) {
            if (logger.isWarnEnabled()) {
                logger.warn("Path contains \"../\" after call to StringUtils#cleanPath: [" + path + "]");
            }
            return true;
        }
        return false;
    }
    public String getMediaType(HttpServletRequest request,String urlpath){
        MediaType mediaType = null;
        ServletContext servletContext=request.getServletContext();
        String mimeType = servletContext.getMimeType(urlpath);
        if (StringUtils.hasText(mimeType)) {
            mediaType = MediaType.parseMediaType(mimeType);
            return mediaType.toString();
        }


        if (mediaType == null) {
            mediaType = MediaTypeFactory.getMediaType(urlpath).orElse(null);
        }
        if (StringUtils.hasText(mimeType)) {

            return mediaType.toString();
        }

        return ContentTypeUtil.getContentType(StringUtils.getFilenameExtension(urlpath));

    }

}
