package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {

    int serialNumber;
    boolean available;

    public Ewok(int serialNumber) {
        this.serialNumber = serialNumber;
        available = true;
    }

    /**
     * Acquires an Ewok
     */
    public synchronized void acquire() throws InterruptedException { //sync this method because HanSolo and C3PO use this shared resources
        while (!available)
            wait();
        available = false;
    }

    /**
     * release an Ewok
     */
    public synchronized void release() { //sync this method because HanSolo and C3PO use this shared resources and notifies when released
        available = true;
        notifyAll();
    }
//-----------------------------------ADDED FOR TESTS USAGE ONLY----------------------------------

    /**
     * @return the status of available member
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * sets the available status to a new state
     *
     * @param available
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }
}
