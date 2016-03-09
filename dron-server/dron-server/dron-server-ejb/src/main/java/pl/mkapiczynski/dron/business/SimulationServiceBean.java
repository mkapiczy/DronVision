package pl.mkapiczynski.dron.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
import pl.mkapiczynski.dron.message.ClientGeoDataMessage;
import pl.mkapiczynski.dron.message.Message;
import pl.mkapiczynski.dron.message.SimulationEndedMessage;

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

	private static Set<Long> clientsToSimulate = new HashSet<>();

	@Override
	public void handleSimulationMessage(Message incomingMessage, final Session session,
			final Set<Session> clientSessions) {
		log.info("Simulation message came");
		final List<GeoPoint> locationsToSimulate = getLocationsToSimulate();
		final int numberOfLocationsToSimulate = locationsToSimulate.size();
		final Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
		sessions.add(session);

		final Long droneId = 4l;
		clientsToSimulate.add(1l);
		if (droneService.createNewDroneSession(droneId)) {
			timer.scheduleAtFixedRate(new Runnable() {
				int i = 0;

				@Override
				public void run() {
					if (numberOfLocationsToSimulate > 0) {
						if (clientIsToBeSimulated(clientsToSimulate, 1l)) {
							if (i < numberOfLocationsToSimulate) {
								log.info("Simulates location with id : " + i);
								GeoPoint locationToSimulate = locationsToSimulate.get(i);
								gpsTrackerDeviceService.simulate(locationToSimulate, sessions);
								i++;
							} else {
								clientsToSimulate.remove(1l);
								timer.shutdown();
								sendSimulationEndedMessage(session);
							}
						} else {
							clientsToSimulate.remove(1l);
							timer.shutdown();
							sendSimulationEndedMessage(session);
						}
					}
				}

			}, 1, 1, TimeUnit.SECONDS);

		}
	}

	private Boolean clientIsToBeSimulated(Set<Long> clientsToSimulate, Long client) {
		Iterator<Long> iterator = clientsToSimulate.iterator();
		while(iterator.hasNext()){
			if (iterator.next().compareTo(1l) == 0) {
				return true;
			}
		}
		return false;

	}

	@Override
	public void handleEndSimulationMessage(Message incomingMessage, Session session) {
		clientsToSimulate.remove(1l);
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

	private void sendSimulationEndedMessage(Session clientSession) {
		SimulationEndedMessage simulationEndedMessage = new SimulationEndedMessage();
		try {
			clientSession.getAsyncRemote().sendObject(simulationEndedMessage);
			log.info("Message send to client : " + clientSession.getUserProperties().get("clientId"));
		} catch (IllegalArgumentException e) {
			log.error("Illegal argument exception while sending a message to client: "
					+ clientSession.getUserProperties().get("clientId") + " : " + e);
		}
	}

}
