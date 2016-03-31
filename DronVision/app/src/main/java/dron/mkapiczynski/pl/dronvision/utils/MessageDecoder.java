package dron.mkapiczynski.pl.dronvision.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import dron.mkapiczynski.pl.dronvision.helper.JsonDateDeserializer;
import dron.mkapiczynski.pl.dronvision.message.GeoDataMessage;
import dron.mkapiczynski.pl.dronvision.message.GetDroneSessionsMessage;
import dron.mkapiczynski.pl.dronvision.message.GetPreferencesMessage;
import dron.mkapiczynski.pl.dronvision.message.GetSearchedAreaMessage;


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

    public static GetPreferencesMessage decodePreferencesMessage(String jsonMessage){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDateDeserializer());
        Gson gson = gsonBuilder.create();
        return gson.fromJson(jsonMessage, GetPreferencesMessage.class);
    }

    public static GetDroneSessionsMessage decodeGetDroneSessionsMessage(String jsonMessage){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDateDeserializer());
        Gson gson = gsonBuilder.create();
        return gson.fromJson(jsonMessage, GetDroneSessionsMessage.class);
    }

    public static GetSearchedAreaMessage decodeGetSearchedAreaMessage(String jsonMessage){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDateDeserializer());
        Gson gson = gsonBuilder.create();
        return gson.fromJson(jsonMessage, GetSearchedAreaMessage.class);
    }
}
