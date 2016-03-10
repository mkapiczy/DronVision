package dron.mkapiczynski.pl.dronvision.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import dron.mkapiczynski.pl.dronvision.R;


public class SimulationFragment extends Fragment {

    private SimulationFragmentActivityListener simulationFragmentActivityListener;
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
                    simulationTurnOnOffButton.setEnabled(false);
                    simulationFragmentActivityListener.onTurnOnSimulationButtonClickedInSimulationFragment();
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof SimulationFragmentActivityListener){
            simulationFragmentActivityListener = (SimulationFragmentActivityListener) context;
        } else{
            throw new ClassCastException( context.toString() + " musi implementować interfejs: " +
                    "SimulationFragment.SimulationFragmentActivityListener");
        }
    }

    public void turnOffSimulationInSimulationFragment(){
        simulationTurnOnOffButton.setEnabled(true);
        simulationTurnOnOffButton.setChecked(false);
    }

    // interfejs, który będzie implementować aktywność
    public interface SimulationFragmentActivityListener {
        public void onTurnOnSimulationButtonClickedInSimulationFragment();
    }

}
