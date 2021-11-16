package com.reins.bookstore.repository;


import java.util.List;

import com.reins.bookstore.entity.BookType;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface BookTypeRepository extends Neo4jRepository<BookType, Long> {

    BookType findByName(String name);
    List<BookType> findByRecommendsName(String name);

    //Find double recommendations
    @Query("Match (n:BookType {name:$name})-[:RECOMMEND]->()-[:RECOMMEND]->(m:BookType) RETURN m")
    List<BookType> findByRecommendsName2(String name);
}

