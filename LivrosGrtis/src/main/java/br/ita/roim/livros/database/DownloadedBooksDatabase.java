package br.ita.roim.livros.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DownloadedBooksDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DOWNLOADED_TABLE_NAME = "DOWNLOADED";
    private static final String DOWNLOADED_TABLE_CREATE = "CREATE TABLE " + DOWNLOADED_TABLE_NAME + " (" +
            "TITLE    TEXT, " +
            "ID       INTEGER PRIMARY KEY, " +
            "LANGUAGE TEXT, " +
            "AUTHOR   TEXT);";

    private static DownloadedBooksDatabase singleton;

    public static DownloadedBooksDatabase instance() {
        return singleton;
    }

    public static void initialize(Context c) {
        singleton = new DownloadedBooksDatabase(c);
    }

    public static void addBook(Book book) {
        SQLiteDatabase db = singleton.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("TITLE", book.getName());
        values.put("ID", book.getID());
        values.put("LANGUAGE", book.getLanguage());
        values.put("AUTHOR", book.getAuthor());

        db.insert("DOWNLOADED", null, values);
        db.close();
    }

    public static Book getBook(int id) {
        SQLiteDatabase db = singleton.getReadableDatabase();

        Cursor cursor = db.query("DOWNLOADED",
                new String[]{"TITLE", "ID", "LANGUAGE", "AUTHOR"},
                "ID=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null) cursor.moveToFirst();

        try {
            if (cursor == null || cursor.getString(1) == null || cursor.getString(1).isEmpty()) return null;
        } catch (CursorIndexOutOfBoundsException cioobe) {
            return null;
        }

        return new Book(cursor.getString(0), cursor.getString(3), cursor.getString(2), Integer.parseInt(cursor.getString(1)));
    }

    public static ArrayList<Book> getAllBooks() {
        ArrayList<Book> books = new ArrayList<Book>();

        String selectQuery = "SELECT * FROM DOWNLOADED";

        SQLiteDatabase db = singleton.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) do {
            books.add(new Book(cursor.getString(0), cursor.getString(3), cursor.getString(2), Integer.parseInt(cursor.getString(1))));
        } while (cursor.moveToNext());

        Collections.sort(books, new Comparator<Book>() {
            @Override
            public int compare(Book lhs, Book rhs) {
                return lhs.toString().compareTo(rhs.toString());
            }
        });

        return books;
    }

    private DownloadedBooksDatabase(Context context) {
        super(context, "LIVROSGRTS", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DOWNLOADED_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DOWNLOADED_TABLE_NAME);
        onCreate(db);
    }
}
