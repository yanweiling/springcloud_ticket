1.新建registry，测试:http://localhost:8761 登录账号yanweiling 123456
2.新建proxy
3.新建user项目 

启动registry
启动proxy
启动user
之后
直接访问user接口：http://localhost:8081/api/customer 

通过proxy访问user接口：http://localhost:8888/user/api/customer

需要在proxy中配置
#避免非法服务注册
security:
  basic:
    enabled: false
    
    这样，访问http://localhost:8888/user/api/customer的时候，就不会提示要输入用户名和密码了
    
    在proxy中新增
    #设置路由规则
    zuul:
      routes:
        userApi:
          path: /home/**
          stripPrefix: false
          serviceId: user
          请求http://localhost:8888/home/ 可以直接访问到http://localhost:8081/home
          
---
registiry 增加hystrix dashboard
pom中新增
spring-cloud-starter-hystrix-dashboard
启动类上增加@EnableHystrixDashboard   

访问：http://localhost:8761/hystrix
在url中输入 包含了hystrix客户端的服务 因为proxy增加了zuul组件，zuul组件已经内置了hystrix，所以
输入:http://localhost:8888/hystrix.stream
然后点击monitro stream    
          
    
 ---
 在user服务上增加hystrix监控
1.pom中增加依赖

      <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-hystrix</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-actuator</artifactId>
            </dependency>
2.启动类上增加

    @EnableHystrix
    
    启动registry，启动proxy，启动user以后，我们可以看到http://localhost:8888/hystrix.stream 下的hystrix监控数据
    现在我们已经在user上也加上hystrix了，
    但是查看user端口的hystrix监控数据（http://localhost:8081/hystrix.stream ）却访问不到任何数据
    
3.解决办法，需要手动加上@HystrixCommand

例如：需要在请求方法上加上@HystrixCommand

        @GetMapping("")
        @HystrixCommand
        public String get() {
            return "Welcome!";
        }
----

###演示user服务访问order服务
user的pom中增加

    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-openfeign</artifactId>
     </dependency>             
               
启动类上增加

    @EnableFeignClients
    
新增feignClient
    
    @FeignClient(value = "order",path = "/api/order")
    public interface OrderClient{
    
        /**
         * 对于feign的规范，其中@PathVariable中name必须要写，否则报错
         * @param id
         * @return
         */
        @GetMapping("/{id}")
        String getMyOrder(@PathVariable(name = "id") Long id);
    
    
    }
    
    
----
zuul proxy默认的请求超时时间是很短的，代理服务需要设置请求超时时间----具体实现方式待研究 
---

#购票业务流程演示
启动registry，启动proxy，启动order，ticket，user服务以后
访问

    POST http://localhost:8888/order/api/order
    body 体：

    {
        "customerId": 1,
        "title":"new order",
        "amount":100,
        "ticketNum": 100
    }
##测试锁票失败

设置ticket表中的lock_user=2

    POST http://localhost:8888/order/api/order
    body 体：

    {
        "customerId": 1,
        "title":"new order",
        "amount":100,
        "ticketNum": 100
    }
   
 会生成订单表customer_order 标注该订单fail，reason是LOCK_FAIL
 
 ##测试金额不足失败
 设置ticket表中的lock_user=null
 设置cusetomer的余额是90
 
    POST http://localhost:8888/order/api/order
    body 体：

    {
        "customerId": 1,
        "title":"new order",
        "amount":100,
        "ticketNum": 100
    }
   
 会生成订单表customer_order 标注该订单fail，reason是NOT_ENOUGH_DEPOSIT
 
   