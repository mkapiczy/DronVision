package dron.mkapiczynski.pl.dronvision.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dron.mkapiczynski.pl.dronvision.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutAuthorFragment extends Fragment {


    public AboutAuthorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_author, container, false);
    }

}
