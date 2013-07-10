package br.ita.roim.livros.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import br.ita.roim.livros.R;


public class DummySectionFragment extends Fragment {

    public static final String ARG_SECTION_NUMBER = "section_number";

    public DummySectionFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about, container, false);
        return rootView;
    }
}