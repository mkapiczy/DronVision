package dron.mkapiczynski.pl.dronvision.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.domain.DBDrone;
import dron.mkapiczynski.pl.dronvision.fragment.HistoryFragment;

/**
 * Created by Miix on 2016-01-15.
 */
public class CustomHistoryDronesListViewAdapter extends ArrayAdapter<DBDrone> {

    private HistoryFragment historyFragment;
    private List<DBDrone> assignedDrones;
    private Context context;


    public CustomHistoryDronesListViewAdapter(HistoryFragment historyFragment, Context context, List<DBDrone> assignedDrones) {
        super(context, R.layout.row, assignedDrones);
        this.context = context;
        this.assignedDrones = assignedDrones;
        this.historyFragment = historyFragment;

        historyFragment.changeHistoryListTitle("Dostępne drony:");
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.row_history, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.rowHistoryTextView);
        name.setText("Id: " + assignedDrones.get(position).getDroneName() + " Opis: (" + assignedDrones.get(position).getDroneDescription() + ")");
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 v.setBackgroundColor(Color.LTGRAY);
                historyFragment.showDroneSessionIdListView(assignedDrones.get(position).getDroneId());
            }
        });
        return convertView;
    }

    @Override
    public DBDrone getItem(int position) {
        return super.getItem(position);
    }

}
