package pl.mkapiczynski.dron.database;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SimulationSession")
public class SimulationSession {

	@Id
	@GeneratedValue
	private Long id;

	private Long clientId;

	private Long lastSimulationId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getLastSimulationId() {
		return lastSimulationId;
	}

	public void setLastSimulationId(Long lastSimulationId) {
		this.lastSimulationId = lastSimulationId;
	}

}
