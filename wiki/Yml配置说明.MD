# SpringBoot Yml说明
   yml配置文件案例:

```
dactor:
  enabled: true
  buffer-size: 1024
  checktime: 1000
  monitor: false
  threadmax: 300
  threadmin: -1
```

配置通用说明如下

| 属性               | 默认值 | 说明                                                         |
| ------------------ | ------ | ------------------------------------------------------------ |
| dactor.enabled     | true   | 是否在SpringBoot中启用dactor <br>true:启用 <br>false:不启用  |
| dactor.buffer-size | 1024   | 瞬时最大可接收并发数。超过队列数，会抛出超过最大链接数错误，需要是2的指数 |
| dactor.checktime   | 1000   | 单位毫秒数，根据checktime监控处理能力，不足增加，有空闲则减少 |
| dactor.monitor     | false  | 是否打开日志监控                                             |
| dactor.threadmax   | 300    | 最大线程并发处理数                                           |
| dactor.threadmin   | -1     | <0:使用当前系统CPU个数                                       |

