package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;


/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice extends MicroService {
    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }
    /**
     * initializing the microservice by subscribing to the specific events and broadcasts
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, (b) -> {
            terminate();
            Diary.getInstance().setLandoTerminate(System.currentTimeMillis());
        });
        subscribeEvent(BombDestroyerEvent.class, (e) -> {
            try {
                Thread.sleep(duration);
                sendBroadcast(new TerminateBroadcast());
            } catch (InterruptedException exception) {
            }
        });
    }
}