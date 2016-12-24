package com.example.nbdell.movieisland;

/**
 * Created by NB DELL on 11/30/2016.
 */

public class Review {
    String author;
    String content;

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        content = content;
    }
}
