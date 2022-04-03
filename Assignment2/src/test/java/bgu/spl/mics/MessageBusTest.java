package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.application.services.R2D2Microservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {
    private MessageBus messageBus;
    private MicroService c3po;
    private MicroService hanSolo;


    @BeforeEach
    void setUp() { //register is checked here because we check everytime a new test is running
        messageBus = MessageBusImpl.getInstance();
        c3po = new C3POMicroservice();
        hanSolo = new HanSoloMicroservice();
        messageBus.register(c3po);
        messageBus.register(hanSolo);
    }

    @AfterEach
    void tearDown() {
        messageBus.unregister(c3po);
        messageBus.unregister(hanSolo);
    }

    @Test
    void getInstance() {
        MessageBus messageBus1 = MessageBusImpl.getInstance();
        assertEquals(messageBus, messageBus1);
    }

    @Test
    void subscribeEvent() throws InterruptedException { //here we test the register function on the way
        try {
            AttackEvent e = new AttackEvent();
            c3po.subscribeEvent(e.getClass(),(c)->{}); //to add event to the queue
            messageBus.subscribeEvent(e.getClass(), c3po); // now c3po can receive attack events
            Future<Boolean> f = messageBus.sendEvent(e);
            Message m = messageBus.awaitMessage(c3po);
            f.resolve(true);
            f.get();
            assertTrue(f.isDone());
            //check that deactivation event wont get a result in c3po
            DeactivationEvent de = new DeactivationEvent();
            MicroService x = new R2D2Microservice(1);
            messageBus.register(x);
            messageBus.subscribeEvent(de.getClass(),x);
            Future<Boolean> f0 = messageBus.sendEvent(e);
            Future<Boolean> f1 = messageBus.sendEvent(de);
            Message m1 = messageBus.awaitMessage(c3po);
            f0.resolve(true);
            f1.resolve(false);
            assertNotEquals(f0.get(),f1.get());
            messageBus.unregister(x);
        } catch (InterruptedException e) {
            fail(e.getMessage());
       }
    }

    @Test
    void subscribeBroadcast() {
        try {
            TerminateBroadcast bro = new TerminateBroadcast();
            messageBus.subscribeBroadcast(bro.getClass(), c3po);
            messageBus.subscribeBroadcast(bro.getClass(), hanSolo);
            messageBus.sendBroadcast(bro);
            Message m = messageBus.awaitMessage(c3po);
            Message m1 = messageBus.awaitMessage(hanSolo);
            assertSame(bro, m);
            assertSame(bro, m1);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }


    }

    @Test
    void testComplete() throws InterruptedException {
        AttackEvent e = new AttackEvent();
        messageBus.subscribeEvent(e.getClass(),c3po);
        Future<Boolean> f = messageBus.sendEvent(e);
        messageBus.complete(e, true);
        assertEquals(f.get(), true);
    }

    @Test
    void sendBroadcast() {
        try {
            TerminateBroadcast bro = new TerminateBroadcast();
            c3po.subscribeBroadcast(bro.getClass(), (c -> {}));
            hanSolo.subscribeBroadcast(bro.getClass(), (c -> {}));
            messageBus.sendBroadcast(bro);
            Message m = messageBus.awaitMessage(c3po);
            Message m1 = messageBus.awaitMessage(hanSolo);
            assertSame(bro, m);
            assertSame(bro, m1);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }

    }

    @Test
    void sendEvent() throws InterruptedException {

        try {
            AttackEvent e = new AttackEvent(); //attack event for c3po
            messageBus.subscribeEvent(e.getClass(),c3po);
            Future<Boolean> f = messageBus.sendEvent(e); // send event to the message bus for to c3po
            Message m = messageBus.awaitMessage(c3po); // await message to c3po //throws exception
            f.resolve(true);
            f.get();
            assertTrue(f.isDone()); // check the events result
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void register() { //no need to be tested - a part of other test - written in  comments
    }


    @Test
    void unregister() { // no need to be tested
    }

    @Test
    void awaitMessage() throws InterruptedException { //test assuming that the queue is not empty
        try {
            AttackEvent e = new AttackEvent();
            messageBus.subscribeEvent(e.getClass(), hanSolo);
            Future<Boolean> f = messageBus.sendEvent(e);
            Message message = messageBus.awaitMessage(hanSolo);
            assertSame(message, e);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }
}