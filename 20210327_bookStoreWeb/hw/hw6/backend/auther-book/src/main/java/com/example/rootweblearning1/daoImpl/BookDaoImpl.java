package com.example.rootweblearning1.daoImpl;

import com.example.rootweblearning1.dao.BookDao;
import com.example.rootweblearning1.entity.Book;
import com.example.rootweblearning1.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BookDaoImpl implements BookDao {
    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<String> getAuthorByBookName(String bookName) {
        List<Book> bookList=bookRepository.getBookByName(bookName);
        // consider if one bookname have many books.
        List<String > strList=new ArrayList<String>();
        if(bookList==null){
            strList.add("There is no such book.");
            return strList;
        }
        for(Book b:bookList){
            strList.add(b.getAuthor());
        }
       return strList;
    }
}
