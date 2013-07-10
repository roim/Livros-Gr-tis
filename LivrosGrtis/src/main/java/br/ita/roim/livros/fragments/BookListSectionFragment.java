package br.ita.roim.livros.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import br.ita.roim.livros.BookInfoViewer;
import br.ita.roim.livros.R;
import br.ita.roim.livros.android.extensions.AlphabeticalAdapter;
import br.ita.roim.livros.database.Book;

import java.util.ArrayList;

public class BookListSectionFragment extends Fragment {

    public static final String ARG_SECTION_NUMBER = "section_number";
    private final Context mContext;
    private final ArrayList<Book> mBooks;

    public BookListSectionFragment(Context c, ArrayList<Book> books) {
        mContext = c;
        mBooks = books;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.catalog, container, false);

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
