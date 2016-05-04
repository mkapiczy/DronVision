package pl.mkapiczynski.dron.domain;

import java.util.Date;
/**
  * Niebazodanowa reprezentacja sesji drona do komunikacji z aplikacją DronVision (aby ułatwić parse'owanie po tamtej stronie)
 * @author Michal Kapiczynski
 *
 */
public class NDBDroneSession {
	private Long sessionId;
	private Long droneId;
	private String droneName;
	private String sessionStarted;
	private String sessionEnded;

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public Long getDroneId() {
		return droneId;
	}

	public void setDroneId(Long droneId) {
		this.droneId = droneId;
	}

	public String getDroneName() {
		return droneName;
	}

	public void setDroneName(String droneName) {
		this.droneName = droneName;
	}

	public String getSessionStarted() {
		return sessionStarted;
	}

	public void setSessionStarted(String sessionStarted) {
		this.sessionStarted = sessionStarted;
	}

	public String getSessionEnded() {
		return sessionEnded;
	}

	public void setSessionEnded(String sessionEnded) {
		this.sessionEnded = sessionEnded;
	}

}
