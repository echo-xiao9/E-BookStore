# hw5: MicroServices & Security

请你在大二开发的E-Book系统的基础上，完成下列任务：

1. 下面两项任务你可以选择一项完成：
    - ① 开发一个微服务，输入为书名，输出为书的作者。将此微服务单独部署，并使用netflix-zuul进行路由，在你的E-Book系统中使用该服务来完成作者搜索功能。
    - ② 开发一个函数式服务，输入为订单中每种书的价格和数量，输出为订单的总价。将此函数式服务单独部署，并在你的E-Book系统中使用该服务来完成作者搜索功能。
2. 在你的工程中增加HTTPS通信功能，并且观察程序运行时有什么不同。请你编写文档，将程序运行时的过程截图，并解释为什么会出现和之前不同的差异。

–请将你的工程所有源代码和资源文件压缩后上传，请勿压缩编译后生成文件和依赖的第三方包

–关于第2点的文档一并压缩提交。

## 微服务

客户端添加eureka的dependency

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled.png)

这里考虑了一本书名可能有好几个版本的情况，因此测试了数据库放了两本同书名的书。

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%201.png)

访问独立端口

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%202.png)

注册到eureka上面

通过gateway访问

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%203.png)

其他服务（Book Store)也通过gateway进行了路由，相应的前端页面的url都要改了。

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%204.png)

# HTTPS

1.命令行生成keystore, 秘钥库指令是自己输入的，这里是testkey.

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%205.png)

查看证书

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%206.png)

这是访问http显示如下，需要改用https

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%207.png)

即使改到了https 也还不能访问（safari可以点击信任证书）

![64841635063724_.pic_hd.jpg](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/64841635063724_.pic_hd.jpg)

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%208.png)

1. 导出.cer 证书文件

```cpp
keytool -export -alias testKey -keystore test.keystore -rfc -file keystore.cer
```

-alias testKey, testKey是别名，这个和之前的设置证书的-alias要一样。

-keystore test.keystore 对哪个文件进行导出

-rfc 指定可以查看编码的方式输出 

-file keystore.cer 设置导出后的文件名

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%209.png)

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%2010.png)

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%2011.png)

参考：keyTool操作合集

[keytool常用操作_这是一个懒人的博客-CSDN博客](https://blog.csdn.net/qq_30062125/article/details/86717827?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1.no_search_link&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1.no_search_link)

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%2012.png)

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%2013.png)

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%2014.png)

![2221635065005_.pic_hd.jpg](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/2221635065005_.pic_hd.jpg)

## 增加HTTPS通信功能后，运行的区别

1. 输入域名之前是http,现在变成了https
    
    见下图，http升级为https的方法是部署SSL证书。而SSL证书可以正规的CA机构申请，也可以自己导出。本次作业使用jdk自带的工具生成本地的ssl证书
    
    HTTPS协议和HTTP协议的区别：
    
    - https协议需要到ca申请证书，一般免费证书很少，需要交费。
    - http是超文本传输协议，信息是明文传输，https 则是具有安全性的ssl加密传输协议。
    - http和https使用的是完全不同的连接方式用的端口也不一样,前者是80,后者是443。
    - http的连接很简单,是无状态的 。
    - HTTPS协议是由SSL+HTTP协议构建的可进行加密传输、身份认证的网络协议， 要比http协议安全。
2. 连接过程中会多一个SSL握手

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%2015.png)

类似与TCP的3次握手建立TCP连接，SSL握手是用于建立SSL（Security Socket Layer）层的连接。SSL握手的场景很多，比如最常见的HTTPS，在进行HTTPS的应用数据传递之前，需要建立SSL的连接.

上图就进行了一次SSL握手。

TLS/SSL中使用了非对称加密，对称加密以及HASH算法。握手过程的具体描述如下：

- 1）浏览器将自己支持的一套加密规则发送给网站。
- 2）网站从中选出一组加密算法与HASH算法，并将自己的身份信息以证书的形式发回给浏览器。证书里面包含了网站地址，加密公钥，以及证书的颁发机构等信息。
- 3）浏览器获得网站证书之后浏览器要做以下工作：a) 验证证书的合法性（颁发证书的机构是否合法，证书中包含的网站地址是否与正在访问的地址一致等），如果证书受信任，则浏览器栏里面会显示一个小锁头，否则会给出证书不受信的提示。b) 如果证书受信任，或者是用户接受了不受信的证书，浏览器会生成一串随机数的密码，并用证书中提供的公钥加密。c) 使用约定好的HASH算法计算握手消息，并使用生成的随机数对消息进行加密，最后将之前生成的所有信息发送给网站。
- 4）网站接收浏览器发来的数据之后要做以下的操作：a) 使用自己的私钥将信息解密取出密码，使用密码解密浏览器发来的握手消息，并验证HASH是否与浏览器发来的一致。b) 使用密码加密一段握手消息，发送给浏览器。
- 5）浏览器解密并计算握手消息的HASH，如果与服务端发来的HASH一致，此时握手过程结束，之后所有的通信数据将由之前浏览器生成的随机密码并利用对称加密算法进行加密。

这里浏览器与网站互相发送加密的握手消息并验证，目的是为了保证双方都获得了一致的密码，并且可以正常的加密解密数据，为后续真正数据的传输做一次测试。另外，HTTPS一般使用的加密与HASH算法如下：

- 非对称加密算法：RSA，DSA/DSS
- 对称加密算法：AES，RC4，3DES
- HASH算法：MD5，SHA1，SHA256

1. 需要增加证书认证

在你的初始尝试通过安全连接与webserver通信时，该服务器将以“证书”的形式向你的web浏览器提供一组凭证，作为该网站声称是**谁和什么网站**的证明。

HTTPS核心的一个部分是数据传输之前的握手，握手过程中确定了数据加密的密码。在握手过程中，网站会向浏览器发送SSL证书，SSL证书和我们日常用的身份证类似，是一个支持HTTPS网站的身份证明，SSL证书里面包含了网站的域名，证书有效期，证书的颁发机构以及用于加密传输密码的公钥等信息，由于公钥加密的密码只能被在申请证书时生成的私钥解密，因此浏览器在生成密码之前需要先核对当前访问的域名与证书上绑定的域名是否一致，同时还要对证书的颁发机构进行验证，如果验证失败浏览器会给出证书错误的提示。

在本次实现中，就导出了自己的证书，放入了本地的证书保存库，然后设置为始终信任，这样就使得证书被导入了浏览器（mac是这样）

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%2011.png)

![Untitled](hw5%20MicroServices%20&%20Security%20d62e3e91ef594473bf845a75a6989d29/Untitled%2014.png)

## reference

mac 导入证书方式

[mac下chrome导入burp证书](https://www.cnblogs.com/Hi-blog/p/How-To-Import-BurpSuite-Certificate-To-Chrome-On-MacOS.html)

HTTPS 与 SSL 证书概要

[HTTPS 与 SSL 证书概要 | 菜鸟教程](https://www.runoob.com/w3cnote/https-ssl-intro.html)