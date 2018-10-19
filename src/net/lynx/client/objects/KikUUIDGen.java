package net.lynx.client.objects;

import java.util.UUID;

public class KikUUIDGen {
    private static int subStringUUID(long j, int i) {
        return i > 32 ? ((int) ((j >> 32) & ((long) (1 << i)))) >> i : ((int) (j & ((long) (1 << i)))) >> i;
    }

    public static synchronized String getKikUUID() {
        String uuid = null;
        synchronized (KikUUIDGen.class) {
            try {
                UUID randomUUID = UUID.randomUUID();
                long leastSignificantBits = randomUUID.getLeastSignificantBits();
                long mostSignificantBits = randomUUID.getMostSignificantBits();
                int i = (int) ((mostSignificantBits & -1152921504606846976L) >>> 62);
                int[][] r7 = new int[4][];
                int i2 = 0;
                r7[0] = new int[]{3, 6};
                int i3 = 1;
                r7[1] = new int[]{2, 5};
                r7[2] = new int[]{7, 1};
                r7[3] = new int[]{9, 5};
                int i4 = r7[i][0];
                i = r7[i][1];
                long j = (((mostSignificantBits & -16777216) >>> 22) ^ ((mostSignificantBits & 16711680) >>> 16)) ^ ((mostSignificantBits & 65280) >>> 8);
                i = (subStringUUID(mostSignificantBits, i) + 1) | (subStringUUID(mostSignificantBits, i4) << 1);
                while (i2 < 6) {
                    i3 = (i3 + (i * 7)) % 60;
                    int i5 = i3 + 2;
                    leastSignificantBits = (((long) subStringUUID(j, i2)) << i5) | (leastSignificantBits & (~(1 << i5)));
                    i2++;
                }
                uuid = new UUID(mostSignificantBits, leastSignificantBits).toString();
            } catch (Throwable th) {
            }
        }
        return uuid;
    }
}
