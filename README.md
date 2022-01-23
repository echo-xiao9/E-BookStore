# E-BookStore

An online book store

### Project introduction 

1. User management: 
   
   - you can see this function after logging in as administrator; users are divided into two roles: customers and administrators
   
   - The administrator can disable/unblock users, and the disabled users will not be able to log into the system. 
   
2. User Login and Registration
   * User login requires user name and password, if you don't enter them, you will be prompted to enter them when you click the login button.
   * Disabled users cannot log in to the system and the user will be prompted "Your account has been disabled". 
   * The user name is used to identify whether they are an administrator or a customer, and the interface has differences for different roles.
   * New users need to fill in username, password, duplicate password and email when registering.
   * It is necessary to check whether the user name is duplicated, whether the password entered twice is the same, and whether the email address meets the format requirements.

3. Book management: 

   After logging in as administrator, you can see these function:

   1. Administrator can browse the existing books in the database and display them in the form of list, including title, author, cover, ISBN number and stock quantity.
   2. The search function is provided at the top of the list, and the administrator can filter the books he/she wants to find by the book name.
   3. Administrators can modify various attributes of each book mentioned above in the list, including title, author, cover, ISBN number and stock quantity.
   4. The administrator can delete old books and add new books. 4.

4. Browse Books: This part can reuse the functions in "Book Management".
   * Both customers and administrators can browse existing books in the database and display them in a list, including title, author, cover, ISBN number and stock quantity.
   * Provide search function, users can filter the books they want to find by book name.
   * When a book is selected, the details of the book are retrieved and displayed via Ajax. 

5. Buy Books
   * When users browse books, they can choose to put a book into the shopping cart.
   * Users can browse the shopping cart and see all the books they have added to the cart.
   * After clicking on a book in the shopping cart, the cart is emptied and the book inventory is reduced accordingly.
   * After purchasing a book, an order is generated and displayed to the user and the order is stored in the database.

6. Order Management
   * Customers can view all their orders and can use the search function for filtering, specifically by time range or book name.
   * Administrators can view all orders in the system and can use the search function to achieve over
     The administrator can view all orders in the system and use the search function to filter them by time range or book name. 

7. Statistics
   * Administrators can count the sales of various books within a specified time frame, rank them according to sales volume
     The administrator can count the sales of various books within a specified time frame, sort them by sales volume, and form a hot list, which is presented in the form of a chart or table.
     
   * Administrators can count the cumulative consumption of each user within a specified time frame, sort the books by purchase, form a consumption list, and present it in the form of a chart or a table.

   * Customers can count their book purchases within a specified time frame, including how many copies of each book they have purchased, the total number of books purchased and the total amount.

     

### Technology Stack

**Frontend**: React </br>
**Backend**: SpringBoot、Maven、Docker </br>

**Database**: mysql, mongodb, neo4j, influxdb</br>

**middleware**: Activemq、WebSocket、Lucene、SOAP、WSDL、Eureka、Nginx、Hadoop</br>

### Technical Highlights

#### [Activemq](https://github.com/echo-xiao9/E-BookStore/tree/main/20210327_bookStoreWeb/hw/hw1)

Use Activemq to implement an order placement function message queue. When a user clicks to place an order, the back-end Controller first forwards the user request to the consumer using the producer and quickly returns to the user that the order is being executed. The back-end consumer will complete the transaction during the idle period. This effectively improves the efficiency and throughput of placing orders.

#### [WebSocket](https://github.com/echo-xiao9/E-BookStore/tree/main/20210327_bookStoreWeb/hw/hw2)

Use WebSocket to implement an online chat room where users can engage in real-time group chats.

#### [Transaction](https://github.com/echo-xiao9/E-BookStore/tree/main/20210327_bookStoreWeb/hw/hw2)

Utilize additional **SpringBoot annotation** methods to add transaction control features to the order placement service to ensure consistency and integrity of order placement actions and the database.

#### [MultiThreading](https://github.com/WilliamX1/bookstore/blob/main/hw/hw3/README.md)

The interface-oriented programming concept is used to count the number of user accesses to the interface. And use **AtomicCounter** for count atomicity, to ensure data safety and reliability when multi-threaded access.

#### [Redis](https://github.com/echo-xiao9/E-BookStore/tree/main/20210327_bookStoreWeb/hw/hw3)

Use Redis database as a cache for book information, which greatly reduces the pressure on MySQL database and improves access speed and stability.

#### [Lucene](https://github.com/echo-xiao9/E-BookStore/tree/main/20210327_bookStoreWeb/hw/hw4)

Use Lucene full-text search engine to index the text of book profiles, so that users can quickly and efficiently perform full-text searches.

#### [Web Service](https://github.com/echo-xiao9/E-BookStore/tree/main/20210327_bookStoreWeb/hw/hw4)

Full-text search of books is developed and deployed as a Web Service, accessed by the same URL using different parameters. The interface is implemented as a restful web service defined for data rather than operations.

#### [microservice](https://github.com/echo-xiao9/E-BookStore/tree/main/20210327_bookStoreWeb/hw/hw5)

Build a SpringBoot microservice architecture using Eureka Server and Eureka Client, decoupled by decomposing functionality into discrete services, and load balanced using Netflix-Zuul for routing.

#### [MongoDB](https://github.com/echo-xiao9/E-BookStore/tree/main/20210327_bookStoreWeb/hw/hw8)

Convert book image information into base64 strings to MongoDB database, increasing project portability.

#### [Neo4j](https://github.com/echo-xiao9/E-BookStore/tree/main/20210327_bookStoreWeb/hw/hw8)

Add multiple tags to a book, using the Neo4j database to store tag types and interconnections, allowing users to fuzzy search for books containing adjacent tags based on tags.

#### [InfluxDB](https://github.com/echo-xiao9/E-BookStore/tree/main/20210327_bookStoreWeb/hw/hw9)

Real-time monitoring of the running status of each CPU core, each disk, etc. of a computer.

#### [Nginx](https://github.com/echo-xiao9/E-BookStore/tree/main/20210327_bookStoreWeb/hw/hw10)

Load balancing to handle high concurrency.

#### [Docker](https://github.com/echo-xiao9/E-BookStore/tree/main/20210327_bookStoreWeb/hw/hw10)

Containerized deployment, i.e. load balancer nginx, cache redis, and service cluster tomcat are deployed in docker containers. 

#### [Hadoop MapReduce](https://github.com/echo-xiao9/E-BookStore/tree/main/20210327_bookStoreWeb/hw/hw11)

Use the hadoop MapReduce framework to count the number of occurrences of each of the above keywords in the book introduction.
 Translated with www.DeepL.com/Translator (free version)
