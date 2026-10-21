package com.clovermagic.client.vfx;

/** Deterministic hash noise, so a model looks the same every frame (no flicker from new Random each frame). */
public final class Rnd {
    private Rnd() {
    }

    /** Value in [0, 1) from a seed and an index. */
    public static double h(long seed, int i) {
        long z = seed * 0x9E3779B97F4A7C15L + (long) i * 0xBF58476D1CE4E5B9L + 0x632BE59BD9B4E019L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        z = z ^ (z >>> 31);
        return (z >>> 11) * (1.0 / (1L << 53));
    }

    /** Value in [-1, 1). */
    public static double s(long seed, int i) {
        return h(seed, i) * 2.0 - 1.0;
    }
}
