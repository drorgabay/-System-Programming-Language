package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    private static class SingletonMessageBus { //singleton thread-safe
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> microserviceMessages;
    private ConcurrentHashMap<Class, LinkedBlockingQueue<MicroService>> subEvents;
    private ConcurrentHashMap<Class, LinkedBlockingQueue<MicroService>> subBroadcasts;
    private ConcurrentHashMap<Event, Future> needsToBeResolved;
    private Object lockSendEvent; //lock for the sendEvent method
    private Object lockSubscribeEvent; // lock to the subscribeEvent method
    private Object lockSubscribeBroadcast; // lock for thr subscribeBroadcast method


    // constructor for singleton
    private MessageBusImpl() {
        microserviceMessages = new ConcurrentHashMap<>();
        subEvents = new ConcurrentHashMap<>();
        needsToBeResolved = new ConcurrentHashMap<>();
        subBroadcasts = new ConcurrentHashMap<>();
        lockSendEvent = new Object();
        lockSubscribeEvent = new Object();
        lockSubscribeBroadcast = new Object();
    }

    public static MessageBusImpl getInstance() {
        return SingletonMessageBus.instance;
    }


    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        synchronized (lockSubscribeEvent) { //sync this method because there could be a scenario: 2 threads are called
            if (!subEvents.containsKey(type)) { // and they could add at this "if" 2 different queues with the same purpose
                subEvents.put(type, new LinkedBlockingQueue<>()); //therefore  we lock only the method and not the whole map of events
            }
            if (!subEvents.get(type).contains(m))
                subEvents.get(type).add(m);
            lockSubscribeEvent.notifyAll();
        }
    }


    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        synchronized (lockSubscribeBroadcast) {//sync this method because there could be a scenario: 2 threads are called
            if (!subBroadcasts.containsKey(type)) {// and they could add at this "if" 2 different queues with the same purpose
                subBroadcasts.put(type, new LinkedBlockingQueue<>());//therefore  we lock only the method and not the whole map of broadcasts
            }
            if (!subBroadcasts.get(type).contains(m))
                subBroadcasts.get(type).add(m);
            lockSubscribeBroadcast.notifyAll();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result) {
        synchronized (needsToBeResolved.get(e)) { //sync the Future<T> object from the map because Leia is using them two in the flow of the runtime
            needsToBeResolved.get(e).resolve(result);
            needsToBeResolved.get(e).notifyAll();
        }
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        LinkedBlockingQueue<MicroService> micros = subBroadcasts.get(b.getClass());
        for (MicroService micro : micros) {
            synchronized (microserviceMessages.get(micro)) { //sync the queue of messages of the microservice because :
                microserviceMessages.get(micro).add(b); // there could be a scenario where a microservice is awaiting message and
                microserviceMessages.get(micro).notifyAll(); // needs to be notified that he has a new one if he waits.
            }
        }
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        synchronized (lockSendEvent) { //locking the whole method for a scenario where 2 threads send the same event
            if (!subEvents.containsKey(e.getClass()) || subEvents.get(e.getClass()).isEmpty())
                return null;
            Future<T> future = new Future<>();
            needsToBeResolved.put(e, future);
            LinkedBlockingQueue<MicroService> microEvents = subEvents.get(e.getClass());
            MicroService micro = microEvents.poll();
            synchronized (microserviceMessages.get(micro)) {
                LinkedBlockingQueue<Message> eventMessage = microserviceMessages.get(micro);
                eventMessage.add(e);
                microEvents.add(micro);
                microserviceMessages.get(micro).notifyAll();
            }
            lockSendEvent.notifyAll();
            return future;
        }
    }

    @Override
    public void register(MicroService m) { //register in init -> we were told that there will not be 2 Threads of the same (e.g 2 Threads of HanSolo)
        if (!microserviceMessages.containsKey(m)) { //therefore we do not need sync here
            microserviceMessages.put(m, new LinkedBlockingQueue<>());
        }
    }

    @Override
    public void unregister(MicroService m) { //no need to sync because at this point all queues are empty and all are terminated.
        microserviceMessages.remove(m);
        for (LinkedBlockingQueue<MicroService> qOfm : subEvents.values()) {
            qOfm.remove(m);
        }
        for (LinkedBlockingQueue<MicroService> qOfm : subBroadcasts.values()) {
            qOfm.remove(m);
        }
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        if (!microserviceMessages.containsKey(m))
            throw new InterruptedException(m.getName() + " is not registered to MessageBus");
        LinkedBlockingQueue<Message> messagesOfm = microserviceMessages.get(m);
        Message msg;
        synchronized (messagesOfm) { // sync the message queue of the "m" microservice to notify that it has a new message
            while (messagesOfm.isEmpty()) // and to prevent a scenario where poll() and add() from 2 different thread
                messagesOfm.wait(); // from sendEvent/sendBroadcast use the message queue of the microservice
            msg = messagesOfm.poll();
            messagesOfm.notifyAll();
        }
        return msg;
    }
}
