# README

## 全文搜索

使用Lucene

添加了IndexFiles 和SearchFiles，并且每次后端重启的时候就生成新的indexFile, 传入前端的query请求，得到List<Book>的结果

![Untitled](README%20336fc23bbb714e7ca3f91f6868ac1e2a/Untitled.png)

![Untitled](README%20336fc23bbb714e7ca3f91f6868ac1e2a/Untitled%201.png)

## 实现效果

![截屏2021-10-17 下午12.43.18.png](README%20336fc23bbb714e7ca3f91f6868ac1e2a/%E6%88%AA%E5%B1%8F2021-10-17_%E4%B8%8B%E5%8D%8812.43.18.png)

![截屏2021-10-17 下午12.43.25.png](README%20336fc23bbb714e7ca3f91f6868ac1e2a/%E6%88%AA%E5%B1%8F2021-10-17_%E4%B8%8B%E5%8D%8812.43.25.png)

## webservice

采用restfulWebService, 因为只有查询，所以就用getMapping

![image-20211022001422878](README .assets/image-20211022001422878.png)

这里用同一个URL，但是带不同的参数，实现了接口是对数据而不是操作定义的restful的web service

![image-20211022001521573](README .assets/image-20211022001521573.png)

针对不同的操作使用不同的方法（Post,get,...)œ