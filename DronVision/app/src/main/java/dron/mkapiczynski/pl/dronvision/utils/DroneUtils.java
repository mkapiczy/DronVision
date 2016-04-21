package dron.mkapiczynski.pl.dronvision.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.domain.MapHoleInSearchedArea;

/**
 * Created by Miix on 2016-01-14.
 */
public class DroneUtils {

    public static void updateDronesSet(Set<Drone> dronesSet, Drone droneToUpdate) {
        if (dronesSetContainsThisDrone(droneToUpdate,dronesSet)) {
            updateDroneInSet(dronesSet, droneToUpdate);
        } else {
            addDroneToSet(dronesSet, droneToUpdate);
        }
    }

    public static Drawable getDroneMarkerIcon(Drone dronToUpdate, Activity activity) {
        Long droneId = dronToUpdate.getDroneId();
        Drawable droneIcon = null;
        if(droneId.compareTo(1l)==0){
           droneIcon = activity.getResources().getDrawable(R.drawable.drone_marker);
        } else if(droneId.compareTo(2l)==0){
            droneIcon = activity.getResources().getDrawable(R.drawable.drone_marker2);
        }else if(droneId.compareTo(3l)==0){
            droneIcon = activity.getResources().getDrawable(R.drawable.drone_marker3);
        }else if(droneId.compareTo(4l)==0){
            droneIcon = activity.getResources().getDrawable(R.drawable.drone_marker4);
        }
        ColorFilter filter = new LightingColorFilter(dronToUpdate.getColor(), 0);
        droneIcon.setColorFilter(filter);
        return droneIcon;
    }

    private static boolean dronesSetContainsThisDrone(Drone droneToUpdate, Set<Drone> drones) {
        Iterator<Drone> iterator = drones.iterator();
        while (iterator.hasNext()) {
            Drone currentDroneOnList = iterator.next();
            if (currentDroneOnList.getDroneId().equals(droneToUpdate.getDroneId())) {
                return true;
            }
        }
        return false;
    }

    private static void updateDroneInSet(Set<Drone> dronesSet, Drone droneToUpdate) {
        Iterator<Drone> iterator = dronesSet.iterator();
        while (iterator.hasNext()) {
            Drone currentDroneOnList = iterator.next();
            if (currentDroneOnList.getDroneId().equals(droneToUpdate.getDroneId())) {
                currentDroneOnList.setCurrentPosition(droneToUpdate.getCurrentPosition());
                currentDroneOnList.setLastSearchedArea(droneToUpdate.getLastSearchedArea());
                currentDroneOnList.setSearchedArea(droneToUpdate.getSearchedArea());
                currentDroneOnList.setHoles(droneToUpdate.getHoles());
                currentDroneOnList.setLastHoles(droneToUpdate.getLastHoles());
            }
        }
    }

    private static void addDroneToSet(Set<Drone> dronesSet, Drone droneToAdd) {
        droneToAdd.setColor(getDroneColor(droneToAdd));
        if(droneToAdd.getSearchedArea()==null) {
            droneToAdd.setSearchedArea(new ArrayList<GeoPoint>());
        }
        if(droneToAdd.getLastSearchedArea()==null) {
            droneToAdd.setLastSearchedArea(new ArrayList<GeoPoint>());
        }
        if(droneToAdd.getLastHoles()==null){
            droneToAdd.setLastHoles(new ArrayList<MapHoleInSearchedArea>());
        }
        dronesSet.add(droneToAdd);
    }


    private static int getDroneColor(Drone drone) {
        Long droneId = drone.getDroneId();
        if(droneId.compareTo(1l)==0){
            return Color.BLUE;
        } else if(droneId.compareTo(2l)==0){
            return Color.RED;
        } else if(droneId.compareTo(3l)==0){
            return Color.GREEN;
        } else if(droneId.compareTo(4l)==0){
            return Color.YELLOW;
        } else{
            return Color.BLACK;
        }
    }



}
