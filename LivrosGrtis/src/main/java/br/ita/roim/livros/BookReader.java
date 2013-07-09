package br.ita.roim.livros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class BookReader extends FragmentActivity {
    private String name;
    private int chapter;
    private int page;

    private ScrollView scrollView;
    private TextView textView;
    private List<Resource> res;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader);

        Intent usedIntent = getIntent();
        name = usedIntent.getStringExtra("book");

        InputStream epubInputStream;
        Book book = null;

        try {
            epubInputStream = openFileInput(name);
            book = new EpubReader().readEpub(epubInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        res = book.getContents();

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        textView = (TextView) findViewById(R.id.textView);

        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        int wanted_chapter = pref.getInt(name + "chapter", 0);
        int wanted_page = pref.getInt(name + "page", 0);

        navigateTo(wanted_chapter, wanted_page);
    }

    private void navigateTo(int dest_chapter) {
        chapter = dest_chapter;
        try {
            textView.setText(Html.fromHtml( new String( res.get(dest_chapter).getData() )));
        } catch (IOException e) {}
        scrollView.scrollTo(0,0);
        page = 0;
    }

    private void navigateTo(int dest_chapter, int dest_page) {
        navigateTo(dest_chapter);
        for (int i = 1; i < dest_page; i++) {
            avancaPagina(null);
        }
    }

    public void voltaPagina(View view) {
        int pos = scrollView.getScrollY();
        scrollView.scrollBy(0, -scrollView.getHeight());
        page--;

        if (scrollView.getScrollY() == pos && chapter != 0) {
            navigateTo(chapter-1);
        }
    }

    public void avancaPagina(View view) {
        int pos = scrollView.getScrollY();
        scrollView.scrollBy(0, scrollView.getHeight());
        page++;

        if (scrollView.getScrollY() == pos && chapter + 1 < res.size()) {
            navigateTo(chapter+1);
        }
    }

    @Override
    public void onStop() {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(name + "chapter", chapter);
        editor.putInt(name + "page", page);
        editor.commit();
    }
}
