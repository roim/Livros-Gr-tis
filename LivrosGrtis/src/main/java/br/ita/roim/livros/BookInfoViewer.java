package br.ita.roim.livros;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import br.ita.roim.livros.database.Book;

public class BookInfoViewer extends FragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_info_viewer);

        Intent usedIntent = getIntent();
        Book book = (Book) usedIntent.getSerializableExtra("book");

        TextView book_title = (TextView) findViewById(R.id.book_title);
        TextView book_author = (TextView) findViewById(R.id.book_author);

        book_title.setText(book.getName());
        book_author.setText(book.getAuthor());
    }
}