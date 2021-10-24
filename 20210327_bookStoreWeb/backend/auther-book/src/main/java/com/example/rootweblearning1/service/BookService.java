package com.example.rootweblearning1.service;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
public interface BookService {
    public List<String> getAuthorByBookName( String bookName);
}
