package dron.mkapiczynski.pl.gpsvisualiser.message;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by Miix on 2016-01-05.
 */
public class ClientLoginMessage {
    private static final String MESSAGE_TYPE = "ClientLoginMessage";
    private final String messageType = MESSAGE_TYPE;
    private String clientId;


    public String getMessageType() {
        return messageType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "TrackerLoginMessage [messageType=" + messageType + ", clientId=" + clientId + "]";
    }


}
