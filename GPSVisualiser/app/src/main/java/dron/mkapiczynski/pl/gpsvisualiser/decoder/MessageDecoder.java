package dron.mkapiczynski.pl.gpsvisualiser.decoder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.StringReader;
import java.util.Date;

import javax.json.Json;

import dron.mkapiczynski.pl.gpsvisualiser.jsonHelper.JsonDateDeserializer;
import dron.mkapiczynski.pl.gpsvisualiser.jsonHelper.JsonDateSerializer;
import dron.mkapiczynski.pl.gpsvisualiser.message.ClientLoginMessage;
import dron.mkapiczynski.pl.gpsvisualiser.message.GeoDataMessage;

/**
 * Created by Miix on 2016-01-05.
 */
public class MessageDecoder  {

    public static GeoDataMessage decodeGeoDataMessage(String jsonMessage){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDateDeserializer());
        Gson gson = gsonBuilder.create();
        return gson.fromJson(jsonMessage, GeoDataMessage.class);
    }
}
