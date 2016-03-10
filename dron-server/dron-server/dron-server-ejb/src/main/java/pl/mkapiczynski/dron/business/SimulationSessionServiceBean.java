package pl.mkapiczynski.dron.business;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.websocket.Session;

import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.database.SearchedArea;
import pl.mkapiczynski.dron.database.SimulationSession;

@Local
@Stateless(name = "SimulationSessionService")
public class SimulationSessionServiceBean implements SimulationSessionService {

	@PersistenceContext(name = "dron")
	private EntityManager entityManager;

	@Override
	public void setLastSimulationId(Long lastSimulationId, Long clientId) {
		SimulationSession activeSimulationSession = getSimulationSessionForClient(clientId);
		if (activeSimulationSession != null) {
			activeSimulationSession.setLastSimulationId(lastSimulationId);
		}
	}

	@Override
	public SimulationSession createNewSimulationSession(Long clientId) {
		SimulationSession simulationSession = null;
		if (clientId != null) {
			simulationSession = new SimulationSession();
			simulationSession.setClientId(clientId);
			entityManager.persist(simulationSession);
		}
		return simulationSession;
	}

	@Override
	public void deleteSimulationSession(Long clientId) {
		SimulationSession simulationSession = getSimulationSessionForClient(clientId);
		if (simulationSession != null) {
			entityManager.remove(simulationSession);
		}
	}

	@Override
	public void deleteAllSimulationSessionsForClient(Long clientId) {
		SimulationSession simulationSession = null;
		String queryStr = "SELECT s from SimulationSession s WHERE s.clientId = :clientId";
		TypedQuery<SimulationSession> query = entityManager.createQuery(queryStr, SimulationSession.class);
		query.setParameter("clientId", clientId);
		List<SimulationSession> simulationSessionsList = query.getResultList();
		for (int i = 0; i < simulationSessionsList.size(); i++) {
			entityManager.remove(simulationSessionsList.get(i));
		}
	}

	@Override
	public SimulationSession getSimulationSessionForClient(Long clientId) {
		SimulationSession simulationSession = null;
		String queryStr = "SELECT s from SimulationSession s WHERE s.clientId = :clientId";
		TypedQuery<SimulationSession> query = entityManager.createQuery(queryStr, SimulationSession.class);
		query.setParameter("clientId", clientId);
		List<SimulationSession> simulationSessionsList = query.getResultList();
		if (simulationSessionsList != null && !simulationSessionsList.isEmpty()) {
			simulationSession = simulationSessionsList.get(0);
		}
		return simulationSession;
	}

	@Override
	public void updateSimulationData(Drone drone, Set<Session> clientSessions) {
		if (drone.getDroneId().compareTo(4l) == 0) {
			Iterator<Session> iterator = clientSessions.iterator();
			while (iterator.hasNext()) {
				Long clientId = (Long) iterator.next().getUserProperties().get("clientId");
				SimulationSession simulationSession = getSimulationSessionForClient(clientId);
				if (simulationSession != null) {
					if (drone != null && drone.getActiveSession() != null
							&& drone.getActiveSession().getSearchedArea() != null
							&& !drone.getActiveSession().getSearchedArea().getSearchedLocations().isEmpty()) {
						simulationSession.setSearchedArea(drone.getActiveSession().getSearchedArea());
					}
					if (drone.getActiveSession().getSearchedArea() == null
							|| drone.getActiveSession().getSearchedArea().getSearchedLocations().isEmpty()) {
						SearchedArea simulationSearchedArea = simulationSession.getSearchedArea();
						if (simulationSearchedArea != null) {
							drone.getActiveSession().setSearchedArea(simulationSearchedArea);
						}
					}
				}
			}
		}
	}

}
