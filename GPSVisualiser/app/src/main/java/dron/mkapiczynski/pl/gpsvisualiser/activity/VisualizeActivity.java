package dron.mkapiczynski.pl.gpsvisualiser.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import dron.mkapiczynski.pl.gpsvisualiser.R;


public class VisualizeActivity extends AppCompatActivity {

    private MapView mapView;
    private MapController mapController;
    private List<OverlayItem> overlayItemList;
    private DefaultResourceProxyImpl defaultResourceProxyImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mapView = (MapView) findViewById(R.id.MapView);
        overlayItemList = new ArrayList<>();
        defaultResourceProxyImpl
                = new DefaultResourceProxyImpl(this);

        setMapViewDefaultSettings();

        GeoPoint myPositionPoint = new GeoPoint(52.24, 21.104);
        setMyPositionMarkerOnMap(myPositionPoint);
        mapController.setCenter(myPositionPoint);
        mapView.invalidate();

    }

    private void setMapViewDefaultSettings() {
        mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapController = (MapController) mapView.getController();
        mapController.setZoom(17);
    }

    private void setMyPositionMarkerOnMap(GeoPoint myPositionPoint) {
        OverlayItem newMyLocationItem = new OverlayItem("My Location", "My Location", myPositionPoint);
        overlayItemList.clear();
        overlayItemList.add(newMyLocationItem);
        MyItemizedIconOverlay myItemizedIconOverlay = new MyItemizedIconOverlay(overlayItemList, null, defaultResourceProxyImpl);
        mapView.getOverlays().clear();
        mapView.getOverlays().add(myItemizedIconOverlay);
    }

    private class MyItemizedIconOverlay extends ItemizedIconOverlay<OverlayItem> {

        public MyItemizedIconOverlay(List<OverlayItem> pList,
                                     org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener<OverlayItem> pOnItemGestureListener,
                                     ResourceProxy pResourceProxy) {
            super(pList, pOnItemGestureListener, pResourceProxy);

        }

        @Override
        public void draw(Canvas canvas, MapView mapview, boolean arg2) {
            //super.draw(canvas, mapview, arg2);

            if (!overlayItemList.isEmpty()) {

                //overlayItemArray have only ONE element only, so I hard code to get(0)
                GeoPoint in = (GeoPoint) overlayItemList.get(0).getPoint();

                Point out = new Point();
                mapview.getProjection().toPixels(in, out);

                Bitmap bm = BitmapFactory.decodeResource(getResources(),
                        R.drawable.marker);



                Paint p = new Paint();
                ColorFilter filter = new LightingColorFilter(Color.BLUE, 1);
                p.setColorFilter(filter);

                canvas.drawBitmap(bm,
                        out.x - bm.getWidth() / 2,  //shift the bitmap center
                        out.y - bm.getHeight() / 2,  //shift the bitmap center
                       p);


            }
        }
    }

}
