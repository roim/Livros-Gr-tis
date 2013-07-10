package br.ita.roim.livros.database;

import java.io.Serializable;

public class Book implements Serializable {
    private String name;
    private String author;
    private String language;
    private int ID;

    public Book(String name, String author, String language, int ID) {
        this.setName(name);
        this.setAuthor(author);
        this.setLanguage(language);
        this.setID(ID);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    void setAuthor(String author) {
        this.author = author;
    }

    public String getLanguage() {
        return language;
    }

    void setLanguage(String language) {
        this.language = language;
    }

    public int getID() {
        return ID;
    }

    void setID(int ID) {
        this.ID = ID;
    }
}
