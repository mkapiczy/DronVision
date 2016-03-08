package dron.mkapiczynski.pl.dronvision.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import dron.mkapiczynski.pl.dronvision.R;


public class SimulationFragment extends Fragment {

    private ToggleButton simulationTurnOnOffButton;

    public SimulationFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simulation, container, false);
        simulationTurnOnOffButton = (ToggleButton) view.findViewById(R.id.simulationButton);
        simulationTurnOnOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (simulationTurnOnOffButton.isChecked()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Włączono symulację!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Zatrzymano symulację!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }

}
