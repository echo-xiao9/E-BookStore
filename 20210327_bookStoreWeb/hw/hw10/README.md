# hw10: Clustering & Docker

请你根据上课内容，针对你在E-BookStore项目中的数据库设计，完成下列任务：
1.请你参照课程样例，构建你的E-BookStore的集群，它应该至少包含 1 个nginx实例(负载均衡) + 1 个Redis实例(存储session) + 2 个Tomcat实例。(4分）
2.所使用的框架不限，例如可以不使用nginx而选用其他负载均衡器，或不使用Redis而选用其他缓存工具。
3.参照上课演示的案例，将上述系统实现容器化部署，即负载均衡器、缓存、注册中心和服务集群都在容器中部署。 (1分）
– 请提交一份Word文档，详细叙述你的实现方式；并提交你的工程代码。
评分标准：
– 能够正确地部署和运行上述系统，在验收时需当面演示。
– 部署方案不满足条件或无法正确运行，则视情况扣分。

数据仓库：大量数据来源多个数据源，读入之后清洗加载，之后按照需求建立主题表， 是需求驱动的（OLAP）

OLTP： 事务驱动， 需要承接这么多的请求

shard server： 三备份， share nothing 

## Session + Redis

application.properties

```python
...
#redis
spring.session.store-type=redis
spring.session.redis.namespace=spring:session
spring.redis.host= localhost
spring.redis.port= 6379
spring.redis.password=
spring.redis.timeout= 6000
spring.redis.jedis.pool.max-active=8
spring.redis.jedis.pool.max-wait= -1ms
spring.redis.jedis.pool.max-idle= 8
spring.redis.jedis.pool.min-idle= 0
spring.redis.database = 0
```

增加依赖

```python
<dependency>
            <groupId>org.springframework.session</groupId>
            <artifactId>spring-session-data-redis</artifactId>
</dependency>
```

实例项目，登陆之后可以得到信息

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled.png)

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%201.png)

自己的项目中，一开始没有登陆时无法获得用户购物车信息，登陆之后

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%202.png)

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%203.png)

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%204.png)

![前端在未登陆状态下查看order，会被要求登陆。](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%205.png)

前端在未登陆状态下查看order，会被要求登陆。

![redis中记录的session](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%206.png)

redis中记录的session

## Nginx

安装 brew install nginx

启动 brew start/restart nginx

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%207.png)

### 实例程序

nginx.config 位于/usr/local/etc/nginx   编辑nginx.config，增加

![截屏2021-12-01 下午7.14.52.png](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/%E6%88%AA%E5%B1%8F2021-12-01_%E4%B8%8B%E5%8D%887.14.52.png)

现在启动样例项目进行测试（8080端口与8090端口）

重启并reload configuration  `sudo nginx -s reload`

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%208.png)

接下来更换自己的项目，复制后端工程两份配置为9091和9092，同时修改nginx.config

![集群的端口是9091和9092， nginx 的端口为9091](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%209.png)

集群的端口是9091和9092， nginx 的端口为9091

![截屏2021-12-01 下午8.03.34.png](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/%E6%88%AA%E5%B1%8F2021-12-01_%E4%B8%8B%E5%8D%888.03.34.png)

注意这里因为是listen的同一个端口会报错，所以把8787改为8788

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2010.png)

这样两个项目就跑完了。

发现会遇到TLS的问题, 因为9090端口和9080端口前面都是https，之前做了TLS认证，于是把 proxy_pass 从http://pancm 改为https://pancm; 

### 成功结果

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2011.png)

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2012.png)

![注意这里需要把https改为http](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2013.png)

注意这里需要把https改为http

前端之前的https端口调用都改为http 端口

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2014.png)

## 容器化部署

### 上课样例程序实现

[Sample application](https://docs.docker.com/get-started/02_our_app/)

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2015.png)

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2016.png)

# docker

### 1. intellj 打包spring boot

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2017.png)

**springboot打jar包报javax.websocket.server.ServerContainer not available** 

参考解决

[https://blog.csdn.net/qq_41754409/article/details/107997225](https://blog.csdn.net/qq_41754409/article/details/107997225)

原注解*`@SpringBootTest 改为`*

```python
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
```

Shutdown in program 

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2018.png)

