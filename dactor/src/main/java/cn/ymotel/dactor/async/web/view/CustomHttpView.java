package cn.ymotel.dactor.async.web.view;

import cn.ymotel.dactor.message.Message;

/**
 * 定制接口继承这个类
 */
public interface CustomHttpView<T  extends Message> extends HttpView<T> {
}
