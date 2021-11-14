package com.reins.bookstore.entity;

import javax.persistence.Id;

public class BookDescription {
    @Id
    private int id;
    private String description;

    public BookDescription(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
