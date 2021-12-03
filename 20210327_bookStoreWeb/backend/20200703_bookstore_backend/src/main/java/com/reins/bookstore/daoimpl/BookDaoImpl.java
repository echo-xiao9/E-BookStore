package com.reins.bookstore.daoimpl;

import com.reins.bookstore.dao.BookDao;
import com.reins.bookstore.entity.Book;
import com.reins.bookstore.entity.BookDescription;
import com.reins.bookstore.entity.BookType;
import com.reins.bookstore.repository.BookDescriptionRepository;
import com.reins.bookstore.repository.BookRepository;
import com.reins.bookstore.repository.BookTypeRepository;
import com.reins.bookstore.utils.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.JSON;

import java.util.*;
import java.util.List;

/**
 * @ClassName BookDaoImpl
 * @Description TODO
 * @Author thunderBoy
 * @Date 2019/11/5 20:20
 */
@Repository
public class BookDaoImpl implements BookDao {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookDescriptionRepository bookDescriptionRepository;
    @Autowired
    private BookTypeRepository bookTypeRepository;
    @Autowired
    RedisUtil redisUtil;

    private final static Logger log = LoggerFactory.getLogger(BookDaoImpl.class);

    @Override
    public Book findOne(Integer id){
        Book book=null;
        System.out.println("Searching Book: " + id + " in Redis");
        Object b = redisUtil.get("book" + id);
        if(b==null){
            System.out.println("Book: "+id+" is not in Redis");
            System.out.println("Searching Book: "+id+" in DB");
            book = bookRepository.getById(id);
            book.setDescription(bookDescriptionRepository.findById(id).get().getDescription());
            //debug
            System.out.println("findOne: description:");
            System.out.println(book.getDescription());

            redisUtil.set("book"+id,JSONArray.toJSON(book));
        }else{
            book = JSONArray.parseObject(b.toString(),Book.class);
            book.setDescription(bookDescriptionRepository.findById(id).get().getDescription());
            //debug
            System.out.println("findOne: description:");
            System.out.println(book.getDescription());
            System.out.println("Book: "+id+" is in Redis");
        }
        return book;
    }

    @Override
    public List<Book> getBooks(Integer page) {
        List<Book>bookList = new ArrayList<Book>();
        List<Integer> bookIdList =new ArrayList<Integer>();
        List<Book>targetList =  new ArrayList<Book>();
        Object bIdList=redisUtil.get("bookIdList");

        if(bIdList==null){
            // refresh the cache
            bookList=bookRepository.findAll();
            for (int i = 0; i < bookList.size(); i++) {
                Book b=bookList.get(i);
                b.setDescription(bookDescriptionRepository.findById(b.getBookId()).get().getDescription());
                //debug
                System.out.println("getBooks: description:");
                System.out.println(b.getDescription());

                redisUtil.set("book"+b.getBookId(),JSONArray.toJSON(b));
                bookIdList.add(b.getBookId());
                System.out.println("set Book in redis: "+bookList.get(i).getBookId());
            }
            redisUtil.set("bookIdList",JSONArray.toJSON(bookIdList));
            // prepare the result
            for(int i=4*page-3 ;i<bookList.size() && i<=4*page;i++){
                targetList.add(bookList.get(i));
            }
        }
        else{
            bookIdList=JSONArray.parseArray(bIdList.toString(),Integer.class);
            for(int i=4*page-3 ;i<bookIdList.size() && i<=4*page;i++){
                targetList.add(findOne(bookIdList.get(i)));
            }

        }
        return targetList;
    }

    public List<Book> getAllBooks(){
        List<Book> allBooks=new ArrayList<Book>();
        List<Integer> bookIdList =new ArrayList<Integer>();
        Object bIdList=redisUtil.get("bookIdList");
        if(bIdList==null){
            allBooks=bookRepository.findAll();
            for (int i = 0; i < allBooks.size(); i++) {
                Book b=allBooks.get(i);
                b.setDescription(bookDescriptionRepository.findById(b.getBookId()).get().getDescription());
                //debug
                System.out.println("getBooks: description:");
                System.out.println(b.getDescription());

                redisUtil.set("book"+b.getBookId(),JSONArray.toJSON(b));
                bookIdList.add(b.getBookId());
                System.out.println("set Book in redis: "+allBooks.get(i).getBookId());
            }
            redisUtil.set("bookIdList",JSONArray.toJSON(bookIdList));
        }
        else{
            bookIdList=JSONArray.parseArray(bIdList.toString(),Integer.class);
            for (int i = 0; i < bookIdList.size(); i++) {
                allBooks.add(findOne(bookIdList.get(i)));
            }
    }
        return allBooks;
}
    public void updateIdList(Boolean add, Integer id){
        Object bIdList=redisUtil.get("bookIdList");
        if(bIdList==null)return;
        List<Integer> bookIdList =new ArrayList<Integer>();
        bookIdList=JSONArray.parseArray(bIdList.toString(),Integer.class);
        if(add){
            bookIdList.add(id);
        }
        else{
            for (Integer item : bookIdList) {
                if(id.equals(item)) {
                    bookIdList.remove(item);
                }
            }
        }
        redisUtil.set("bookIdList",JSONArray.toJSON(bookIdList));
    }



