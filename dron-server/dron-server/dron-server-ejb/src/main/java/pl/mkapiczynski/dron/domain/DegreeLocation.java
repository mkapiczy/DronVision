package pl.mkapiczynski.dron.domain;

import pl.mkapiczynski.dron.database.Location;

public class DegreeLocation {
	private Location location;
	private int degree;

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

}
