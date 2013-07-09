package br.ita.roim.livros.database;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GutenbergPtParser {
    private final ArrayList<Book> books = new ArrayList<Book>();
    private InputStream gutHttpStream;

    public ArrayList<Book> getBooks() {
        return books;
    }

    public GutenbergPtParser(InputStream gutHttpStream) {
        this.gutHttpStream = gutHttpStream;
    }

    public void parse() {
        BufferedReader br = new BufferedReader(new InputStreamReader(gutHttpStream));
        String readLine = null;

        try {
            String title = null;
            String author = null;
            String language = "Portuguese";
            int id = -1;

            Pattern bookAuthor = Pattern.compile("<h2><a .*></a><a .*>(.*)</a></h2>");
            Pattern bookTitle = Pattern.compile("  <li class=\"pgdbetext\"><a href=\"/ebooks/([0-9]+)[abcdeABCDE]*\">(.*)</a>.*");

            while ((readLine = br.readLine()) != null) {
                Matcher matchAuthor = bookAuthor.matcher(readLine);
                if (matchAuthor.matches()) {
                    author = matchAuthor.group(1);
                    continue;
                }

                Matcher matchTitle = bookTitle.matcher(readLine);
                if (matchTitle.matches()) {
                    id = Integer.parseInt(matchTitle.group(1));
                    title = matchTitle.group(2).replaceAll("<br>", ": ");

                    if (title.charAt(0) == 'Á') title = title.replaceFirst("Á", "A");

                    books.add(new Book(title, author, language, id));
                }
            }

            // Close the InputStream and BufferedReader
            gutHttpStream.close();
            br.close();

        } catch (IOException e) {
            for (int i = 0; i < 20; ++i ) Log.d("ERROR", e.getMessage());
        }

        Collections.sort(books, new Comparator<Book>() {
            @Override
            public int compare(Book lhs, Book rhs) {
                return lhs.toString().compareTo(rhs.toString());
            }
        });
    }
}
