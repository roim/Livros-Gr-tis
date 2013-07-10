package br.ita.roim.livros.android.extensions;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import br.ita.roim.livros.database.Book;

import java.util.*;

public class AlphabeticalAdapter extends ArrayAdapter<Book> implements SectionIndexer {
    private final HashMap<String, Integer> alphaIndexer;
    private final String[] sections;
    private final List<Book> mData;
    private final ArrayList<String> sectionList;

    public AlphabeticalAdapter(Context c, int resource, List<Book> data) {
        super(c, resource, data);

        mData = data;
        alphaIndexer = new HashMap<String, Integer>();

        for (int i = 0; i < data.size(); i++) {
            String s = data.get(i).toString().substring(0, 1).toUpperCase();
            if (!alphaIndexer.containsKey(s))
                alphaIndexer.put(s, i);
        }

        Set<String> sectionLetters = alphaIndexer.keySet();
        sectionList = new ArrayList<String>(sectionLetters);
        Collections.sort(sectionList);
        sections = new String[sectionList.size()];
        for (int i = 0; i < sectionList.size(); i++)
            sections[i] = sectionList.get(i);
    }

    public int getPositionForSection(int section) {
        if (section >= sections.length) return alphaIndexer.get(sections[section - 1]);

        return alphaIndexer.get(sections[section]);
    }

    public int getSectionForPosition(int position) {
        String firstLetter = mData.get(position).toString().substring(0, 1);
        return sectionList.indexOf(firstLetter);
    }

    public Object[] getSections() {
        return sections;
    }
}
