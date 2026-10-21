package com.clovermagic.client.vfx;

/** Everything a model recipe needs to know about the effect it is drawing. Filled in by the renderer. */
public final class Ctx {
    /** Age in ticks including the partial tick. */
    public float t;
    /** Total lifetime in ticks. */
    public int life = 20;
    /** Main size: beam length, area radius or projectile scale (depends on the effect). */
    public float len = 1f;
    /** Extra packed data (ground heights for Earth Spike). */
    public long aux;
    /** Stable per-effect seed (entity id). */
    public long seed;
    /** Look direction at cast time, in Minecraft degrees (yaw 0 = south, pitch + = down). */
    public float yaw, pitch;
    /** Bounding box of the entity this effect follows (target or caster). */
    public float fw = 0.6f, fh = 1.8f;
    /** Body yaw of the followed entity, degrees. */
    public float bodyYaw;
    /** True when the camera sits inside the followed entity (shells get see-through). */
    public boolean firstPerson;

    public float p() {
        return life <= 0 ? 1f : Math.max(0f, Math.min(1f, t / life));
    }

    /** 0..1 ramp in over the first {@code in} ticks and out over the last {@code out} ticks. */
    public float fade(float in, float out) {
        float a = in <= 0 ? 1f : Math.min(1f, t / in);
        float b = out <= 0 ? 1f : Math.min(1f, (life - t) / out);
        return Math.max(0f, Math.min(a, b));
    }

    /** Alpha multiplier for big shells that would block the view from inside. */
    public float shell() {
        return firstPerson ? 0.18f : 1f;
    }

    /** Ground height offset (blocks, relative to the effect origin) stored for slot i, or NaN for "no ground". */
    public double ground(int i) {
        int b = (int) ((aux >> (8 * i)) & 0xFF);
        if (b > 127) b -= 256;
        if (b == -128) return Double.NaN;
        return b / 8.0;
    }
}
