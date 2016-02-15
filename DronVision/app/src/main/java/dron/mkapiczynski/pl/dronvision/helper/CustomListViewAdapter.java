package dron.mkapiczynski.pl.dronvision.helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.database.DBDrone;

/**
 * Created by Miix on 2016-01-15.
 */
public class CustomListViewAdapter extends ArrayAdapter<DBDrone> {

    private List<DBDrone> assignedDrones;
    private List<DBDrone> checkedDrones;
    private Context context;
    private boolean changed = false;


    public CustomListViewAdapter(Context context, List<DBDrone> assignedDrones, List<DBDrone> checkedDrones) {
        super(context, R.layout.row, assignedDrones);
        this.context = context;
        this.assignedDrones = assignedDrones;
        this.checkedDrones = checkedDrones;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.row, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.textView1);
        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox1);
        name.setText("Id: " + assignedDrones.get(position).getDroneName() + " Status: (" + assignedDrones.get(position).getStatus() + ")");
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Click!", Toast.LENGTH_SHORT).show();
            }
        });
        if (droneIsChecked(assignedDrones.get(position), checkedDrones)) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changed = true;
                if(checkBox.isChecked()) {
                    checkedDrones.add(assignedDrones.get(position));
                } else{
                    removeDroneWithId(assignedDrones.get(position).getDroneId(), checkedDrones);
                }
            }
        });
        return convertView;
    }

    private void removeDroneWithId(Long id, List<DBDrone> drones){
        for(int i=0;i<drones.size();i++){
            if(drones.get(i).getDroneId()==id){
                drones.remove(drones.get(i));
            }
        }
    }

    @Override
    public DBDrone getItem(int position) {
        return super.getItem(position);
    }


    public List<DBDrone> getCheckedDrones(){
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

    public boolean wasChanged(){
        return changed;
    }
}
