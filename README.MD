[![GitHub release](https://img.shields.io/github/release/allon2/dactor.svg)](https://github.com/allon2/dactor/releases)
# **DActor**  
## Introduction
DActor框架基于协程思想设计，可同时支持同步和异步代码，简化在线异步代码的开发，用同步代码的思维来开发异步代码，兼顾异步代码的高并发、无阻塞和同步代码的易读性，可维护性。
最大程度的降低阻塞，提高单个线程的处理能力，并可有效的降低线程数。
## 项目地址
 GitHub:https://github.com/allon2/dactor  
 GitEE:https://gitee.com/handyun/dactor  
## QQ交流群
 783580303
## Overview
目前开发过程中的几个常见模型
 -  同步编程
    所有步骤都在一个主线程中完成,调用一个方法，等待其响应返回。一个请求占用一个线程,在有数据库操作、TCP和Http通讯时因为有阻塞情况，会导致占用线程占用而无法及时释放  
    ，因此在同步交易中引入了线程池概念,提高系统的吞吐量
 -  异步编程
    所有步骤都可在不同线程中完成,调用一个方法，不等待响应既返回，典型交易如NodeJs。
    目前市面上的异步框架都比较复杂，市面的通用解决方案是CallBack和Promise/Deferred模式模式。  
## 设计思路  
- 为了保留异步的高性能,简化异步的开发模式，同时使得程序更容易被程序员理解，在性能和代码可阅读性中间取得平衡，设计了此框架。    
- 处理步骤:将请求封装为消息,丢入消息队列，寻找合适步骤处理消息,上述过程不断循环，直到所有可用步骤都执行完毕。  
  因为是对消息队列进行处理,对于同步交易，处理完毕即可丢入消息队列。对于异步交易，等待回调完毕再丢入消息队列。  
  两种情况对于框架来说是无差别的。同时因为通过异步交易避免了阻塞情况的发生,所以可在不大幅度提高线程数的情况下，提高吞吐量，  
  同时也可在一定程度避免流量突增的情况发生。  
- 消息队列采用Disruptor的的高性能队列RingBuffer。
- 以Actor协程并发模型为基础设计框架。 
## Features
- 1、集成Netty
- 2、集成HttpClient
- 3、集成HttpServlet
- 4、支持多层父子结构
- 5、支持责任链模式  
- 6、J2EE支持json,csv,pdf,xml,html格式输出
- 7、J2EE支持数据流输出,动态文件下载、动态图片输出、跳转和可根据配置动态输出
 环境要求  
  JDK 1.8  
  Spring FrameWork 5.2.4.RELEASE +  
  Servlet 3.0+(因为需要使用Servlet的异步功能)  
 注意事项  
  请求的完整逻辑是分散在不同的线程中执行的，所以尽量避免使用ThreadLocal
## Release Note  
1.1.1版本  
&emsp;1:增加AfterChain和BeforeChain注解    
&emsp;2:优化相关代码    
&emsp;3:更改DyanmicUrlPattern接口，增加参数        
&emsp;4:将创建责任链方法单独抽取       
&emsp;5:yml文件支持属性自动提示       
1.1.0版本  
&emsp;1:SpringBoot依赖版本升级为2.2.7.RELEASE,其他依赖升级为最新版本  	
&emsp;2:增加MessageSourceFilter,并从AsyncServletFilter中移除相关代码 
&emsp;3:增加DyanmicUrlPattern接口，可在系统中动态修改Pattern   
&emsp;4:Page,PageUtil,AsyncServlet标记为过期   
&emsp;5:增加雪花算法IdWorker,可直接使用IdWorker.getInstance().nextId()获得流水号  
&emsp;6：ServletMessage 增加 getFileBytes,getFileNames,getFiles,getFile,getFileStream,getClientIp,getHeaderIgnoreCase,getHeader等方法  
&emsp;7:注解ActorCfg增加excludeUrlPatterns,domains,view,timeout,methods等参数  
&emsp;8:Message方法中getContextData等修改为泛型,不再需要强转,增加getContextList直接获得客户端提交的列表参数
1.0.18版本  
&emsp;1:支持SpringBoot,只需要引用jar包，即可在springboot中自动启用，进一步简化代码配置    
&emsp;2:服务可优雅停止  
&emsp;3：依赖升级为最新版本  
&emsp;4：Actor接口增加默认方法   
1.0.15版本  
&emsp;1:解决在队列满的情况下，程序无法响应问题    
1.0.14版本  
&emsp;1:解决在队列满的情况下，互锁导致程序宕机问题  
1.0.13版本  
&emsp;1:完善代码解决消费者减少后,消息丢失问题   
&emsp;2:支持url路径中加动态参数  
1.0.12版本  
&emsp;1:动态调节消费者线程数  
&emsp;2：增加maxsize：动态消费最大增加数  
&emsp;3：修改threadNumber为minsize，可不配置，不配置为核心CPU个数  
&emsp;4：增加checktime函数，默认为1s,监控消费者线程是否不足  
&emsp;5：支持Rest格式,如/a/b.json格式，对应内部为namespace为a,actor的id为b  
1.0.11版本    
&emsp;1:对SpringBean进行缓存,缓解getBean造成的锁问题  
&emsp;2:增加对netty http的支持  
&emsp;3:日志默认级别调整为debug  
1.0.10版本    
&emsp; Message增加ControlData，将业务信息和控制信息分离   
1.0.9版本  
&emsp;修正在集成的Netty错误,并增加测试案例  
1.0.8版本  
&emsp;在Steps中的beginBeanId支持用Actor标签的BeanId  
&emsp;增加测试案例  
1.0.7版本  
&emsp;修正Step中所有条件不为真时，报错问题  
1.0.6版本  
&emsp;通过设置async=true的方式,便捷的增加旁路交易  
&emsp;移除对javax.mail-api的依赖  
1.0.5版本  
&emsp;ServletMessage的用户对象直接从会话中取得  
1.0.4版本  
&emsp;优化程序逻辑  
1.0.3版本  
&emsp;Message增加setUser和getUser对象  
1.0.2版本
&emsp;初始化版本
## Getting Started
  example是J2EE程序,下载后，可直接运行,其中集成了若干例子  
    默认使用.do提交相关交易，但如果是.json将会返回json数据
   启动后,在浏览器中输入http://localhost:8080/example/randomTxt2.json  
   输出的是json格式的字符串  
   randomTxt2:只有一级父子关系  
   randomTxt1:有二级父子关系 
   chaintest1:只使用责任链  
   chaintest2:同时使用责任链和一级父子关系  
   exceptionTest:子交易抛出错误,框架对错误的处理  
   randomTxt3为beginBeanId为Actor标签的BeanId例子
   httptest演示的是通过httpclient异步方式访问百度网站   
        访问URL:http://localhost:8080/example/ httptest.do    
   http://localhost:8080/example/np.randomTxt2.json为使用命名空间的例子,相关配置在conf/namespace.xml中。  

   启动后，可在控制台看到内部调用结果  
   ![Image text](https://gitee.com/handyun/dactor/raw/master/1.png)


### Maven dependency

```xml
<dependency>
    <groupId>cn.ymotel</groupId>
    <artifactId>dactor</artifactId>
    <version>1.1.1</version>
</dependency>
```
### Gradle dependency

```
compile group: 'cn.ymotel', name: 'dactor', version:'1.1.1'

```

### 代码简单讲解
执行过程为chain->grandfather->parent->Selft。
依次调用执行责任链中逻辑，grandfather中的逻辑，parent的逻辑和自身逻辑。
chain,grandfather,parent都可为空，不设置
在grandfather和parent中的Steps中至少有一个为placeholderActor交易，以调用子逻辑

整个过程中，需要先设置全局占位符  
    <actor:global id="actorglobal">  
           <actor:param name="beginBeanId" value="beginActor"/>  
           <actor:param name="endBeanId" value="endActor"/>  
       </actor:global>  
交易中如果未填写beginBeanId或者endBeanId时，系统默认使用全局中配置的beginBeanId或者endBeanId  
```
   <actor id="randomTxt" parent="actorhttpcore" beginBeanId="randomTxtActor">
        <steps>
            <step fromBeanId="randomTxtActor" toBeanId="placeholderActor" conditon=""/>
            <step fromBeanId="placeholderActor" toBeanId="endActor" conditon=""/>
        </steps>
    </actor>
```
condtion可为空，空字符串，或者是ognl表达式  
placeholderActor的作用是在暂存当前环境，并调用子交易，待子交易执行完毕后，再恢复当前环境继续执行  
如果在Step中未找到toBeanIdActor，会直接调用endBeanId方法，认为自身交易已执行结束。  
交易的请求和流转信息都保存在Message中  
如果指定handleException=false或者使用默认设置，直接返回父中执行，如果父中也未捕获，则继续返回上一级执行，    
一般来说至少有要有一个actor中指定handleException=true
<bean id="MessageRingBufferDispatcher" class="cn.ymotel.dactor.core.disruptor.MessageRingBufferDispatcher">
</bean>
启动框架接收和执行请求
# **同步异步写法**  
## 同步写法
 ```
  public class BeginActor implements Actor {

    /* (non-Javadoc)
     * @see com.ymotel.util.actor.Actor#HandleMessage(com.ymotel.util.actor.Message)
     */
    @Override
    public Object HandleMessage(Message message) throws Exception {
        return message;

    }

}
 ```
 继承Actor接口，在HandlerMessage返回message方法，框架判断返回不为空的情况下，直接将结果丢给框架进行下一步出来

## 异步写法
 以HttpClientActor为例,在HandlerMessage中最后返回null，框架收到请求后等待下一步处理，释放线程，在HttpClient中的CallBack中调用   
 message.getControlMessage().getMessageDispatcher().sendMessage(message);框架收到后进行下一步处理。

 ```
    public Object HandleMessage(final Message message) throws Exception {
        Map context = message.getContext();

//		
        HttpUriRequest request = getHttpBuild(message.getContext()).build();

        if (referer != null) {
            request.addHeader("Referer", referer);
        }


        /**
         * 通过此处可共用会话，进行类似登录后交易
         */
        HttpClientContext tmplocalContext = null;
        if (context.containsKey(HTTPCLIENT_CONTEXT)) {
            tmplocalContext = (HttpClientContext) context.get(HTTPCLIENT_CONTEXT);
        } else {
            tmplocalContext = HttpClientContext.create();
            CookieStore cookieStore = new BasicCookieStore();
            tmplocalContext.setCookieStore(cookieStore);
            context.put(HTTPCLIENT_CONTEXT, tmplocalContext);
        }

        final HttpClientContext localContext = tmplocalContext;
//        CookieStore cookieStore = new BasicCookieStore();
//        localContext.setCookieStore(cookieStore);
        if (logger.isInfoEnabled()) {
            logger.info("HandleMessage(Message) - httpclient----" + request); //$NON-NLS-1$
        }
//		 final HttpGet httpget = new HttpGet(uri);

        final String tmpcontent = content;
        final String tmpcharset = charset != null ? charset : (String) context.get(CHARSET);


        httpClientHelper.getHttpclient().execute(request, localContext, new FutureCallback<HttpResponse>() {

            public void completed(final HttpResponse response) {
                try {
                    /**
                     * 完成后及时清除
                     */
                    message.getContext().remove(tmpcontent);

                    actorHttpClientResponse.handleResponse(response, localContext, tmpcharset, message);
//					String responseString=HandleResponse((String)message.getContext().get(CHARSET),response);
//					message.getContext().put(RESPONSE, responseString);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    if (logger.isErrorEnabled()) {
                        logger.error("$FutureCallback<HttpResponse>.completed(HttpResponse)", e); //$NON-NLS-1$
                    }
                    message.setException(e);
                    message.getControlMessage().getMessageDispatcher().sendMessage(message);
                }
//                  System.out.println(httpget.getRequestLine() + "->" + response.getStatusLine());
            }

            public void failed(final Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("$FutureCallback<HttpResponse>.failed(Exception)", ex); //$NON-NLS-1$
                }
                message.setException(ex);
                message.getControlMessage().getMessageDispatcher().sendMessage(message);

//                  System.out.println(httpget.getRequestLine() + "->" + ex);
            }

            public void cancelled() {
                Exception exception = new Exception("已取消");
                message.setException(exception);
                message.getControlMessage().getMessageDispatcher().sendMessage(message);

//                  System.out.println(httpget.getRequestLine() + " cancelled");
            }

        });
        return null;
    }
 ```
# **配置和API说明**  
## 配置说明
 通过在xml中的Step实现内部Actor之间的流程跳转
 在配置文件中包含
 Actor、chain、和global配置 。
 程序整个执行顺序为根据交易码找到对应的Actor,然后执行按照chain->parent->selft的顺序进行执行。  
 chain执行到placeholder处,调用parent交易继续执行,在parent交易中执行到placeholder交易后，调用selft自身交易继续执行。
 自身交易执行完毕，弹出parent的placeholder处交易继续执行.parent执行完毕,弹出chain中代码继续执行。
 global配置如下
``` 
  <actor:global id="actorglobal">
         <actor:param name="beginBeanId" value="beginActor"/>
         <actor:param name="endBeanId" value="endActor"/>
   </actor:global>
```
beginBeanId为默认的开始Actor,value中的值是在Spring中对应的beanName，程序初始化时将会取得此值，对未指定beginBeanId或者endBeanId的Actor初始化全局配置。  
beginActor和endActor都需要继承Actor接口。
actor配置如下
```   
    <actor:actor id="actorhttpcore"   parent="chainparent" chain="unLoginChain"  handleException="true"  endBeanId="FinishActor" >

        <actor:steps>
            <actor:step xpoint="" ypont="" fromBeanId="beginActor"  conditon="" toBeanId="placeholderActor"/>
             <actor:step xpoint="" ypont="" fromBeanId="beginActor"  conditon="" async="true"  toBeanId="placeholderActor"/>
           <actor:step xpoint="" ypont=""  fromBeanId="placeholderActor" conditon="context._SUFFIX=='json'"  toBeanId="JsonViewResolverActor"/>
            <actor:step xpoint="" ypont=""  fromBeanId="placeholderActor" conditon="exception==null" toBeanId="ViewResolveActor"/>
            <actor:step xpoint="" ypont=""   fromBeanId="placeholderActor" conditon="exception!=null"  toBeanId="ErrorViewResolveActor"/>
        </actor:steps>
             <results>
                    <result name="success">htmlstream:</result>
             </results>
    </actor:actor>
```
 属性handleException如果不设置的话,遇到异常,程序将会认为子类中已经执行完毕,跳到parent中PlaceHolder处执行。设置为true，将不会直接跳转到parent中，由子类进行自我处理。  
 parent和chain为调用具体交易前需要调用的公共交易，由于大部分交易都有通用的前置交易和统一的后置交易。通过设置parent或者chain，可提高代码复用度。  
 fromBeanId和toBeanId配置的是Actor或者实现Actor接口的beanId。
 parent和chain中的ref都需要是Actor.  
 results中可定义返回的state和需要处理的viewActor  
 async标记是否是旁路交易，默认值为false，为true值时,会将上下文内容设置复制一份，重新生成一份Message,进行执行，不影响主流程。
chain配置
 ```
   <actor:chain id="isLoginChain">
          <list>
              <ref bean="actorhttpcore"></ref>
              <ref bean="isLoginActor1"></ref>
  
          </list>
      </actor:chain>
 ```
chain可直观展现Actor调用顺序.    
在chain中可顺序并列多个parent类。每个parent中的Step都需要有placeHolderActor，以调用子类。      
依次执行list中的交易，再执行自身交易。自身交易执行完毕，再依次回溯责任链中的每个交易，直到无可用交易。 
命名空间
  ```
 <actor:actors xmlns="http://www.ymotel.cn/schema/dactor"
               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               xmlns:aop="http://www.springframework.org/schema/aop"
               xmlns:actor="http://www.ymotel.cn/schema/dactor"
               xmlns:beans="http://www.springframework.org/schema/beans"
               xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
 	http://www.ymotel.cn/schema/dactor http://www.ymotel.cn/schema/dactor.xsd"  namespace="np">
     <!-- parent 和beginBeanId为全局name,randomTxt2在Spring中的全程是np.randomTxt2-->
     <actor id="randomTxt2" parent="actorhttpcore" beginBeanId="randomTxtActor">
     </actor>
 </actor:actors>
  ```
  在actor中可增加命名空间，简化代码开发。在actor中配置namespace=np,则实例中的actor的id会自动拼装为np.randomTxt2  
     http://localhost:8080/example/np.randomTxt2.json为使用命名空间的例子,相关配置在conf/namespace.xml中。  

## 重要类方法说明
cn.ymotel.dactor.core.MessageDispatcher是交易流转的核心接口类   
    public void startMessage(Message message, ActorTransactionCfg actorcfg, boolean blocked) throws Exception  
方法,用于开始整个流程，其中message需要在执行前进行构造，actorcfg可通过spring的getBean方法得到为Actor对象，如下  
```
<actor id="randomTxt1" parent="randomTxt" beginBeanId="randomTxtActor">
    </actor>
```
    通过getBean('randomTxt1')即可得到ActorTransactionCfg对象。  
    blocked为是否阻塞，一般在交易初次放入队列是为false,表示如果队列满，则直接扔给客户端进行处理。为true则一般为内部交易,必须提交给队列进行处理。
  sendMessage方法内部调用，用于将处理完毕的Message重新放入队列，继续下一步流程。
 cn.ymotel.dactor.core.disruptor.MessageRingBufferDispatcher是MessageDispatcher的接口实现类。,在启动Spring是需要在配置中加上
 ```
  <bean id="MessageRingBufferDispatcher" class="cn.ymotel.dactor.core.disruptor.MessageRingBufferDispatcher">
    </bean>
 ```
 MessageRingBufferDispatcher的strategy、bufferSize、threadNumber为三个可设置属性.正常情况下使用默认设置即可。  
 strategy默认使用ringBuffer的BlockingWaitStrategy策略进行调度，如果交易量比较大，可调整此策略。  
 bufferSize默认使用1024。   
 threadNumber默认使用CPU个数的线程数。   
 ## 其他默认Actor说明
 cn.ymotel.dactor.message.Message.Actor,所有需要在执行的交易都必须继承此接口。   
 public Object HandleMessage(Message message) throws Exception;程序通过调用HandleMessage对象,如果返回的不是message对象或者为NULL,则认为此交易是异步执行,不再自行调度。由异步交易在收到请求后，自己调用将Message再此放入队列中。  
 cn.ymotel.dactor.action.PlaceholderActor 交易为特殊交易，用来将当前队列暂存，并调用子交易。  
 cn.ymotel.dactor.action.BeginActor 为Actor中step的默认开始交易。  
 cn.ymotel.dactor.action.EndActor 为Actor中step的默认结束交易。
 cn.ymotel.dactor.action.JsonViewResolverActor为需要返回Json的J2EE view
  cn.ymotel.dactor.action.ViewResolveActor为需要返回J2EE view的统一处理Actor  
  cn.ymotel.dactor.action.httpclient.HttpClientActor 提供的异步调用httpClient的Actor  
cn.ymotel.dactor.action.netty.aysnsocket.TcpClientActor 提供的异步调用netty的Actor  
 ## 交易流程举例说明
 ```
   <actor:actor id="actorhttpcore" handleException="true"  endBeanId="FinishActor" >
 
         <actor:steps>
             <actor:step xpoint="" ypont="" fromBeanId="beginActor"  conditon="" toBeanId="placeholderActor"/>
             <actor:step xpoint="" ypont=""  fromBeanId="placeholderActor" conditon="context._SUFFIX=='json'"  toBeanId="JsonViewResolverActor"/>
             <actor:step xpoint="" ypont=""  fromBeanId="placeholderActor" conditon="exception==null" toBeanId="ViewResolveActor"/>
             <actor:step xpoint="" ypont=""   fromBeanId="placeholderActor" conditon="exception!=null"  toBeanId="ErrorViewResolveActor"/>
         </actor:steps>
 
     </actor:actor>

  <actor id="randomTxt2" parent="actorhttpcore" beginBeanId="randomTxtActor">
     </actor>
 ```
 以上交易的交易流程图如下
 ![Image text](https://gitee.com/handyun/dactor/raw/master/randomTxt2.png)
以上的完整例子都可在example中得到