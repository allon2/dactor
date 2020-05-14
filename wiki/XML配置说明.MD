## XML配置说明

  以下为XML配置例子

```xml
  <actor id="randomTxt" parent="actorhttpcore" beginBeanId="randomTxtActor">
        <steps>
            <step fromBeanId="randomTxtActor" toBeanId="placeholderActor" conditon=""/>
            <step fromBeanId="placeholderActor" toBeanId="endActor" conditon=""/>
        </steps>
 <results>
      <result name="success">img:</result>
   </results>
    </actor>
	<beans:bean id="ImgCodeActor" class="com.dragon.imgcode.ImgCodeGeneratorActor" parent="AbstractActor"></beans:bean>

```

actor属性说明

| 名称              | 默认       | 说明                                                         |
| ----------------- | ---------- | ------------------------------------------------------------ |
| id                | 无         | 同spring bean ID，全局唯一                                   |
| parent            | 无         | 执行到父actor的placeholder，再执行该actor的step              |
| chain             | 无         | 责任链中处理公共类问题，比如统一返回的view问题               |
| beginBeanId       | beginActor | step从beginActor开始执行                                     |
| endBeanId         | endActor   | steps中找不到则执行此beanId，并返回到parent或者chain中继续执行 |
| urlPattern        | 无         | 匹配符合AntPathMatcher的路径                                 |
| excludeUrlPattern | 无         | 排除符合AntPathMatcher的路径                                 |
| handleException   | false      | 是否在改actor中处理Exception错误，不处理，则抛给chain中进行处理 |
| domains           | 无         | 特殊情况下，需要匹配域名，此为域名                           |
| timeout           | 无         | 交易的超时时间，如果不设置取系统默认超时时间30s              |
| methods           | 无         | 请求方法名，在以下范围内选择<br/>GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE |

Step属性说明

| 名称       | 默认  | 说明                                                 |
| ---------- | ----- | ---------------------------------------------------- |
| fromBeanId | 无    | 已经执行的bean的Id名称                               |
| conditon   | 无    | ognl表达式，可为空，表示true                         |
| toBeanId   | 无    | conditon为true，则执行toBeanId中对应的bean Id        |
| async      | false | 是否是异步，异步则不需要返回，                       |
| data       | 无    | 执行完toBeanId后，将返回结果放到的message中的对应key |
| xpoint     | 无    | 保留，将来可视化时图中的x坐标                        |
| ypont      | 无    | 保留，将来可视化时图中的y坐标                        |

result属性

| 名称  | 默认    | 说明                                                         |
| ----- | ------- | ------------------------------------------------------------ |
| name  | success | 通过设置message中的state可在同一个代码中返回不同的内容，如果state为1，则name为success1 |
| value | 无      | 参考view视图类型                                             |



