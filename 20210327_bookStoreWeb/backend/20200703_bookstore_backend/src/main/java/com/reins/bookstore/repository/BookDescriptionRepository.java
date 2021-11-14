package com.reins.bookstore.repository;
import com.reins.bookstore.entity.BookDescription;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookDescriptionRepository  extends MongoRepository<BookDescription, Integer> {

}
