# hw7: MySQL Backup, Recovery & Partition

1. 请你详细描述如何通过全量备份和增量备份来实现系统状态恢复。(2分）
2. 请你根据MySQL缓存的工作原理，描述预取机制的优点。(1分)
3. 请你按照你的理解，阐述Partition机制有什么好处？如果数据文件在一台机器上有足够的存储空间存储，是否还需要进行Partition？(2分)

# Q1

**概念介绍**

全量备份 full backup ：MySQL server在给定时间点的**所有**data. 于是通过它可以实现给定时间点的恢复。

实现：MySQL对全量备份有多种方式。

优势：备份的数据最全面且最完整，当发生数据丢失灾难时只要用一盘磁带（即灾难发生前一天的备份磁带）就可以恢复全部的数据, 恢复数据的速度快。

缺点：需要很大的存储空间，带宽，完成备份过程相对耗时。

增量备份 incremental backup：在一个时间段（两个时间点之间）增量式的备份所有的对数据的改变。

实现：通过 server的binary log 来记录数据改变，也叫做point-in-time recovery 因为通过全量+增量可以实现任意时间点的恢复。

优势：备份速度快，没有重复的备份数据，节省了磁带空间，缩短了备份时间。

缺点：恢复需要把多个备份集合的数据拼凑在一起，如果一个备份集crash，完全恢复的可能性较小。

系统恢复通过结合 全量备份和增量备份来实现：

1. 比如现在11月3日12：05 数据库崩溃了需要恢复到之前的状态。
2. 查找所有的全量备份，看到了最新的11月1日 00：00做了全量备份，就先恢复到这个状态
3. 找到11月1日00：00-11月2日：00 和11月2日00：00-11月3日00：00 的已经写完成了bin-log文件（这两个文件不会修改了），按照顺序恢复
4. 再找到现在正在写的（11月3日00：00-12：05）bin-log 文件进行恢复。

这样通过全量备份和增量备份就可以恢复到任意的时间点了。然而需要注意的是，无论是全量还是增量备份，都需要存到非这个数据库的机器/硬盘上 ， 否则可能一起crash, 这样就没有意义了。

# Q2

好处：总结自讲课和板书

目的：通过预读操作控制InnoDB的预取量。

背景：磁盘数据被optimize之后，变成连续存放，内部碎片减少。当系统有未使用的I/O容量时，更多的预读可以提高查询的性能。比如磁盘转一圈就可以把整一个表的数据或者使用的数据的周围数据都读出来。所以预读可以提高性能。但是过多的预读可能会导致高负载系统的性能周期性下降。

对于线性预读，基于数据locality，因为是在一个表里做操作，临近数据被取用或者整个表的数据被频繁使用是很可能的。

对于随机预读， 上课提到的背景是：但是如果是交易性的操作，只要读一个订单，就应该使用随机处理（因为不需要周围其他的order).有一张order表，关联了orderItem, orderItem 又关联了book，需要一定的预测机制来得到对应的orderItem 和book。在这里，预测算法就显得非常重要了，预测的越准，性能越好。

技术：总结自mysql官方文档和PPT

预读请求是指预取缓冲池中的多个页面的异步请求，预计这些页面很快就会被使用。请求在一个区段中引入所有页面。InnoDB使用两种预读算法来提高I/O性能: 

**线性预读**是一种基于按顺序访问的缓冲池中的页面来预测可能很快需要哪些页面的技术。通过配置参数innodb_read_ahead_threshold，可以通过调整触发异步读请求所需的顺序页访问次数来控制InnoDB执行预读操作的时间。在此之前，InnoDB只会在读取当前extent的最后一页时，计算是否对整个下一个extent发出异步预取请求。

**随机预读**是一种技术，它可以根据缓冲池中已经存在的页面预测何时可能需要页面，而不管这些页面的读取顺序如何。如果在缓冲池中发现同一个区段的13个连续页面，InnoDB会异步发出一个请求来预取该区段的剩余页面。

![Untitled](hw7%20MySQL%20Backup,%20Recovery%20&%20Partition%2003499c86a9244f1fbf9ac4215200e682/Untitled.png)

![Untitled](hw7%20MySQL%20Backup,%20Recovery%20&%20Partition%2003499c86a9244f1fbf9ac4215200e682/Untitled%201.png)

# Q3

**分区的好处：**

（1）可伸缩性：

可能表太大了，单个文件或者硬盘放不下。文件一次性读进来占用很多时间和内存。

将数据分区分在不同磁盘，可以解决单磁盘容量瓶颈问题，存储更多的数据，也能解决单磁盘的IO瓶颈问题。
（2）提升数据库的性能：

减少数据库检索时需要遍历的数据量，在查询时只需要在数据对应的分区进行查询。
避免Innodb的单个索引的互斥访问限制
对于聚合函数，例如sum()和count()，可以在每个分区进行并行处理，最终只需要统计所有分区得到的结果
（3）方便对数据进行运维管理：

方便管理，对于失去保存意义的数据，通过删除对应的分区，达到快速删除的作用。比如删除某一时间的历史数据，直接执行truncate，或者直接drop整个分区，这比detele删除效率更高；
在某些场景下，单个分区表的备份很恢复会更有效率。

分区一个最大的优点就是可以非常高效的进行历史数据的清理。也容易把整个分区比较容易增删改查，比如对所有同一类数据操作，如果是删除，这些数据在一个分区里，直接删除掉这个分区就可以了。查询也可以只查询这一个分区。甚至sql语句里面可以指定在哪个分区里面查。

