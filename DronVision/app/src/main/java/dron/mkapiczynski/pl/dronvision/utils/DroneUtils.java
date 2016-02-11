package dron.mkapiczynski.pl.dronvision.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.domain.Drone;

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

    public static void updateDronesSearchedArea(Set<Drone> drones){
        Iterator<Drone> dronesIterator = drones.iterator();
        while(dronesIterator.hasNext()) {
            Drone droneToUpdate = dronesIterator.next();
            List<GeoPoint> updatedSearchedArea = SearchedAreaUtil.updateSearchedAreaSet(droneToUpdate.getSearchedArea(), droneToUpdate.getLastSearchedArea());
            droneToUpdate.getSearchedArea().clear();
            droneToUpdate.getSearchedArea().addAll(updatedSearchedArea);
        }
    }

        public static Drawable getDroneMarkerIcon(Drone dronToUpdate, Activity activity) {
        Drawable droneIcon = activity.getResources().getDrawable(R.drawable.drone_marker);
        ColorFilter filter = new LightingColorFilter(dronToUpdate.getColor(), 1);
        droneIcon.setColorFilter(filter);
        return droneIcon;
    }



    private static boolean dronesSetContainsThisDrone(Drone droneToUpdate, Set<Drone> drones) {
        Iterator<Drone> iterator = drones.iterator();
        while (iterator.hasNext()) {
            Drone currentDroneOnList = iterator.next();
            if (currentDroneOnList.getDeviceId().equals(droneToUpdate.getDeviceId())) {
                return true;
            }
        }
        return false;
    }

    private static void updateDroneInSet(Set<Drone> dronesSet, Drone droneToUpdate) {
        Iterator<Drone> iterator = dronesSet.iterator();
        while (iterator.hasNext()) {
            Drone currentDroneOnList = iterator.next();
            if (currentDroneOnList.getDeviceId().equals(droneToUpdate.getDeviceId())) {
                currentDroneOnList.setCurrentPosition(droneToUpdate.getCurrentPosition());
                currentDroneOnList.setLastSearchedArea(droneToUpdate.getLastSearchedArea());
                currentDroneOnList.setSearchedArea(droneToUpdate.getSearchedArea());
            }
        }
    }

    private static void addDroneToSet(Set<Drone> dronesSet, Drone droneToAdd) {
        droneToAdd.setColor(getRandomColor());
        if(droneToAdd.getSearchedArea()==null) {
            droneToAdd.setSearchedArea(new ArrayList<GeoPoint>());
        }
        if(droneToAdd.getLastSearchedArea()==null) {
            droneToAdd.setLastSearchedArea(new ArrayList<GeoPoint>());
        }
        dronesSet.add(droneToAdd);
    }

    private static int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }



}
