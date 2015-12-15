package pl.mkapiczynski.dron.message;

import java.util.Set;

public class UsersMessage implements Message {
	private Set<String> users;

	public UsersMessage(Set<String> users) {
		this.users = users;
	}

	public Set<String> getUsers() {
		return users;
	}

	public void setUsers(Set<String> users) {
		this.users = users;
	}

}
