package com.example.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Clock {
    private static long time = 0; // seconds since Unix epoch
    private static int tickInterval = 1800; // seconds (30 minutes)

    public static void setTickInterval(int t) {
        tickInterval = t;
    }

    public static long getTime() {
        return time;
    }

    public static long tick() {
        time += tickInterval;
        return time;
    }

    public static void setTime(long newTime) {
        time = newTime;
    }

    public static void setTime(LocalDateTime newTime) {
        time = newTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    public static LocalDateTime getDateTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.systemDefault());
    }
}
