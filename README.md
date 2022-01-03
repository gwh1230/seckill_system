jmeter linux命令
jmeter -n -t first.jmx -l result.jtl

rabbitMQ与Redis同一个地址
rabbitmq启动
systemctl start rabbitmq-server.service
systemctl status rabbitmq-server.service
本地浏览器访问rabbitmq地址
http://192.168.75.21:15672
username: guest
password: guest

61
#一、压测  
##1.商品列表压测
接口：/goods/toList  
线程数1000，循环10次  
###1.1 优化前
windows  QPS:  1332  
Linux    QPS:   207  

###1.2 添加页面缓存
windows  QPS:  2342
 

##2.秒杀接口压测
秒杀接口：/seckill/doSeckill  
线程数1000，循环10次  
windows优化前QPS:  785.9  
Linux优化前QPS:    170  

使用缓存优化后
windows的QPS:    1356

使用Redis预减库存、RabbitMQ后
windows的QPS: 2454

#二、内容
##1. 简单的秒杀系统
##2 缓存
1.页面缓存  
2.url缓存  
3.对象缓存  
4.前后端分离，页面静态化  
(1)秒杀页面静态化  
(2)订单详情页面静态化  

##3.问题 
1.库存超卖   
解决方案   
(1)减库存时判断库存大于0   
2.同一个人秒杀多个  
解决方案  
(1)添加唯一索引  
用户ID+商品id的唯一索引，解决同一个用户秒杀多个商品  
(2)该订单存入redis缓存，同一个人并发多次秒杀，可以从redis读到数据，则不能秒杀。
3.预减库存  
通过实现InitializingBean接口，重写初始化方法，在启动时将库存加载到redis中，每次秒杀偶从redis减库存
4.秒杀对接RabbitMQ  
将秒杀任务发送RabbitMQ  
5.使用Redis分布式锁优化预减库存  


##4.安全问题
1.隐藏秒杀地址，改为获取秒杀地址  
2.使用验证码，防止恶意脚本进行秒杀和拉长访问时间  
3.秒杀接口限流，使用计数器法
  其他限流算法：漏斗算法、令牌桶算法  
4.通用接口限流，使用注解拦截器优化代码  

##4.RabbitMQ
###1.springboot整合RabbitMQ
(1)新建虚拟机，安装RabbitMQ，并安装管理面插件  
虚拟机：192.168.75.21  
管理面  
    url: 192.168.75.21:15672  
    username: guest  
    password: guest  
(2)引入依赖，yml中配置主机密码等属性  
(3)新建RabbitMQ管理类，新建队列Queue并注入  
(4)创建生产者，消费者  
###2.RabbitMQ四种交换机模式
(1)fanout 广播模式  
(2)direct 直连模式  
生产者发送给交换机，交换机转发给路由键，路由键发送给指定队列  
(3)topic模式
(4)headers模式

  

