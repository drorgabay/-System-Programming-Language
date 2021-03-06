package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;


public class FutureTest {

    private Future<String> future;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testResolve() throws InterruptedException {
        String str = "someResult";
        future.resolve(str);
        assertTrue(future.isDone());
        assertTrue(str.equals(future.get()));
    }

    @Test
    void testGet() throws InterruptedException {
        assertFalse(future.isDone());
        future.resolve("hello");
        future.get();
        assertTrue(future.isDone());
    }

    @Test
    void testIsDone() {
        assertFalse(future.isDone());
        future.resolve("xxx");
        assertTrue(future.isDone());
    }

    @Test
    void testGetTimeOut() throws InterruptedException {
        assertFalse(future.isDone());
        future.get(100,TimeUnit.MILLISECONDS);
        assertFalse(future.isDone());
        future.resolve("xxx");
        assertEquals(future.get(100,TimeUnit.MILLISECONDS),"xxx");
    }
}
