package com.example.rootweblearning1.dao;

import java.util.List;

public interface BookDao {

    List<String> getAuthorByBookName(String bookName);
}
