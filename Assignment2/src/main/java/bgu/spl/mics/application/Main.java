package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import javax.naming.directory.DirContext;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        //read from the json file
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(args[0]));
        Read r = gson.fromJson(reader, Read.class);

        //initialize all resources inputs and create threads
        Ewoks.getInstance().initEwoks(r.Ewoks);
        Thread leia = new Thread(new LeiaMicroservice(r.attacks));
        Thread c3p0 = new Thread(new C3POMicroservice());
        Thread hanSolo = new Thread(new HanSoloMicroservice());
        Thread lando = new Thread(new LandoMicroservice(r.Lando));
        Thread r2d2 = new Thread(new R2D2Microservice(r.R2D2));

        //start all threads
        hanSolo.start();
        c3p0.start();
        r2d2.start();
        lando.start();
        leia.start();
        //join all threads
        hanSolo.join();
        c3p0.join();
        r2d2.join();
        lando.join();
        leia.join();

        //write to output json file
        writeToJsonFile(args[1]);

    }

    /**
     * create a output json file and write all Diary parameters inside
     *
     * @param p path string to wtire the json file
     * @throws IOException
     */
    public static void writeToJsonFile(String p) throws IOException {
        FileWriter fileWriter = new FileWriter(p);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(Diary.getInstance(), fileWriter);
        fileWriter.close();
    }

}
