package net.lynx.client.utils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Source: kik.core.util.x;
 */

public class KikTimestampUtils {
    private static volatile long a = 0;
    private static volatile long b = -1;

    public static double e(long j) {
        return ((double) j) / 1000.0d;
    }

    private static synchronized void f(long j) {
        synchronized (KikTimestampUtils.class) {
            a = j;
            b = System.currentTimeMillis();
        }
    }

    public static synchronized void a(long j) {
        synchronized (KikTimestampUtils.class) {
            f(j - System.currentTimeMillis());
        }
    }

    public static synchronized long a() {
        long j;
        synchronized (KikTimestampUtils.class) {
            j = a;
        }
        return j;
    }

    public static synchronized long b() {
        long currentTimeMillis;
        synchronized (KikTimestampUtils.class) {
            currentTimeMillis = System.currentTimeMillis() + a;
        }
        return currentTimeMillis;
    }

    public static long b(long j) {
        return j - a;
    }

    public static long c() {
        return System.currentTimeMillis();
    }

    public static long a(Random random, long j) {
        return (long) (((random.nextDouble() * 0.25d) + 1.0d) * ((double) j));
    }

    public static synchronized long c(long j) {
        synchronized (KikTimestampUtils.class) {
            long j2 = ((((j & 65280) >> 8) ^ ((j & 16711680) >> 16)) ^ ((j & -16777216) >> 24)) & 30;
            long j3 = (j & 224) >> 5;
            long j4 = j & -255;
            if (j2 % 4 == 0) {
                j3 = (j3 / 3) * 3;
            } else {
                j3 = (j3 / 2) * 2;
            }
            j = (j4 | (j3 << 5)) | j2;
        }
        return j;
    }

    public static synchronized long d() {
        long j;
        synchronized (KikTimestampUtils.class) {
            j = b;
        }
        return j;
    }

    public static long d(long j) {
        return TimeUnit.MILLISECONDS.toDays(b() - j);
    }
}

