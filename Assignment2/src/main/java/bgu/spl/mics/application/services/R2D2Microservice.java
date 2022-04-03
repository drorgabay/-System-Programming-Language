package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private long duration;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
    }
    /**
     * initializing the microservice by subscribing to the specific events and broadcasts
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, (b) -> {
            terminate();
            Diary.getInstance().setR2D2Terminate(System.currentTimeMillis());
        });
        subscribeEvent(DeactivationEvent.class, (e) -> {
            try {
                Thread.sleep(duration);
                Diary.getInstance().setR2D2Deactivation(System.currentTimeMillis());
                sendEvent(new BombDestroyerEvent());
            } catch (InterruptedException exception) {
            }
        });
    }
}
