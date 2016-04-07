package pl.mkapiczynski.dron.database;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class SearchedArea {
	@Id
	@GeneratedValue
	private Long id;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Location> searchedLocations;

	/*
	 * @OneToOne(targetEntity=HoleInSearchedArea.class, cascade =
	 * CascadeType.ALL)
	 * 
	 * @JoinColumn(name = "holesInSearchedArea") private
	 * List<HoleInSearchedArea> holesInSearchedArea;
	 */
	@ManyToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinTable(name="SearchedArea_Holes")
	private List<Location> holesInSearchedArea;

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

	public List<Location> getHolesInSearchedArea() {
		return holesInSearchedArea;
	}

	public void setHolesInSearchedArea(List<Location> holesInSearchedArea) {
		this.holesInSearchedArea = holesInSearchedArea;
	}

}
