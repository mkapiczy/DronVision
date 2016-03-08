package pl.mkapiczynski.dron.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.websocket.Session;

import org.jboss.logging.Logger;

import pl.mkapiczynski.dron.database.Simulation;
import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.message.Message;

@Local
@Stateless(name = "SimulationService")
public class SimulationServiceBean implements SimulationService {

	private static final Logger log = Logger.getLogger(SimulationServiceBean.class);

	@PersistenceContext(name = "dron")
	private EntityManager entityManager;

	@Inject
	private GPSTrackerDeviceService gpsTrackerDeviceService;

	@Inject
	private DroneService droneService;

	private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

	@Override
	public void handleTrackerSimulationMessage(Message incomingMessage, final Session session, final Set<Session> clientSessions) {
		log.info("Simulation message came");
		final List<GeoPoint> locationsToSimulate = getLocationsToSimulate();
		final int numberOfLocationsToSimulate = locationsToSimulate.size();
		final Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
		sessions.add(session);

		final Long droneId = 4l;
		if (droneService.createNewDroneSession(droneId)) {
			timer.scheduleAtFixedRate(new Runnable() {
				int i = 0;

				@Override
				public void run() {
					if (numberOfLocationsToSimulate > 0) {
						if (i < numberOfLocationsToSimulate) {
							log.info("Simulates location with id : " + i+1);
							GeoPoint locationToSimulate = locationsToSimulate.get(i);
							gpsTrackerDeviceService.simulate(locationToSimulate, sessions);
							i++;
						} else {
							timer.shutdown();
						}
					}
				}

			}, 1, 1, TimeUnit.SECONDS);
			
		}
	}

	private List<GeoPoint> getLocationsToSimulate() {
		List<GeoPoint> list = new ArrayList<>();
		String queryStr = "SELECT s FROM Simulation s ORDER BY s.id ASC";

		TypedQuery<Simulation> query = entityManager.createQuery(queryStr, Simulation.class);

		// query.setParameter("simulatedFlag", false);

		List<Simulation> simulationList = query.getResultList();
		for (int i = 0; i < simulationList.size(); i++) {
			Simulation currentSimulation = simulationList.get(i);
			GeoPoint location = new GeoPoint();
			location.setLatitude(currentSimulation.getLatitude());
			location.setLongitude(currentSimulation.getLongitude());
			location.setAltitude(currentSimulation.getAltitude());
			list.add(location);
		}
		return list;
	}

}
