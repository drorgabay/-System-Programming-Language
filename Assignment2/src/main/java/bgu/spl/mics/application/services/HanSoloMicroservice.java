package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    public HanSoloMicroservice() {
        super("Han");
    }

    /**
     * initializing the microservice by subscribing to the specific events and broadcasts
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, (b) -> {
            Diary.getInstance().setHanSoloTerminate(System.currentTimeMillis());
            this.terminate();
        });
        subscribeEvent(AttackEvent.class, (e) -> {
            int numberOfResourcesNeeded = e.getSerialNumber().size();
            int acquiredResources = 0;
            while (!e.isDone()) {
                int resourceSerial = e.getSerialNumber().get(acquiredResources);
                Ewoks.getInstance().getResources().get(resourceSerial).acquire(); //acquires the resource
                acquiredResources++;
                if (acquiredResources == numberOfResourcesNeeded)
                    e.finishEvent();
            }
            try {
                Thread.sleep(e.getDuration()); //attacking with resource
                for (int serial : e.getSerialNumber()) {
                    Ewoks.getInstance().getResources().get(serial).release();
                }
                Diary.getInstance().incrementTotalAttack();
                complete(e,true);
                Diary.getInstance().setHanSoloFinish(System.currentTimeMillis());
            } catch (InterruptedException exception) {}
        });
    }
}


