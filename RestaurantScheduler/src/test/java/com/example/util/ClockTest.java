package com.example.util;

import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ClockTest {

    @BeforeEach
    void resetClock() {
        Clock.setTime(0);
        Clock.setTickInterval(1800);
    }

    @Test
    void testTickAdvancesByInterval() {
        long t0 = Clock.getTime();
        long t1 = Clock.tick();
        assertEquals(t0 + 1800, t1);
    }

    @Test
    void testSetDateTimeConversion() {
        LocalDateTime dt = LocalDateTime.of(1970,1,1,1,0);
        Clock.setTime(dt);
        assertEquals(3600, Clock.getTime());
        assertEquals(dt, Clock.getDateTime());
    }

    @Test
    void testCustomInterval() {
        Clock.setTickInterval(60);
        long t = Clock.tick();
        assertEquals(60, t);
    }
}
