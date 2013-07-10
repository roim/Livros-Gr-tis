package br.ita.roim.livros;

import java.io.InputStream;
import java.util.*;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import br.ita.roim.livros.android.extensions.AlphabeticalAdapter;
import br.ita.roim.livros.database.Book;
import br.ita.roim.livros.database.DownloadedBooksDatabase;
import br.ita.roim.livros.database.GutenbergPtParser;
import br.ita.roim.livros.reader.BookReader;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Load books
        // TODO preload books into a database
        loadBooks();

        // Initialize SQLite db
        DownloadedBooksDatabase.initialize(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getBooks(), getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 1) refresh();
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    private void refresh() {
        if (mSectionsPagerAdapter != null) mSectionsPagerAdapter.refresh();
    }
    
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private ArrayList<Book> mBooks;
        private DownloadedBooksSectionFragment downloadedFragment;

        public SectionsPagerAdapter(Context c, ArrayList<Book> books, FragmentManager fm) {
            super(fm);
            mContext = c;
            mBooks = books;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a BookListSectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment;
            if (position == 0) {
                fragment = new BookListSectionFragment(mContext, mBooks);
            } else if (position == 1) {
                downloadedFragment = new DownloadedBooksSectionFragment(mContext);
                fragment = downloadedFragment;
            } else {
                fragment = new DummySectionFragment(mContext);
            }
            Bundle args = new Bundle();
            args.putInt(BookListSectionFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        public void refresh() {
            if (downloadedFragment != null) downloadedFragment.refresh();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    public static class BookListSectionFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";
        private Context mContext;
        private ArrayList<Book> mBooks;

        public BookListSectionFragment(Context c, ArrayList<Book> books) {
            mContext = c;
            mBooks = books;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);

            ListView list = (ListView) rootView.findViewById(R.id.viewBooks);

            list.setAdapter(new AlphabeticalAdapter(mContext, R.layout.list_element, mBooks));

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent myIntent = new Intent(mContext, BookInfoViewer.class);
                    myIntent.putExtra("book", mBooks.get(position));
                    startActivity(myIntent);
                }
            });

            return rootView;
        }
    }

    public static class DownloadedBooksSectionFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";
        private Context mContext;
        private ArrayList<Book> mBooks;
        private View rootView;

        public DownloadedBooksSectionFragment(Context c) {
            mContext = c;
            mBooks = DownloadedBooksDatabase.getAllBooks();
        }

        public void refresh() {
            mBooks = DownloadedBooksDatabase.getAllBooks();

            ListView list = (ListView) rootView.findViewById(R.id.viewBooks);
            list.setAdapter(new AlphabeticalAdapter(mContext, R.layout.list_element, mBooks));
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);

            ListView list = (ListView) rootView.findViewById(R.id.viewBooks);
            list.setAdapter(new AlphabeticalAdapter(mContext, R.layout.list_element, mBooks));

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent myIntent = new Intent(mContext, BookReader.class);
                    myIntent.putExtra("book", mBooks.get(position).getID());
                    startActivity(myIntent);
                }
            });

            return rootView;
        }
    }

    public static class DummySectionFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";
        private Context mContext;

        public DummySectionFragment(Context c) {
            mContext = c;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
            return rootView;
        }
    }

    private GutenbergPtParser gpp;
    private void loadBooks() {
        InputStream is = this.getResources().openRawResource(R.raw.pt);
        gpp = new GutenbergPtParser(is);
        gpp.parse();
    }

    public ArrayList<Book> getBooks() {
        return gpp.getBooks();
    }
}
