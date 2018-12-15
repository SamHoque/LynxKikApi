package net.lynx.client.utils;

/**
 * Source: kik.core.util.x;
 */

public class KikTimestampUtils {
    public static synchronized long getCurrentTimestamp() {
        long currentTimeMillis;
        synchronized (KikTimestampUtils.class) {
            currentTimeMillis = System.currentTimeMillis();
        }
        return currentTimeMillis;
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
}

