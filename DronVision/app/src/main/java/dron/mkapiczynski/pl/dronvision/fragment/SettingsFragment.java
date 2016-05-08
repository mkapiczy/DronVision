package dron.mkapiczynski.pl.dronvision.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import dron.mkapiczynski.pl.dronvision.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private Button saveSettingsButton;

    private SettingsFragmentActivityListener settingsActivityListener;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        saveSettingsButton = (Button) view.findViewById(R.id.buttonSaveSettting);

        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsActivityListener.onSaveSettingButtonClicked();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof SettingsFragmentActivityListener){
            settingsActivityListener = (SettingsFragmentActivityListener) context;
        } else{
            throw new ClassCastException( context.toString() + " musi implementować interfejs: " +
                    "SettingsFragment.SettingsFragmentActivityListener");
        }
    }

    public interface SettingsFragmentActivityListener {
        public void onSaveSettingButtonClicked();
    }

}