![[https://blog.csdn.net/zhangningniwanle/article/details/80062266](https://blog.csdn.net/zhangningniwanle/article/details/80062266)](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2019.png)

[https://blog.csdn.net/zhangningniwanle/article/details/80062266](https://blog.csdn.net/zhangningniwanle/article/details/80062266)

![截屏2021-12-04 下午10.54.52.png](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/%E6%88%AA%E5%B1%8F2021-12-04_%E4%B8%8B%E5%8D%8810.54.52.png)

可以看见左侧打包成功了出现jar包

运行命令

```python
java -jar bookstore-0.0.1-SNAPSHOT.jar
```

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2020.png)

前端也可以用它来服务了。

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2021.png)

接下来获取各个依赖的版本

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2022.png)

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2023.png)

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2024.png)

安装对应版本的image

![截屏2021-12-05 下午12.41.35.png](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/%E6%88%AA%E5%B1%8F2021-12-05_%E4%B8%8B%E5%8D%8812.41.35.png)

编写Dockerfile, 在jar包同目录touch Douckerfile

```python
# 基础镜像使用java
FROM java:8
# 维护人，作者
MAINTAINER yixiao
# VOLUME 指定了临时文件目录为/tmp，
# 其效果是在主机 /var/lib/docker 目录下创建了一个临时文件，并链接到容器的/tmp
VOLUME /tmp
# 将jar包添加到容器中并更名为app.jar
ADD bookstore-0.0.1-SNAPSHOT.jar  eBookStore.jar
# 暴露运行端口,但是这个EXPOSE并不会实际起作用，而只是一个提示一个声明，比如这里EXPOSE 80，实际application.yaml中的server.port配置90，那么容器实际有用的端口是90，不是80
# 在docker run -p指定宿主机端口与容器端口映射时容器端口应指定90而不是80
EXPOSE 80
# 指定prod环境运行
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-Duser.timezone=GMT+8","-jar","eBookStore.jar"]
```

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2025.png)

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2026.png)

跑容器

```python
docker run -it --net=host --name ebookstor(base) 

docker run -it --net=host --name ebookstore -p 9091:9091 ebookstoree -p 9091:9091 ebookstore
```

```python
docker run --name mysql -p 3306:3306 -v mysql_data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=password  -d mysql:8.0.23
```

于是就在docker环境跑起来了，但是会报mysql等依赖连接 的错误

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2027.png)

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2028.png)

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2029.png)

docker exec -it mysql bash

mysql -uroot -ppassword

GRANT ALL PRIVILEGES ON *.* TO 'root'@'%'WITH GRANT OPTION;

FLUSH PRIVILEGES;

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2030.png)

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2031.png)

![Untitled](hw10%20Clustering%20&%20Docker%200999c22c4e59414586a29cfc9a30f8ed/Untitled%2032.png)

### session 和cookie 的区别

Cookies and Sessions are used to store information. Cookies are only stored on the client-side machine, while sessions get stored on the client as well as a server.

**Session**

A session creates a file in a temporary directory on the server where registered session variables and their values are stored. This data will be available to all pages on the site during that visit.

A session ends when the user closes the browser or after leaving the site, the server will terminate the session after a predetermined period of time, commonly 30 minutes duration.

**Cookies**

Cookies are text files stored on the client computer and they are kept of use tracking purpose. Server script sends a set of cookies to the browser. For example name, age, or identification number etc. The browser stores this information on a local machine for future use.

When next time browser sends any request to web server then it sends those cookies information to the server and server uses that information to identify the user.

## reference

[https://blog.csdn.net/jomexiaotao/article/details/83271458](https://blog.csdn.net/jomexiaotao/article/details/83271458)

[记一次docker部署springboot项目,mysql以及redis一样是docker中安装_zhs145612zhs的博客-CSDN博客](https://blog.csdn.net/zhs145612zhs/article/details/82225591?spm=1001.2101.3001.6650.3&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-3.no_search_link&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-3.no_search_link)

[在springboot项目中怎么打包成jar，同时怎么解析jar_my_ha_ha的博客-CSDN博客_springboot怎么打包成jar](https://blog.csdn.net/my_ha_ha/article/details/94183113)

[完整的docker+springboot+mysql部署_eriz程序之路-CSDN博客](https://blog.csdn.net/zhangcc233/article/details/96706157?spm=1001.2101.3001.6650.8&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7Edefault-8.fixedcolumn&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7Edefault-8.fixedcolumn)