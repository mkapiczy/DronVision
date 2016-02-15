package dron.mkapiczynski.pl.dronvision.message;

import dron.mkapiczynski.pl.dronvision.domain.Parameters;

/**
 * Created by Miix on 2016-01-05.
 */
public class ClientLoginMessage {
    private final String messageType = Parameters.CLIENT_LOGIN_MESSAGE_TYPE;
    private Long clientId;


    public String getMessageType() {
        return messageType;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "TrackerLoginMessage [messageType=" + messageType + ", clientId=" + clientId + "]";
    }


}
