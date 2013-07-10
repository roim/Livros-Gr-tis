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

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
