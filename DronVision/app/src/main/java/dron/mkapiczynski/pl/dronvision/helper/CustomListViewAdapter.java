package dron.mkapiczynski.pl.dronvision.helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.database.DBDrone;

/**
 * Created by Miix on 2016-01-15.
 */
public class CustomListViewAdapter extends ArrayAdapter<DBDrone> {

    private List<DBDrone> drones;
    private Context context;

    public CustomListViewAdapter(Context context, List<DBDrone> drones) {
        super(context, R.layout.row, drones);
        this.context = context;
        this.drones = drones;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.row, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.textView1);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox1);
        name.setText(drones.get(position).getDroneName());
        if(drones.get(position).getTracked()==true){
            checkBox.setChecked(true);
        } else{
            checkBox.setChecked(false);
        }
        return convertView;
    }
}
