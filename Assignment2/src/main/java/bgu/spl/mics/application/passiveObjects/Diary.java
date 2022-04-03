package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.MessageBusImpl;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {

    /**
     * a private static class to make the singleton of this Diary object to be thread-safe (like in lecture)
     */
    private static class SingletonDiary {
        private static Diary instance = new Diary();
    }

    private AtomicInteger totalAttacks;
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;

    /**
     * private constructor of this class
     */
    private Diary() {
        totalAttacks = new AtomicInteger(0);
        HanSoloFinish = 0;
        C3POFinish = 0;
        R2D2Deactivate = 0;
        LeiaTerminate = 0;
        HanSoloTerminate = 0;
        C3POTerminate = 0;
        R2D2Terminate = 0;
        LandoTerminate = 0;
    }

    public static Diary getInstance() {
        return SingletonDiary.instance;
    }



    /**
     * increments the total attacks using the compare and set atomic action because 2 threads are using this method(safe and correct)
     */
    public void incrementTotalAttack() {
        int oldVal;
        int newVal;
        do {
            oldVal = totalAttacks.get();
            newVal = oldVal + 1;
        }
        while (!totalAttacks.compareAndSet(oldVal, newVal));
    }
    // setters
    public void setC3POFinish(long c3POFinish) {
        C3POFinish = c3POFinish;
    }

    public void setC3POTerminate(long c3POTerminate) {
        C3POTerminate = c3POTerminate;
    }

    public void setHanSoloFinish(long hanSoloFinish) {
        HanSoloFinish = hanSoloFinish;
    }

    public void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate;
    }

    public void setLandoTerminate(long landoTerminate) {
        LandoTerminate = landoTerminate;
    }

    public void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate;
    }

    public void setR2D2Deactivation(long r2D2Deactivation) {
        R2D2Deactivate = r2D2Deactivation;
    }

    public void setR2D2Terminate(long r2D2Terminate) {
        R2D2Terminate = r2D2Terminate;
    }


    //getters
    public long getC3POFinish() {
        return C3POFinish;
    }

    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }

    public long getR2D2Deactivation() {
        return R2D2Deactivate;
    }

    public long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }

    public int getTotalAttack() {
        return totalAttacks.get();
    }
}