    @Override
    public ArrayList getAdminBook() {
        List<Book> allBooks=getAllBooks();
        Iterator<Book> it = allBooks.iterator();
        ArrayList<JSONArray> booksJson = new ArrayList<JSONArray>();
        while (it.hasNext()) {
            Book book = (Book) it.next();
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(book.getBookId().toString());
            arrayList.add(book.getIsbn());
            arrayList.add(book.getName());
            arrayList.add(book.getType());
            arrayList.add(book.getAuthor());
            arrayList.add(book.getPrice().toString());
            arrayList.add(book.getInventory().toString());
            arrayList.add(book.getImage());
            arrayList.add(book.getDescription());
            booksJson.add((JSONArray) JSONArray.toJSON(arrayList));
        }
        String  booksString = JSON.toJSONString(booksJson, SerializerFeature.BrowserCompatible);
        return booksJson;
    }

    @Override
    public Book addBook( String isbn, String name, String type, String author, Integer price, String description, Integer inventory, String image) {
        Book book= new Book(isbn,name, type, author, price, description, inventory, image);
        Book b=bookRepository.save(book);
        b.setDescription(description);
        BookDescription bookDescription=new BookDescription(b.getBookId(),description);
        bookDescriptionRepository.save(bookDescription);
        //debug
        System.out.println("add book: description:");
        System.out.println(bookDescriptionRepository.findById(b.getBookId()));


        redisUtil.set("book"+b.getBookId(),JSONArray.toJSON(b));
        updateIdList(true,b.getBookId());
        return book;
    }

    @Override
    public Book deleteBook(Integer bookId) {
        Optional<Book> b=bookRepository.findById(bookId);
        bookRepository.deleteById(bookId);
        bookDescriptionRepository.deleteById(bookId);
            //isPresent方法用来检查Optional实例中是否包含值
            if (b.isPresent()) {
                //在Optional实例内调用get()返回已存在的值
                // update cache
                redisUtil.del("book"+b.get().getBookId());
                updateIdList(false,b.get().getBookId());
                return b.get();
            }else return null;
    }

    @Override
    public Book changeBook(Integer id,String isbn, String name, String type, String author, Integer price, String description, Integer inventory, String image) {
        Book b=this.findOne(id);
        BookDescription bd = bookDescriptionRepository.findById(id).get();
        b.setBookId(id);
        b.setIsbn(isbn);
        b.setName(name);
        b.setType(type);
        b.setAuthor(author);
        b.setPrice(price);
        b.setDescription(description);
        b.setInventory(inventory);
        bookRepository.save(b);
        bd.setDescription(description);
        bookDescriptionRepository.save(bd);
        // update cache
        redisUtil.set("book"+b.getBookId(),JSONArray.toJSON(b));
        return b;
    }



    @Override
    public List<Book> recommendBooks(String recommendType) {
        BookType bookType=bookTypeRepository.findByName(recommendType);
//        log.info(recommendType+" recommends :");

        Set<BookType> recommendList=bookType.recommends;
        log.info( recommendList.toString());
        System.out.println(recommendList.size());
        List<Book>bookList =new ArrayList<>();
        List<String> rec1Type=new ArrayList<>();
        for (BookType it :recommendList ) {
            String type=it.getName();
            rec1Type.add(type);
            bookList.addAll(bookRepository.findByType(type));
        }

        List<BookType> recmList2 =bookTypeRepository.findByRecommendsName2(recommendType);
//        log.info(recommendType+" recommends 2 depth :"+recmList2.toString());
        for (BookType it :recmList2 ) {
            String type=it.getName();
            if(!rec1Type.contains(type) && !type.equals(recommendType))
            bookList.addAll(bookRepository.findByType(type));
        }
        return bookList;
    }

    @Override
    public List<Book> booksOfType(String type) {
        BookType bookType=bookTypeRepository.findByName(type);
        return bookRepository.findByType(bookType.getName());
    }

//    @Override
//    public List<Book> searchBooks(String query) {
//        return bookRepository.findByName();
//    }


}
