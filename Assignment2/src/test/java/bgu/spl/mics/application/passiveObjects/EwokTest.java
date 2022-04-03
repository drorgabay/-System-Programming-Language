package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {
    private Ewok ewok;
    @BeforeEach
    void setUp() {
        ewok = new Ewok(1);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testAcquire() throws InterruptedException {
        ewok.setAvailable(true);
        ewok.acquire();
        assertFalse(ewok.isAvailable());
    }

    @Test
    void release() {
        ewok.setAvailable(false);
        ewok.release();
        assertTrue(ewok.isAvailable());
    }
}