package com.example.rootweblearning1.controller;

import com.example.rootweblearning1.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping(value = "/getAuthorByBookName",params = "bookName")
    public List<String> getAuthorByBookName(@RequestParam("bookName") String bookName){
        return bookService.getAuthorByBookName(bookName);
    }
}
