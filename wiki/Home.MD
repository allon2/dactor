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
chain配置
 ```
   <actor:chain id="unLoginChain">
        <actor:before>
            <actor:ref bean="actorhttpcore"></actor:ref>
        </actor:before>
    </actor:chain>
```  
chain可直观展现Actor调用顺序.    
在chain中可顺序并列多个parent类。每个parent中的Step都需要有placeHolderActor，以调用子类。      
依次执行before中的交易，再执行自身交易。自身交易执行完毕，再依次回溯责任链中的每个交易，直到无可用交易。  
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
 ![Image text](randomTxt2.png)
以上的完整例子都可在example中得到

 

 

 