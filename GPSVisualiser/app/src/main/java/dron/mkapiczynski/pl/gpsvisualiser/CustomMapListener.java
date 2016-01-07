package dron.mkapiczynski.pl.gpsvisualiser;



import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import dron.mkapiczynski.pl.gpsvisualiser.activity.VisualizeActivity;

/**
 * Created by Miix on 2016-01-06.
 */
public class CustomMapListener implements MapListener {
        Activity activity;
        private int oldZoomLevel = -1;
        private Context context;

        public CustomMapListener(Activity activity, Context context, int oldZoomLevel){
                this.activity = activity;
                this.context = context;
                this.oldZoomLevel = oldZoomLevel;
        }

        @Override
        public boolean onScroll(ScrollEvent event) {
                return false;
        }

        @Override
        public boolean onZoom(ZoomEvent event) {
                if (oldZoomLevel == -1) {
                        oldZoomLevel = event.getZoomLevel();
                } else if(oldZoomLevel < event.getZoomLevel()){
                        Toast.makeText(context,"Zoom in!", Toast.LENGTH_SHORT).show();
                        oldZoomLevel = event.getZoomLevel();

                } else if(oldZoomLevel > event.getZoomLevel()){
                        Toast.makeText(context,"Zoom out!", Toast.LENGTH_SHORT).show();
                        oldZoomLevel = event.getZoomLevel();
                        MapView mapView = (MapView)activity.findViewById(R.id.MapView);
                        mapView.getOverlays();
                }
                return false;
        }

        public void setContext(Context context) {
                this.context = context;
        }

        public void setOldZoomLevel(int oldZoomLevel) {
                this.oldZoomLevel = oldZoomLevel;
        }
}