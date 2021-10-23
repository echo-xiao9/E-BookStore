package com.example.rootweblearning1.serviceImpl;

import com.example.rootweblearning1.dao.BookDao;
import com.example.rootweblearning1.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(propagation= Propagation.REQUIRED)
public class BookServiceImpl implements BookService {
    @Autowired
    private BookDao bookDao;
    @Override
    public List<String> getAuthorByBookName(String bookName) {
        return bookDao.getAuthorByBookName(bookName);
    }
}