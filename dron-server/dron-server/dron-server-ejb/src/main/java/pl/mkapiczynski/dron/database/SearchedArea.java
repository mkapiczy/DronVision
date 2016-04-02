package pl.mkapiczynski.dron.database;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
public class SearchedArea {
	@Id
	@GeneratedValue
	private Long id;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Location> searchedLocations;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<HoleInSearchedArea> holesInSearchedArea;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Location> getSearchedLocations() {
		return searchedLocations;
	}

	public void setSearchedLocations(List<Location> searchedLocations) {
		this.searchedLocations = searchedLocations;
	}

	public List<HoleInSearchedArea> getHolesInSearchedArea() {
		return holesInSearchedArea;
	}

	public void setHolesInSearchedArea(List<HoleInSearchedArea> holesInSearchedArea) {
		this.holesInSearchedArea = holesInSearchedArea;
	}
	
	

}
