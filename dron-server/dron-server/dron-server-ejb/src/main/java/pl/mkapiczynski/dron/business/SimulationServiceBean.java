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
import pl.mkapiczynski.dron.database.SimulationSession;
import pl.mkapiczynski.dron.domain.Constants;
import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.message.Message;
import pl.mkapiczynski.dron.message.SimulationEndedMessage;
import pl.mkapiczynski.dron.message.SimulationMessage;

/**
 * Klasa biznesowa do obsługi symulacji
 * 
 * @author Michal Kapiczynski
 *
 */
@Local
@Stateless(name = "SimulationService")
public class SimulationServiceBean implements SimulationService {

	private static final Logger log = Logger.getLogger(SimulationServiceBean.class);

	@PersistenceContext(name = "dron")
	private EntityManager entityManager;

	@Inject
	private AdministrationService administrationService;

	@Inject
	private GPSTrackerDeviceService gpsTrackerDeviceService;

	@Inject
	private DroneService droneService;
	
	@Inject
	private SimulationSessionService simulationSessionService;

	private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

	private static Set<Long> clientsToSimulate = new HashSet<>();
	
	/**
	 * Metoda obsługująca wiadomości symulacyjne od instancji aplikacji DronVision
	 */
	@Override
	public void handleSimulationMessage(Message incomingMessage, Session session, Set<Session> clientSessions) {
		SimulationMessage simulationMessage = (SimulationMessage) incomingMessage;
		String task = simulationMessage.getTask();
		if (task != null) {
			switch (task) {
			case "startSimulation":
				handleStartSimulationMessage(simulationMessage, session, clientSessions);
				break;

			case "endSimulation":
				handleEndSimulationMessage(simulationMessage, session);
				break;

			case "stopSimulation":
				handleStopSimulationMessage(simulationMessage, session);
				break;

			case "rerunSimulation":
				handleRerunSimulationMessage(simulationMessage, session);
				break;

			default:
				log.info("Simulation message with unknown taks");
			}
		} else {
			log.info("Property task in simulation message can not be NULL!");
		}

	}

	/**
	 * Metoda obsługująca wiadomośc startową od aplikacji DronVision
	 * 
	 * @param startSimulationMessage
	 * @param session
	 * @param clientSessions
	 */
	private void handleStartSimulationMessage(SimulationMessage startSimulationMessage, final Session session,
			final Set<Session> clientSessions) {
		log.info("Start Simulation message came");
		final Long clientId = startSimulationMessage.getDeviceId();
		simulationSessionService.deleteAllSimulationSessionsForClient(clientId);
		SimulationSession simulationSession = simulationSessionService.createNewSimulationSession(clientId);
		if (simulationSession != null) {
			Long lastSimulationId = 0l;
			simulationSession.setLastSimulationId(lastSimulationId);
			
			runSimulation(clientId, lastSimulationId, session);
		}
	}
	
	/**
	 * Metoda uruchamiająca symulację
	 * 
	 * @param clientId
	 * @param lastSimulationId
	 * @param session
	 */
	private void runSimulation(final Long clientId, final Long lastSimulationId, final Session session) {
		final List<GeoPoint> locationsToSimulate = getLocationsToSimulate(lastSimulationId);
		final int numberOfLocationsToSimulate = locationsToSimulate.size();

		final Set<Session> clientSessionsForSimulation = Collections.synchronizedSet(new HashSet<Session>());
		clientSessionsForSimulation.add(session);

		final Long droneId = Constants.SIMULATION_DRONE_ID;
		clientsToSimulate.add(clientId);

		if (droneService.createNewDroneSession(droneId)) {
			timer.scheduleAtFixedRate(new Runnable() {
				int i = 0;
				Long lastSimulationIdIncrement = lastSimulationId;
				@Override
				public void run() {
					if (numberOfLocationsToSimulate > 0) {
						if (clientIsToBeSimulated(clientsToSimulate, clientId)) {
							if (i < numberOfLocationsToSimulate) {
								log.info("Simulates location with id : " + lastSimulationIdIncrement);
								GeoPoint locationToSimulate = locationsToSimulate.get(i);
								gpsTrackerDeviceService.simulate(locationToSimulate, clientSessionsForSimulation);
								simulationSessionService.setLastSimulationId(lastSimulationIdIncrement, clientId);
								i++;
								lastSimulationIdIncrement++;
							} else {
								clientsToSimulate.remove(clientId);
								timer.shutdown();
								sendSimulationEndedMessage(session);
							}
						} else {
							clientsToSimulate.remove(clientId);
							timer.shutdown();
						}
					}
					if (clientsToSimulate.isEmpty()) {
						timer.shutdown();
					}
				}

			}, 1, 1, TimeUnit.SECONDS);

		}
	}
	
	/**
	 * Metoda obsługująca wiadomośc kończącą symulację od aplikacji DronVision
	 * @param endSimulationMessage
	 * @param session
	 */
	private void handleEndSimulationMessage(SimulationMessage endSimulationMessage, Session session) {
		Long clientId = endSimulationMessage.getDeviceId();
		clientsToSimulate.remove(clientId);
		simulationSessionService.deleteSimulationSession(clientId);
		droneService.closeDroneActiveSession(Constants.SIMULATION_DRONE_ID);
		sendSimulationEndedMessage(session);
	}

	/**
	 * Metoda obsługująca wiadomośc zatrzymującą symulację od aplikacji DronVision
	 * 
	 * @param stopSimulationMessage
	 * @param session
	 */
	private void handleStopSimulationMessage(SimulationMessage stopSimulationMessage, Session session) {
		Long clientId = stopSimulationMessage.getDeviceId();
		clientsToSimulate.remove(clientId);
	}
	
	/**
	 * Metoda obsługująca wiadomośc wznawiającą symulację od aplikacji DronVison
	 * @param rerunSimulationMessage
	 * @param session
	 */
	private void handleRerunSimulationMessage(SimulationMessage rerunSimulationMessage, Session session) {
		Long clientId = rerunSimulationMessage.getDeviceId();
		SimulationSession simulationSession = simulationSessionService.getSimulationSessionForClient(clientId);
		if (simulationSession != null) {
			Long lastSimulationId = simulationSession.getLastSimulationId();
			if(lastSimulationId!=null){
				runSimulation(clientId, lastSimulationId, session);
			}
		}
	}
	
	private Boolean clientIsToBeSimulated(Set<Long> clientsToSimulate, Long client) {
		Iterator<Long> iterator = clientsToSimulate.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().compareTo(1l) == 0) {
				return true;
			}
		}
		return false;

	}

	private List<GeoPoint> getLocationsToSimulate(Long lastSimulatedLocation) {
		List<GeoPoint> list = new ArrayList<>();
		String queryStr = "SELECT s FROM Simulation s WHERE s.id > :lastSimulatedLocation ORDER BY s.id ASC";

		TypedQuery<Simulation> query = entityManager.createQuery(queryStr, Simulation.class);

		query.setParameter("lastSimulatedLocation", lastSimulatedLocation);

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
