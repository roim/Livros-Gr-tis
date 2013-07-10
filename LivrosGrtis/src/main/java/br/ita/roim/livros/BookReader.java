package br.ita.roim.livros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ScrollView;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity to read a book in epub format. It reads the book passed as input
 * through the "book" parameter of the intent.
 */
public class BookReader extends FragmentActivity {
    private int id;
    private int chapter;
    private int page;
    private int wanted_chapter;
    private int wanted_page;

    private String FILE_NAME;
    private ScrollView scrollView;
    private WebView webView;
    private List<Resource> res;
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader);

        Intent usedIntent = getIntent();
        id = usedIntent.getIntExtra("book", -1);

        if (id == -1) {
            Log.e("reader", "didn't receive ID");
        }

        InputStream epubInputStream;
        Book book = null;
        FILE_NAME = "pg" + Integer.toString(id) + ".epub";

        try {
            epubInputStream = openFileInput(FILE_NAME);
            book = new EpubReader().readEpub(epubInputStream);
        } catch (IOException e) {
            Log.e("reader", e.getMessage());
        }

        res = book.getContents();

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        // Hack to disable scrolling.
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        webView = (WebView) findViewById(R.id.textView);

        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        wanted_chapter = pref.getInt(FILE_NAME + "chapter", 0);
        wanted_page = pref.getInt(FILE_NAME + "page", 0);

        navigateTo(wanted_chapter, 0);

        Callable<Void> navigator = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Scrolling doesn't work while the scrollView is being prepared
                //   but apparently we don't know when it's gonna be ready for scrolling!
                //   Well, one day it will be, so let's keep trying until it works.
                while (true) {
                    int pos = scrollView.getScrollY();
                    scrollView.scrollBy(0, 1);
                    if (pos != scrollView.getScrollY()) {
                        scrollView.scrollBy(0, -1);
                        break;
                    }
                    scrollView.scrollBy(0, -1);
                }

                navigateTo(wanted_chapter, wanted_page);
                return null;
            }
        };
        executor.submit(navigator);
    }

    private void navigateTo(int dest_chapter) {
        chapter = dest_chapter;
        try {
            webView.loadData(new String(res.get(dest_chapter).getData()), "text/html; charset=UTF-8", null);
        } catch (IOException e) {
            Log.e("reader", e.getMessage());
        }
        scrollView.scrollTo(0, 0);
        page = 0;
    }

    private void navigateTo(int dest_chapter, int dest_page) {
        navigateTo(dest_chapter);
        for (int i = 0; i < dest_page; i++) {
            advancePage(null);
        }
    }

    public void backPage(View view) {
        int pos = scrollView.getScrollY();
        scrollView.scrollBy(0, -scrollView.getHeight() + 10);
        page--;

        if (scrollView.getScrollY() == pos && chapter != 0) {
            navigateTo(chapter - 1);
        }
    }

    public void advancePage(View view) {
        int pos = scrollView.getScrollY();
        scrollView.scrollBy(0, scrollView.getHeight() - 10);
        page++;

        if (scrollView.getScrollY() == pos && chapter + 1 < res.size()) {
            navigateTo(chapter + 1);
        }
    }

    @Override
    public void onStop() {
        persistState();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void persistState() {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(FILE_NAME + "chapter", chapter);
        editor.putInt(FILE_NAME + "page", page);
        editor.commit();
    }
}
