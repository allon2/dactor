package cn.ymotel.dactor.async.web.view;

import cn.ymotel.dactor.message.ServletMessage;

import java.io.File;

public class DownloadView extends StreamView {
    public static String FILE_NAME = "_FileName";

    public static String getFILE_NAME() {
        return FILE_NAME;
    }

    @Override
    public void renderInner(ServletMessage message, String defaultMessage) {
        String fileName = "";

        if (message.getContext().containsKey(FILE_NAME)) {
            fileName = (String) message.getContext().get(FILE_NAME);
        } else {


            Object obj = message.getContext().get(this.getContent());
            if (obj instanceof File) {
                fileName = ((File) obj).getName();
            }
        }
        /**
         * 设置下载文件格式
         */
        message.getResponse().setHeader("Content-Disposition", "attachment; filename=" + fileName);

        super.renderInner(message, defaultMessage);
    }

}
