package pl.mkapiczynski.dron.domain;

import java.util.List;

import pl.mkapiczynski.dron.database.Location;

public class DegreeHole {
	private int degree;
	private List<Location> locaions;

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public List<Location> getLocaions() {
		return locaions;
	}

	public void setLocaions(List<Location> locaions) {
		this.locaions = locaions;
	}

}
