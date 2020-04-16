package cn.ymotel.dactor.async.web.view;

import cn.ymotel.dactor.Constants;
import cn.ymotel.dactor.message.Message;
import cn.ymotel.dactor.message.ServletMessage;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipView implements HttpView<ServletMessage> {
    private String content = Constants.CONTENT;

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }
    @Override
    public void render(ServletMessage message, String viewName) {
        if (message instanceof ServletMessage) {
        } else {
            return;
        }


        ServletMessage lMessage = (ServletMessage) message;
        if (lMessage.getAsyncContext().getResponse().isCommitted()) {
            return;
        }
        if (this.getContentType() != null) {
            lMessage.getAsyncContext().getResponse().setContentType(getContentType());
        }
        if(viewName==null||viewName.equalsIgnoreCase("")){
        }else{
            ((HttpServletResponse)lMessage.getAsyncContext().getResponse()).setHeader("Content-Disposition","attachment;filename="+viewName);
        }
       Map content= message.getContextData(this.content);
        byte[] bs=getByte(content);
        try {
            lMessage.getAsyncContext().getResponse().getOutputStream().write(bs);
            lMessage.getAsyncContext().getResponse().getOutputStream().flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            lMessage.getAsyncContext().complete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public byte[] WriteStream(InputStream input) {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();

        try {
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                byteArrayOutputStream.write(buffer, 0, n);
            }


            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        } catch (IOException e) {

        }
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {

            }
        }
        return  byteArrayOutputStream.toByteArray();
    }
    public byte[] getByte(Map content){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream=new ZipOutputStream(byteArrayOutputStream);
        for(java.util.Iterator iter=content.entrySet().iterator();iter.hasNext();){
            Map.Entry entry=(Map.Entry)iter.next();
            String key=entry.getKey().toString();
            Object value=entry.getValue();
            ZipEntry zipEntry=new ZipEntry(key);
            if(value instanceof byte[]){
                try {
                    zipOutputStream.putNextEntry(zipEntry);
                    zipOutputStream.write((byte[])value);
                    zipOutputStream.flush();
                    zipOutputStream.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if(value instanceof OutputStream){
                byte[] bs=WriteStream((InputStream)value);
                try {
                    zipOutputStream.putNextEntry(zipEntry);
                    zipOutputStream.write(bs);
                    zipOutputStream.flush();
                    zipOutputStream.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(value instanceof  String){
                try {
                    zipOutputStream.putNextEntry(zipEntry);
                    zipOutputStream.write(((String)value).getBytes("UTF-8"));
                    zipOutputStream.flush();
                    zipOutputStream.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        try {
            zipOutputStream.flush();
            zipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }
    @Override
    public String getContentType() {
        return "application/x-zip-compressed";
    }

    @Override
    public void setContentType(String contentType) {

    }
}
