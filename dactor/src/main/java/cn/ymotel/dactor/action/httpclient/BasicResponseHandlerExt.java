package cn.ymotel.dactor.action.httpclient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicResponseHandlerExt implements ResponseHandler<String> {
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(BasicResponseHandlerExt.class);

    private String charset = null;

    public BasicResponseHandlerExt() {
        super();
    }

    public BasicResponseHandlerExt(String scharset) {
        if (scharset != null) {
            this.charset = scharset;
        }
    }

    public String handleResponse(HttpResponse response)
            throws HttpResponseException, IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        try {
            if (statusLine.getStatusCode() >= 300) {
                EntityUtils.consume(entity);
                throw new HttpResponseException(statusLine.getStatusCode(),
                        statusLine.getReasonPhrase());
            }


            if (entity == null) {
                return null;
            }
            Charset charset1 = null;
            if (charset != null) {
                charset1 = Charset.forName(charset);
            } else {
                ContentType contentType = ContentType.getOrDefault(entity);
                charset1 = contentType.getCharset();
                if (charset1 == null || "".equals(charset1.toString())) {
                    byte[] bytes = EntityUtils.toByteArray(entity);
                    charset1 = Charset.forName(getContentCharSet(bytes));
                    return new String(bytes, charset1);
                }

                if (charset1 == null || "".equals(charset1.toString())) {
                    charset1 = Charset.forName(charset);
                }
            }

            return EntityUtils.toString(entity, charset1);
        } catch (ParseException e) {
            if (logger.isErrorEnabled()) {
                logger.error("handleResponse(HttpResponse)", e); //$NON-NLS-1$
            }
        } finally {
            EntityUtils.consumeQuietly(entity);
        }
        return "";

    }

    public static String getContentCharSet(byte[] bytes) throws ParseException {
        String charSet = "";
        //(?i)\\bcharset=\\s*\"?([^\\s;\"]*)
        String regEx = "(?=<meta).*?(?<=charset=[\\'|\\\"]?)([[a-z]|[A-Z]|[0-9]|-]*)";
        Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(new String(bytes)); // 默认编码转成字符串，因为我们的匹配中无中文，所以串中可能的乱码对我们没有影响
        boolean result = m.find();
        if (!result) {
            return "gbk";
        }
        if (m.groupCount() == 1) {
            charSet = m.group(1);
        }
        if (charSet.trim().equals("")) {
            charSet = "gbk";
        }

        return charSet;
    }

}
