package pl.mkapiczynski.dron.message;

import javax.persistence.PersistenceContext;

public class ClientLoginMessage implements Message{
	private String clientId;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}
