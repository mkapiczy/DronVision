package dron.mkapiczynski.pl.dronvision.service;


import java.util.Set;

import dron.mkapiczynski.pl.dronvision.domain.Drone;

/**
 * Created by Miix on 2016-01-08.
 */
public interface DronService {
    public void updateDronesSet(Set<Drone> dronesSet, Drone droneToUpdate);

}
