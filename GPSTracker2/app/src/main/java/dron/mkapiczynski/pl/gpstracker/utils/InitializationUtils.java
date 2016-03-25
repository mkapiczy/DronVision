package dron.mkapiczynski.pl.gpstracker.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Miix on 2016-03-25.
 */
public class InitializationUtils {
    public static String getInitializationNgrokPort(){
        //File sdcard = Environment.getDataDirectory();
        String filePath = "/storage/emulated/0/documents/ngrok.txt";

//Get the text file
        File file = new File(filePath);

//Read text from file
        StringBuilder ngrokPortFromFile = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                ngrokPortFromFile.append(line);
            }
            br.close();
        }
        catch (IOException e) {
            Log.e("LOGIN", "Error while reading ngrok properties file: " + e);
        }
        return ngrokPortFromFile.toString();
    }
}
