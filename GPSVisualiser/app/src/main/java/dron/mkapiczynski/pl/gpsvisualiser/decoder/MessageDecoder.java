package dron.mkapiczynski.pl.gpsvisualiser.decoder;

import java.io.StringReader;

import javax.json.Json;

import dron.mkapiczynski.pl.gpsvisualiser.message.GeoDataMessage;

/**
 * Created by Miix on 2016-01-05.
 */
public class MessageDecoder  {

    public static GeoDataMessage decodeGeoDataMessage(String jsonMessage){
        GeoDataMessage geoDataMessage = new GeoDataMessage();
        geoDataMessage.setDeviceId(Json.createReader(new StringReader(jsonMessage)).readObject().getString("deviceId"));
        geoDataMessage.setDeviceType(Json.createReader(new StringReader(jsonMessage)).readObject().getString("deviceType"));
        geoDataMessage.setTimestamp(Json.createReader(new StringReader(jsonMessage)).readObject().getString("timestamp"));
        geoDataMessage.setLatitude(Json.createReader(new StringReader(jsonMessage)).readObject().getString("latitude"));
        geoDataMessage.setLongitude(Json.createReader(new StringReader(jsonMessage)).readObject().getString("longitude"));
        geoDataMessage.setAltitude(Json.createReader(new StringReader(jsonMessage)).readObject().getString("altitude"));
        return geoDataMessage;
    }
}
