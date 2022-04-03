package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

import java.util.Comparator;
import java.util.List;

/**
 * this class represents an Attack object that is sent to HanSolo or C3PO
 */
public class AttackEvent implements Event<Boolean> {
    private int duration;
    private List<Integer> serialNumbers;
    private boolean status;

    /**
     * this constructor is used only for testing
     */
    public AttackEvent(){}

    /**
     * constructor
     * @param serialNumber the serial numbers of the resources
     * @param duration duration of the attack
     */
    public AttackEvent(List<Integer> serialNumber, int duration){
        this.duration = duration;
        this.serialNumbers = serialNumber;
        serialNumber.sort(Comparator.naturalOrder()); //sort the list inorder to prevent DeadBlocks
        status = false;
    }

    /**
     * getter of duration of the attack
     * @return
     */
    public int getDuration() {
        return duration;
    }

    /**
     * getter of the list of serialNumbers of the resources
     * @return
     */
    public List<Integer> getSerialNumber() {
        return serialNumbers;
    }

    /**
     * getter of the status of the attack event
     * @return true of the attack is finished else returns false
     */
    public boolean isDone() {
        return status;
    }

    /**
     * sets the event to be finished
     */
    public void finishEvent() {
        this.status = true;
    }
}
