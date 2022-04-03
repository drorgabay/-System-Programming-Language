package bgu.spl.mics.application.passiveObjects;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */

public class Ewoks {
    /**
     * a private static class to make the singleton of this Ewoks object to be thread-safe (like in lecture)
     */
    private static class SingletonEwoks {
        private static Ewoks instance = new Ewoks();
    }
    private ConcurrentHashMap<Integer, Ewok> resources;

    /**
     * private constructor for this singleton class
     */
    private Ewoks() {
        resources = new ConcurrentHashMap<>();
    }


    public static Ewoks getInstance() {
        return SingletonEwoks.instance;
    }

    /**
     * getter for the resouces
     * @return
     */
    public ConcurrentHashMap<Integer, Ewok> getResources() {
        return resources;
    }

    /**
     * creates numberOfEwoks from 1 to parameter before all in initialization (Main)
     * @param numberOfEwoks the number of ewoks that are available in all the attacks (from 1 to numberofEwoks)
     */
    public void initEwoks(int numberOfEwoks) {
        for (int i = 1; i <= numberOfEwoks; i++) {
            resources.put(i, new Ewok(i));
        }
    }
}
