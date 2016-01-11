package pl.mkapiczynski.dron.database;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "DroneSession")
public class DroneSession {
	@Id
	@GeneratedValue
	@Column(name="session_id")
	private Long sessionId;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="drone_id", nullable=false)
	private Drone drone;
	
	@Column(name="started")
	private Date sessionStarted;

	@Column(name="ended")
	private Date sessionEnded;

	@OneToOne
	@JoinColumn(name = "searchedArea")
	private SearchedArea searchedArea;

	
	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public Date getSessionStarted() {
		return sessionStarted;
	}

	public void setSessionStarted(Date sessionStarted) {
		this.sessionStarted = sessionStarted;
	}

	public Date getSessionEnded() {
		return sessionEnded;
	}

	public void setSessionEnded(Date sessionEnded) {
		this.sessionEnded = sessionEnded;
	}

	public SearchedArea getSearchedArea() {
		return searchedArea;
	}

	public void setSearchedArea(SearchedArea searchedArea) {
		this.searchedArea = searchedArea;
	}

}
