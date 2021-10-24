package com.example.rootweblearning1.repository;

import com.example.rootweblearning1.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,Integer> {
    @Query("select b from Book b where  b.name=:name")
    List<Book> getBookByName(@Param("name") String name);

}
