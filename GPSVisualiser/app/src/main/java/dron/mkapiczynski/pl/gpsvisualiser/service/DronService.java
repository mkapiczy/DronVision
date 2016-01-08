package dron.mkapiczynski.pl.gpsvisualiser.service;

import java.util.Set;

import dron.mkapiczynski.pl.gpsvisualiser.domain.Drone;

/**
 * Created by Miix on 2016-01-08.
 */
public interface DronService {
    public void updateDronesSet(Set<Drone> dronesSet, Drone droneToUpdate);

}
