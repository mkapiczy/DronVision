package pl.mkapiczynski.dron.location.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="SearchedLocation")
public class SearchedLocation extends Location {
	
	private Boolean registered;

}
