package dron.mkapiczynski.pl.dronvision.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.domain.DroneSession;
import dron.mkapiczynski.pl.dronvision.fragment.HistoryFragment;

/**
 * Created by Miix on 2016-01-15.
 */
public class CustomHistoryDroneSessionsListViewAdapter extends ArrayAdapter<DroneSession> {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private HistoryFragment historyFragment;
    private Context context;
    private SessionManager sessionManager;
    private List<DroneSession> droneSessions;


    public CustomHistoryDroneSessionsListViewAdapter(HistoryFragment historyFragment, Context context, List<DroneSession> droneSessions) {
        super(context, R.layout.row, droneSessions);
        this.context = context;
        this.droneSessions = droneSessions;
        this.historyFragment = historyFragment;
        sessionManager = new SessionManager(context);
        historyFragment.changeHistoryListTitle("Dostępne sesje:");

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.row_history, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.rowHistoryTextView);
        String sessionStarted = dateFormat.format(droneSessions.get(position).getSessionStarted());
        String sessionEnded = dateFormat.format(droneSessions.get(position).getSessionEnded());
        name.setText(sessionStarted + " - " + sessionEnded);
        convertView.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               if (historyFragment.isHistoryModeEnabled()) {
                                                   v.setBackgroundColor(Color.LTGRAY);
                                                   historyFragment.showHistory(droneSessions.get(position).getSessionId());
                                               } else {
                                                   Toast.makeText(context, "Historia niedostępna w trybie symulacji", Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       }

        );
        return convertView;
    }


    @Override
    public DroneSession getItem(int position) {
        return super.getItem(position);
    }

}