如果在一台机器上有足够的存储空间存储，在表很大，经常需要对一类数据进行处理的时候，**仍然需要分区。**

上面提到的(2)(3）两点仍然起作用，分区进行增删改查，使得顺序读写的概率增加，从而性能得到了提升

# reference

[mysql自动分区partition_ 深入理解MySQL分区（Partition）_fire life的博客-CSDN博客](https://blog.csdn.net/weixin_42624889/article/details/113633336?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_title~default-5.essearch_pc_relevant&spm=1001.2101.3001.4242.4)

[MySQL数据库：分区Partition_张维鹏的博客-CSDN博客_mysql数据库分区](https://blog.csdn.net/a745233700/article/details/85250173?spm=1001.2101.3001.6650.6&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EOPENSEARCH%7Edefault-6.essearch_pc_relevant&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EOPENSEARCH%7Edefault-6.essearch_pc_relevant)

[mysql 预读_MySQL InnoDB缓冲池预读_weixin_27038261的博客-CSDN博客](https://blog.csdn.net/weixin_27038261/article/details/113190221)

[关于MySQL buffer pool的预读机制](https://www.cnblogs.com/geaozhang/p/7397699.html)

[MySQL :: MySQL 5.7 Reference Manual :: 14.8.3.4 Configuring InnoDB Buffer Pool Prefetching (Read-Ahead)](https://dev.mysql.com/doc/refman/5.7/en/innodb-performance-read_ahead.html)

官方文档对于**Configuring InnoDB Buffer Pool Prefetching (Read-Ahead)的介绍如下**

A [read-ahead](https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_read_ahead) request is an I/O request to prefetch multiple pages in the [buffer pool](https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_buffer_pool) asynchronously, in anticipation that these pages are needed soon. The requests bring in all the pages in one [extent](https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_extent). `InnoDB` uses two read-ahead algorithms to improve I/O performance:

**Linear** read-ahead is a technique that predicts what pages might be needed soon based on pages in the buffer pool being accessed sequentially. You control when `InnoDB` performs a read-ahead operation by adjusting the number of sequential page accesses required to trigger an asynchronous read request, using the configuration parameter `[innodb_read_ahead_threshold](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_read_ahead_threshold)`. Before this parameter was added, `InnoDB` would only calculate whether to issue an asynchronous prefetch request for the entire next extent when it read the last page of the current extent.

The configuration parameter `[innodb_read_ahead_threshold](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_read_ahead_threshold)` controls how sensitive `InnoDB` is in detecting patterns of sequential page access. If the number of pages read sequentially from an extent is greater than or equal to `[innodb_read_ahead_threshold](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_read_ahead_threshold)`, `InnoDB` initiates an asynchronous read-ahead operation of the entire following extent. `[innodb_read_ahead_threshold](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_read_ahead_threshold)` can be set to any value from 0-64. The default value is 56. The higher the value, the more strict the access pattern check. For example, if you set the value to 48, `InnoDB` triggers a linear read-ahead request only when 48 pages in the current extent have been accessed sequentially. If the value is 8, `InnoDB` triggers an asynchronous read-ahead even if as few as 8 pages in the extent are accessed sequentially. You can set the value of this parameter in the MySQL [configuration file](https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_configuration_file), or change it dynamically with the `[SET GLOBAL](https://dev.mysql.com/doc/refman/5.7/en/set-variable.html)` statement, which requires privileges sufficient to set global system variables. See [Section 5.1.8.1, “System Variable Privileges”](https://dev.mysql.com/doc/refman/5.7/en/system-variable-privileges.html).

**Random** read-ahead is a technique that predicts when pages might be needed soon based on pages already in the buffer pool, regardless of the order in which those pages were read. If 13 consecutive pages from the same extent are found in the buffer pool, `InnoDB` asynchronously issues a request to prefetch the remaining pages of the extent. To enable this feature, set the configuration variable `[innodb_random_read_ahead](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_random_read_ahead)` to `ON`.

The `SHOW ENGINE INNODB STATUS` command displays statistics to help you evaluate the effectiveness of the read-ahead algorithm. Statistics include counter information for the following global status variables:

- `[Innodb_buffer_pool_read_ahead](https://dev.mysql.com/doc/refman/5.7/en/server-status-variables.html#statvar_Innodb_buffer_pool_read_ahead)`
- `[Innodb_buffer_pool_read_ahead_evicted](https://dev.mysql.com/doc/refman/5.7/en/server-status-variables.html#statvar_Innodb_buffer_pool_read_ahead_evicted)`
- `[Innodb_buffer_pool_read_ahead_rnd](https://dev.mysql.com/doc/refman/5.7/en/server-status-variables.html#statvar_Innodb_buffer_pool_read_ahead_rnd)`

This information can be useful when fine-tuning the `[innodb_random_read_ahead](https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html#sysvar_innodb_random_read_ahead)` setting.

For more information about I/O performance, see [Section 8.5.8, “Optimizing InnoDB Disk I/O”](https://dev.mysql.com/doc/refman/5.7/en/optimizing-innodb-diskio.html) and [Section 8.12.2, “Optimizing Disk I/O”](https://dev.mysql.com/doc/refman/5.7/en/disk-issues.html).

[MySQL :: MySQL 5.7 Reference Manual :: 8.5.9 Optimizing InnoDB Configuration Variables](https://dev.mysql.com/doc/refman/5.7/en/optimizing-innodb-configuration-variables.html)

![Untitled](hw7%20MySQL%20Backup,%20Recovery%20&%20Partition%2003499c86a9244f1fbf9ac4215200e682/Untitled%202.png)

👆提到