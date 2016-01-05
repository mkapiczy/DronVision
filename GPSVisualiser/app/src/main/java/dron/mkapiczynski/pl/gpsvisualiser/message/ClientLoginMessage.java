package dron.mkapiczynski.pl.gpsvisualiser.message;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by Miix on 2016-01-05.
 */
public class ClientLoginMessage {
    private static final String MESSAGE_TYPE = "ClientLoginMessage";
    private String clientId;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String toJson() {
        JsonObject jsonObject = Json.createObjectBuilder().add("messageType", MESSAGE_TYPE)
                .add("clientId", clientId).build();
        StringWriter stringWriter = new StringWriter();
        javax.json.JsonWriter jsonWriter = Json.createWriter(stringWriter);
        jsonWriter.write(jsonObject);
        return stringWriter.toString();
    }
}
