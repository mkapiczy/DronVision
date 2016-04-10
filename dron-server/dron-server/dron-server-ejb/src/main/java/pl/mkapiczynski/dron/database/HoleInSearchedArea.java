package pl.mkapiczynski.dron.database;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class HoleInSearchedArea {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name="HoleInSearchedAres_Location")
	private List<Location> holeLocations;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Location> getHoleLocations() {
		return holeLocations;
	}

	public void setHoleLocations(List<Location> holeLocations) {
		this.holeLocations = holeLocations;
	}

}
