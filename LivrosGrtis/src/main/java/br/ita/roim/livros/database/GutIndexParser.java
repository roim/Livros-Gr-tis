package br.ita.roim.livros.database;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GutIndexParser {

    private final ArrayList<Book> books = new ArrayList<Book>();
    private InputStream gutIndexStream;

    public ArrayList<Book> getBooks() {
        return books;
    }

    public GutIndexParser(InputStream gutIndexStream) {
        this.gutIndexStream = gutIndexStream;
    }

    public void parse() {
        BufferedReader br = new BufferedReader(new InputStreamReader(gutIndexStream));
        String readLine = null;

        try {
            // Jump the filler text at start of file
            while ((readLine = br.readLine()) != null) if (readLine.startsWith("~")) break;

            String title = null;
            String author = null;
            String language = null;
            int id = -1;
            boolean skipNextLines = false;
            int i = 0;
            while ((readLine = br.readLine()) != null) {
                // If char 77 is a digit, then this line begins a new book.
                // In this case, the first thing we do is store the previous book we read, if any.
                // Then we clear the variables, store the book id and proceed normally.
                //
                if (readLine.length() >= 78 && Character.isDigit(readLine.charAt(77))) {
                    if (title != null) {
                        // We found a book! yay!
                        title = title.trim().replaceAll(" +", " ");

                        books.add(new Book(title, "", language, id));

                        ++i;
                        if (i%1000 == 0) Log.d("Error", new Integer(i).toString());
                    }

                    title = readLine.replaceFirst("(.*) ([0-9]+)[abcdeABCDE]* *$", "$1");
                    id = Integer.parseInt(readLine.replaceFirst("(.*) ([0-9]+)[abcdeABCDE]* *$", "$2"));
                    language = "English"; // Default language
                    skipNextLines = false;
                    continue;
                }

                if (skipNextLines) continue;

                if (readLine.isEmpty()) {
                    skipNextLines = true;
                    continue;
                }
                else if (readLine.matches(" \\[Language: (.*)\\]")) {
                    language = readLine.replaceFirst(" \\[Language: (.*)\\]", "$1");
                    skipNextLines = true;
                }
                else if (readLine.startsWith(".(") || readLine.startsWith(" [") || readLine.startsWith("  ")) {
                    continue;
                }
                else {
                    title += readLine;
                }

            }

            // Close the InputStream and BufferedReader
            gutIndexStream.close();
            br.close();

        } catch (Exception e) {
            Log.d("", e.getMessage());
        }
    }
}
