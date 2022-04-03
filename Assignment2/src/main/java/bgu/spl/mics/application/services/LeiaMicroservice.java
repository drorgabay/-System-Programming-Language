package bgu.spl.mics.application.services;

import java.util.concurrent.LinkedBlockingQueue;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.SendAttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
    private Attack[] attacks;
    private LinkedBlockingQueue<Future<Boolean>> futures;
    private int numberOfAttacks;


    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
        futures = new LinkedBlockingQueue<>();
        numberOfAttacks = attacks.length;

    }
    /**
     * initializing the microservice by subscribing to the specific events and broadcasts
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, (b) -> {
            terminate();
            Diary.getInstance().setLeiaTerminate(System.currentTimeMillis());
        });
        subscribeEvent(SendAttackEvent.class, (se) -> {
            for (Attack attack : attacks) {
                AttackEvent e = new AttackEvent(attack.getSerials(), attack.getDuration());
                Future<Boolean> f = sendEvent(e);
                while (f == null) {
                    f = sendEvent(e);
                }
                futures.add(f);
            }
            while (numberOfAttacks != 0) {
                for (Future<Boolean> f : futures) {
                    if (f.isDone()) {
                        futures.poll();
                        numberOfAttacks--;
                    }
                }
            }
            sendEvent(new DeactivationEvent());
        });
        sendEvent(new SendAttackEvent());
    }
}
