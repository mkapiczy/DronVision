package pl.mkapiczynski.dron.message;

public class ClientLoginMessage implements Message{
	private String clientId;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}
