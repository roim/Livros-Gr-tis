package br.ita.roim.livros;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.IOException;
import java.io.InputStream;

public class BookReader extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader);

        InputStream epubInputStream;
        Book book = null;

        try {
            epubInputStream = getResources().openRawResource(R.raw.pg1661);
            book = new EpubReader().readEpub(epubInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new BookPager(getSupportFragmentManager(), book);
        pager.setAdapter(mPagerAdapter);
    }

    class BookPager extends FragmentStatePagerAdapter {
        private Book book;

        public BookPager(FragmentManager fm, Book book) {
            super(fm);
            this.book = book;
        }

        @Override
        public Fragment getItem(int position) {
            return new PageFragment(book.getContents().get(position));
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    class PageFragment extends Fragment {
        private Resource res;

        public PageFragment(Resource res) {
            super();
            this.res = res;
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.book_page, container, false);

            TextView textView = (TextView) rootView.findViewById(R.id.textView);

            Spanned display = null;
            try {
                display = Html.fromHtml( new String(res.getData()) );
            } catch (IOException e) {}

            textView.setText(display);

            return rootView;
        }
    }
}
