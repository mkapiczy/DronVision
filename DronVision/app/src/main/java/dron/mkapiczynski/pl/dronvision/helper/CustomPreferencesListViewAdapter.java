package dron.mkapiczynski.pl.dronvision.helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.domain.DBDrone;

/**
 * Created by Miix on 2016-01-15.
 */
public class CustomPreferencesListViewAdapter extends ArrayAdapter<DBDrone> {

    private List<DBDrone> assignedDrones;
    private List<DBDrone> checkedDrones;
    private boolean singleFollowedDroneAdapter = false;
    private Context context;
    private boolean changed = false;
    private List<CheckBox> checkboxes;


    public CustomPreferencesListViewAdapter(Context context, List<DBDrone> assignedDrones, List<DBDrone> checkedDrones, boolean singleFollowedDroneAdapter) {
        super(context, R.layout.row, assignedDrones);
        this.context = context;
        this.assignedDrones = assignedDrones;
        this.checkedDrones = checkedDrones;
        this.singleFollowedDroneAdapter = singleFollowedDroneAdapter;
        checkboxes = new ArrayList<>();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.row, parent, false);
            TextView name = (TextView) convertView.findViewById(R.id.textView1);
            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox1);
            name.setText("Id: " + assignedDrones.get(position).getDroneName() + " Status: (" + assignedDrones.get(position).getStatus() + ")");
            checkboxes.add(checkBox);
            if (droneIsChecked(assignedDrones.get(position), checkedDrones)) {
                checkBox.setChecked(true);

            } else {
                checkBox.setChecked(false);
            }
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (singleFollowedDroneAdapter) {
                        if (checkBox.isChecked()) {
                            for(int i=0;i<checkboxes.size();i++){
                                if(!checkboxes.get(i).equals(checkBox)) {
                                    checkboxes.get(i).setChecked(false);
                                }
                            }
                            checkedDrones.clear();
                            checkedDrones.add(assignedDrones.get(position));
                        } else {
                            removeDroneWithId(assignedDrones.get(position).getDroneId(), checkedDrones);
                        }
                    } else {
                        changed = true;
                        if (checkBox.isChecked()) {
                            checkedDrones.add(assignedDrones.get(position));
                        } else {
                            removeDroneWithId(assignedDrones.get(position).getDroneId(), checkedDrones);
                        }
                    }
                }
            });
        return convertView;
    }

    private void removeDroneWithId(Long id, List<DBDrone> drones) {
        for (int i = 0; i < drones.size(); i++) {
            if (drones.get(i).getDroneId() == id) {
                drones.remove(drones.get(i));
            }
        }
    }

    @Override
    public DBDrone getItem(int position) {
        return super.getItem(position);
    }


    public List<DBDrone> getCheckedDrones() {
        return checkedDrones;
    }

    private boolean droneIsChecked(DBDrone drone, List<DBDrone> checkedDrones) {
        for (int i = 0; i < checkedDrones.size(); i++) {
            if (checkedDrones.get(i).getDroneId() == drone.getDroneId()) {
                return true;
            }
        }
        return false;
    }

    public boolean wasChanged() {
        return changed;
    }
}
