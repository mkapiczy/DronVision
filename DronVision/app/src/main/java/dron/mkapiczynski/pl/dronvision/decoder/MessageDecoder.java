package dron.mkapiczynski.pl.dronvision.decoder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import dron.mkapiczynski.pl.dronvision.jsonHelper.JsonDateDeserializer;
import dron.mkapiczynski.pl.dronvision.message.GeoDataMessage;


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
