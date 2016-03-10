package pl.mkapiczynski.dron.database;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SimulationSession")
public class SimulationSession {

	@Id
	@GeneratedValue
	private Long id;

	private Long clientId;

	private Long lastSimulationId;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "searchedArea")
	private SearchedArea searchedArea;

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

	public SearchedArea getSearchedArea() {
		return searchedArea;
	}

	public void setSearchedArea(SearchedArea searchedArea) {
		this.searchedArea = searchedArea;
	}

}
