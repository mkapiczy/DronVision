package pl.mkapiczynski.dron.message;

import javax.persistence.PersistenceContext;

public class ClientLoginMessage implements Message{
	private Long clientId;

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

}
