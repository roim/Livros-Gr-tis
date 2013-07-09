package br.ita.roim.livros;

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
    private int position;

    private ScrollView scrollView;
    private TextView textView;
    private List<Resource> res;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader);

        position = 0;

        InputStream epubInputStream;
        Book book = null;

        try {
            epubInputStream = getResources().openRawResource(R.raw.pg1661);
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
        switchChapter(0);

    }

    private void switchChapter(int dest) {
        position = dest;
        try {
            textView.setText(Html.fromHtml( new String( res.get(dest).getData() )));
        } catch (IOException e) {}
        scrollView.scrollTo(0,0);
    }

    public void voltaPagina(View view) {
        int pos = scrollView.getScrollY();
        scrollView.scrollBy(0, -scrollView.getHeight());
        if (scrollView.getScrollY() == pos && position != 0) {
            switchChapter(position-1);
        }
    }

    public void avancaPagina(View view) {
        int pos = scrollView.getScrollY();
        scrollView.scrollBy(0, scrollView.getHeight());
        if (scrollView.getScrollY() == pos && position + 1 < res.size()) {
            switchChapter(position+1);
        }
    }
}
