package pl.mkapiczynski.dron.location.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SearchedArea")
public class SearchedArea {
	@Id
	@GeneratedValue
	private Long loid;
	
	private List<Location> searchedLocations;
	

}
