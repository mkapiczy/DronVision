package pl.mkapiczynski.dron.domain;

import pl.mkapiczynski.dron.database.Location;

/**
 * Klasa do obliczeń związanych z obszarem przeszukanym.
 * Lokalizacja wraz z przypisaną współrzędną biegunową.
 * 
 * @author Michal Kapiczynski
 *
 */
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
