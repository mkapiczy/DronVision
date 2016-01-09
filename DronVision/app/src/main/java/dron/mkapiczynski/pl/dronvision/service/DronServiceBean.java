package dron.mkapiczynski.pl.dronvision.service;

import android.graphics.Color;

import org.osmdroid.util.GeoPoint;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import dron.mkapiczynski.pl.dronvision.domain.Drone;


/**
 * Created by Miix on 2016-01-08.
 */
public class DronServiceBean implements DronService {
    @Override
    public void updateDronesSet(Set<Drone> dronesSet, Drone droneToUpdate) {
        if (dronesSetContainsThisDrone(dronesSet, droneToUpdate)) {
            updateDroneInSet(dronesSet, droneToUpdate);
        } else {
            addDroneToSet(dronesSet, droneToUpdate);
        }
    }

    private boolean dronesSetContainsThisDrone(Set<Drone> drones, Drone droneToUpdate) {
        Iterator<Drone> iterator = drones.iterator();
        while (iterator.hasNext()) {
            Drone currentDroneOnList = iterator.next();
            if (currentDroneOnList.getDeviceId().equals(droneToUpdate.getDeviceId())) {
                return true;
            }
        }
        return false;
    }

    private void updateDroneInSet(Set<Drone> dronesSet, Drone droneToUpdate) {
        Iterator<Drone> iterator = dronesSet.iterator();
        while (iterator.hasNext()) {
            Drone currentDroneOnList = iterator.next();
            if (currentDroneOnList.getDeviceId().equals(droneToUpdate.getDeviceId())) {
                currentDroneOnList.setCurrentPosition(droneToUpdate.getCurrentPosition());
                currentDroneOnList.getLastSearchedArea().clear();
                currentDroneOnList.getLastSearchedArea().addAll(droneToUpdate.getLastSearchedArea());
            }
        }
    }

    private void addDroneToSet(Set<Drone> dronesSet, Drone droneToAdd) {
        droneToAdd.setColor(getRandomColor());
        droneToAdd.setSearchedArea(new HashSet<GeoPoint>());
        dronesSet.add(droneToAdd);
    }

    private int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

}
