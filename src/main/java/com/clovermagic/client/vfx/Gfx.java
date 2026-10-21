package com.clovermagic.client.vfx;

/**
 * Tiny code-driven 3D modeller: a transform stack plus shape primitives that emit coloured quads into a {@link Sink}.
 * No Minecraft classes are used here. Axes: +Y up, +Z forward ("aim" rotates +Z onto the caster's look direction).
 * Units are blocks. Colours are ARGB; vertex colours blend smoothly, which gives every shape a gradient for free.
 */
public final class Gfx {
    /** Surface material: GLOW = bright translucent energy, STONE = lit stone texture, ICE = translucent ice texture. */
    public enum Mat { GLOW, STONE, ICE }

    public interface Sink {
        void vertex(Mat mat, float x, float y, float z, float nx, float ny, float nz, float u, float v, int argb);
    }

    private final Sink sink;
    private double[] m = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0};   // row-major 3x4
    private final double[][] stack = new double[48][];
    private int sp = 0;
    private Mat mat = Mat.GLOW;
    /** Multiplies the alpha of everything drawn from now on. */
    public float alpha = 1f;
    public int quads = 0;

    public Gfx(Sink sink) {
        this.sink = sink;
    }

    // ------------------------------------------------------------------ transforms
    public void push() {
        stack[sp++] = m.clone();
    }

    public void pop() {
        m = stack[--sp];
    }

    public Gfx mat(Mat mat) {
        this.mat = mat;
        return this;
    }

    public void translate(double x, double y, double z) {
        m[3] += m[0] * x + m[1] * y + m[2] * z;
        m[7] += m[4] * x + m[5] * y + m[6] * z;
        m[11] += m[8] * x + m[9] * y + m[10] * z;
    }

    public void scale(double sx, double sy, double sz) {
        for (int r = 0; r < 3; r++) {
            m[r * 4] *= sx;
            m[r * 4 + 1] *= sy;
            m[r * 4 + 2] *= sz;
        }
    }

    public void scale(double s) {
        scale(s, s, s);
    }

    /** m.R = m.R * rot, rot given as 3x3 row-major. */
    private void mulRot(double a, double b, double c, double d, double e, double f, double g, double h, double i) {
        for (int r = 0; r < 3; r++) {
            double x = m[r * 4], y = m[r * 4 + 1], z = m[r * 4 + 2];
            m[r * 4] = x * a + y * d + z * g;
            m[r * 4 + 1] = x * b + y * e + z * h;
            m[r * 4 + 2] = x * c + y * f + z * i;
        }
    }

    public void rotX(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        mulRot(1, 0, 0, 0, c, -s, 0, s, c);
    }

    public void rotY(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        mulRot(c, 0, s, 0, 1, 0, -s, 0, c);
    }

    public void rotZ(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        mulRot(c, -s, 0, s, c, 0, 0, 0, 1);
    }

    /** Turns local +Z onto the caster's look direction (Minecraft yaw/pitch in degrees). */
    public void aim(Ctx c) {
        rotY(-Math.toRadians(c.yaw));
        rotX(Math.toRadians(c.pitch));
    }

    /** Aims local +Z from point a to point b (used for arcs between two points). */
    public void lookFromTo(double ax, double ay, double az, double bx, double by, double bz) {
        translate(ax, ay, az);
        double dx = bx - ax, dy = by - ay, dz = bz - az;
        double h = Math.sqrt(dx * dx + dz * dz);
        rotY(Math.atan2(dx, dz));
        rotX(-Math.atan2(dy, h));
    }

    // ------------------------------------------------------------------ raw quad
    private final double[] t = new double[12];

    public void quad(double ax, double ay, double az, double bx, double by, double bz,
                     double cx, double cy, double cz, double dx, double dy, double dz,
                     int ca, int cb, int cc, int cd) {
        put(0, ax, ay, az);
        put(3, bx, by, bz);
        put(6, cx, cy, cz);
        put(9, dx, dy, dz);
        double ux = t[6] - t[0], uy = t[7] - t[1], uz = t[8] - t[2];
        double vx = t[9] - t[3], vy = t[10] - t[4], vz = t[11] - t[5];
        double nx = uy * vz - uz * vy, ny = uz * vx - ux * vz, nz = ux * vy - uy * vx;
        double l = Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (l < 1e-9) {
            nx = 0;
            ny = 1;
            nz = 0;
        } else {
            nx /= l;
            ny /= l;
            nz /= l;
        }
        int[] cols = {ca, cb, cc, cd};
        float[] us = {0, 1, 1, 0};
        float[] vs = {0, 0, 1, 1};
        for (int i = 0; i < 4; i++) {
            int col = alpha >= 0.999f ? cols[i] : Col.alpha(cols[i], alpha);
            if ((col >>> 24) == 0) col &= 0x00FFFFFF; // keep fully transparent quads cheap but valid
            sink.vertex(mat, (float) t[i * 3], (float) t[i * 3 + 1], (float) t[i * 3 + 2],
                    (float) nx, (float) ny, (float) nz, us[i], vs[i], col);
        }
        quads++;
    }

    private void put(int i, double x, double y, double z) {
        t[i] = m[0] * x + m[1] * y + m[2] * z + m[3];
        t[i + 1] = m[4] * x + m[5] * y + m[6] * z + m[7];
        t[i + 2] = m[8] * x + m[9] * y + m[10] * z + m[11];
    }

    /** Triangle (a quad with a repeated corner). */
    public void tri(double ax, double ay, double az, double bx, double by, double bz, double cx, double cy, double cz,
                    int ca, int cb, int cc) {
        quad(ax, ay, az, bx, by, bz, cx, cy, cz, cx, cy, cz, ca, cb, cc, cc);
    }

    // ------------------------------------------------------------------ boxes
    public void box(double x0, double y0, double z0, double x1, double y1, double z1, int c) {
        boxG(x0, y0, z0, x1, y1, z1, c, c);
    }

    /** Box with a colour gradient from cLow (y0) to cHigh (y1). */
    public void boxG(double x0, double y0, double z0, double x1, double y1, double z1, int cLow, int cHigh) {
        quad(x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1, cHigh, cHigh, cHigh, cHigh);   // top
        quad(x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0, cLow, cLow, cLow, cLow);       // bottom
        quad(x1, y0, z0, x1, y0, z1, x1, y1, z1, x1, y1, z0, cLow, cLow, cHigh, cHigh);     // +x
        quad(x0, y0, z1, x0, y0, z0, x0, y1, z0, x0, y1, z1, cLow, cLow, cHigh, cHigh);     // -x
        quad(x1, y0, z1, x0, y0, z1, x0, y1, z1, x1, y1, z1, cLow, cLow, cHigh, cHigh);     // +z
        quad(x0, y0, z0, x1, y0, z0, x1, y1, z0, x0, y1, z0, cLow, cLow, cHigh, cHigh);     // -z
    }

    public void cube(double cx, double cy, double cz, double s, int c) {
        double h = s / 2;
        box(cx - h, cy - h, cz - h, cx + h, cy + h, cz + h, c);
    }

    // ------------------------------------------------------------------ round things
    /** Frustum along +Y from y=0 (radius r0, colour c0) to y=h (radius r1, colour c1). caps: 1 bottom, 2 top, 3 both. */
    public void frustumY(double h, double r0, double r1, int segs, int c0, int c1, int caps) {
        for (int i = 0; i < segs; i++) {
            double a0 = Math.PI * 2 * i / segs, a1 = Math.PI * 2 * (i + 1) / segs;
            double s0 = Math.sin(a0), co0 = Math.cos(a0), s1 = Math.sin(a1), co1 = Math.cos(a1);
            quad(r0 * co0, 0, r0 * s0, r0 * co1, 0, r0 * s1, r1 * co1, h, r1 * s1, r1 * co0, h, r1 * s0, c0, c0, c1, c1);
            if ((caps & 1) != 0) {
                quad(0, 0, 0, r0 * co1, 0, r0 * s1, r0 * co0, 0, r0 * s0, 0, 0, 0, c0, c0, c0, c0);
            }
            if ((caps & 2) != 0 && r1 > 0) {
                quad(0, h, 0, r1 * co0, h, r1 * s0, r1 * co1, h, r1 * s1, 0, h, 0, c1, c1, c1, c1);
            }
        }
    }

    public void coneY(double h, double r, int segs, int cBase, int cTip) {
        frustumY(h, r, 0, segs, cBase, cTip, 0);
    }

    /** Frustum along +Z from z=0 to z=len. */
    public void frustumZ(double len, double r0, double r1, int segs, int c0, int c1, int caps) {
        push();
        rotX(Math.PI / 2);
        frustumY(len, r0, r1, segs, c0, c1, caps);
        pop();
    }

    /** Cone along +Z: base at z=0, tip at z=len. */
    public void coneZ(double len, double r, int segs, int cBase, int cTip) {
        frustumZ(len, r, 0, segs, cBase, cTip, 0);
    }

    /** Flat disc in the XZ plane at y=0. */
    public void disc(double r, int segs, int cCenter, int cEdge) {
        for (int i = 0; i < segs; i++) {
            double a0 = Math.PI * 2 * i / segs, a1 = Math.PI * 2 * (i + 1) / segs;
            quad(0, 0, 0, r * Math.cos(a0), 0, r * Math.sin(a0), r * Math.cos(a1), 0, r * Math.sin(a1), 0, 0, 0,
                    cCenter, cEdge, cEdge, cCenter);
        }
    }

    /** Flat annulus in the XZ plane at y=0. */
    public void ring(double r0, double r1, int segs, int c0, int c1) {
        for (int i = 0; i < segs; i++) {
            double a0 = Math.PI * 2 * i / segs, a1 = Math.PI * 2 * (i + 1) / segs;
            quad(r0 * Math.cos(a0), 0, r0 * Math.sin(a0), r1 * Math.cos(a0), 0, r1 * Math.sin(a0),
                    r1 * Math.cos(a1), 0, r1 * Math.sin(a1), r0 * Math.cos(a1), 0, r0 * Math.sin(a1), c0, c1, c1, c0);
        }
    }

    /** Donut around the Y axis (major radius R, tube radius r). */
    public void torus(double bigR, double r, int nMaj, int nMin, int c) {
        for (int i = 0; i < nMaj; i++) {
            double a0 = Math.PI * 2 * i / nMaj, a1 = Math.PI * 2 * (i + 1) / nMaj;
            for (int j = 0; j < nMin; j++) {
                double b0 = Math.PI * 2 * j / nMin, b1 = Math.PI * 2 * (j + 1) / nMin;
                double[] p00 = tp(bigR, r, a0, b0), p10 = tp(bigR, r, a1, b0), p11 = tp(bigR, r, a1, b1), p01 = tp(bigR, r, a0, b1);
                quad(p00[0], p00[1], p00[2], p10[0], p10[1], p10[2], p11[0], p11[1], p11[2], p01[0], p01[1], p01[2], c, c, c, c);
            }
        }
    }

    private static double[] tp(double bigR, double r, double a, double b) {
        double rad = bigR + r * Math.cos(b);
        return new double[] {rad * Math.cos(a), r * Math.sin(b), rad * Math.sin(a)};
    }

    /** UV sphere, colour gradient from cLow (south pole) to cHigh (north pole). */
    public void sphere(double r, int lat, int lon, int cLow, int cHigh) {
        for (int i = 0; i < lat; i++) {
            double p0 = -Math.PI / 2 + Math.PI * i / lat, p1 = -Math.PI / 2 + Math.PI * (i + 1) / lat;
            int k0 = Col.lerp(cLow, cHigh, (float) i / lat), k1 = Col.lerp(cLow, cHigh, (float) (i + 1) / lat);
            for (int j = 0; j < lon; j++) {
                double a0 = Math.PI * 2 * j / lon, a1 = Math.PI * 2 * (j + 1) / lon;
                quad(r * Math.cos(p0) * Math.cos(a0), r * Math.sin(p0), r * Math.cos(p0) * Math.sin(a0),
                        r * Math.cos(p0) * Math.cos(a1), r * Math.sin(p0), r * Math.cos(p0) * Math.sin(a1),
                        r * Math.cos(p1) * Math.cos(a1), r * Math.sin(p1), r * Math.cos(p1) * Math.sin(a1),
                        r * Math.cos(p1) * Math.cos(a0), r * Math.sin(p1), r * Math.cos(p1) * Math.sin(a0),
                        k0, k0, k1, k1);
            }
        }
    }

    /** Lumpy low-poly rock, radius varies per vertex from a seed. */
    public void rock(double r, long seed, int c) {
        int lat = 4, lon = 7;
        double[][] rad = new double[lat + 1][lon];
        for (int i = 0; i <= lat; i++) {
            for (int j = 0; j < lon; j++) rad[i][j] = r * (0.72 + 0.5 * Rnd.h(seed, i * 16 + j));
        }
        for (int i = 0; i < lat; i++) {
            double p0 = -Math.PI / 2 + Math.PI * i / lat, p1 = -Math.PI / 2 + Math.PI * (i + 1) / lat;
            for (int j = 0; j < lon; j++) {
                int jn = (j + 1) % lon;
                double a0 = Math.PI * 2 * j / lon, a1 = Math.PI * 2 * (j + 1) / lon;
                double ra = i == 0 ? rad[0][0] : rad[i][j], rb = i == 0 ? rad[0][0] : rad[i][jn];
                double rc = i + 1 == lat ? rad[lat][0] : rad[i + 1][jn], rd = i + 1 == lat ? rad[lat][0] : rad[i + 1][j];
                int shade = Col.lerp(c, (c & 0xFF000000) | 0x000000, (float) (0.35 * Rnd.h(seed, 200 + i * 8 + j)));
                quad(ra * Math.cos(p0) * Math.cos(a0), ra * Math.sin(p0), ra * Math.cos(p0) * Math.sin(a0),
                        rb * Math.cos(p0) * Math.cos(a1), rb * Math.sin(p0), rb * Math.cos(p0) * Math.sin(a1),
                        rc * Math.cos(p1) * Math.cos(a1), rc * Math.sin(p1), rc * Math.cos(p1) * Math.sin(a1),
                        rd * Math.cos(p1) * Math.cos(a0), rd * Math.sin(p1), rd * Math.cos(p1) * Math.sin(a0),
                        shade, shade, shade, shade);
            }
        }
    }

    /** Crystal along +Y: hex body 0..0.6h, pointed top to h, short point below. */
    public void crystal(double h, double r, int segs, int cBase, int cTip) {
        int mid = Col.lerp(cBase, cTip, 0.6f);
        push();
        frustumY(h * 0.6, r, r, segs, cBase, mid, 0);
        translate(0, h * 0.6, 0);
        frustumY(h * 0.4, r, 0, segs, mid, cTip, 0);
        pop();
        push();
        rotX(Math.PI);
        frustumY(h * 0.15, r, 0, segs, cBase, cBase, 0);
        pop();
    }

    // ------------------------------------------------------------------ lines and curves
    /** Square rod from a to b. */
    public void rod(double ax, double ay, double az, double bx, double by, double bz, double w0, double w1, int c0, int c1) {
        double dx = bx - ax, dy = by - ay, dz = bz - az;
        double l = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (l < 1e-6) return;
        dx /= l;
        dy /= l;
        dz /= l;
        double ux, uy, uz;
        if (Math.abs(dy) < 0.9) {
            ux = dy * 0 - dz * 1;
            uy = dz * 0 - dx * 0;
            uz = dx * 1 - dy * 0;           // d x up(0,1,0) = (-dz, 0, dx)
        } else {
            ux = 1;
            uy = 0;
            uz = 0;
        }
        double ul = Math.sqrt(ux * ux + uy * uy + uz * uz);
        ux /= ul;
        uy /= ul;
        uz /= ul;
        double vx = dy * uz - dz * uy, vy = dz * ux - dx * uz, vz = dx * uy - dy * ux;
        double[][] a = corners(ax, ay, az, ux, uy, uz, vx, vy, vz, w0);
        double[][] b = corners(bx, by, bz, ux, uy, uz, vx, vy, vz, w1);
        for (int i = 0; i < 4; i++) {
            int n = (i + 1) % 4;
            quad(a[i][0], a[i][1], a[i][2], a[n][0], a[n][1], a[n][2], b[n][0], b[n][1], b[n][2], b[i][0], b[i][1], b[i][2],
                    c0, c0, c1, c1);
        }
    }

    private static double[][] corners(double x, double y, double z, double ux, double uy, double uz,
                                      double vx, double vy, double vz, double w) {
        return new double[][] {
                {x + (ux + vx) * w, y + (uy + vy) * w, z + (uz + vz) * w},
                {x + (-ux + vx) * w, y + (-uy + vy) * w, z + (-uz + vz) * w},
                {x + (-ux - vx) * w, y + (-uy - vy) * w, z + (-uz - vz) * w},
                {x + (ux - vx) * w, y + (uy - vy) * w, z + (uz - vz) * w}};
    }

    /**
     * Crescent blade in the XZ plane, convex side toward +Z, centred on the origin of its circle.
     * sweep = total arc angle (radians), width = max width of the crescent, thick = max half-thickness.
     */
    public void arcBlade(double radius, double width, double sweep, int segs, double thick, int cOuter, int cInner) {
        int mid = Col.lerp(cOuter, cInner, 0.5f);
        double[][] sec = new double[segs + 1][];
        for (int i = 0; i <= segs; i++) {
            double f = (double) i / segs;
            double taper = Math.pow(Math.sin(Math.PI * f), 0.7);
            double th = -sweep / 2 + sweep * f;
            double w = width * taper, tk = thick * taper;
            sec[i] = new double[] {Math.sin(th), Math.cos(th), w, tk};
        }
        for (int i = 0; i < segs; i++) {
            double[] s0 = sec[i], s1 = sec[i + 1];
            double[][] a = section(s0, radius), b = section(s1, radius);
            int[] cols = {cOuter, mid, cInner, mid};
            for (int k = 0; k < 4; k++) {
                int n = (k + 1) % 4;
                quad(a[k][0], a[k][1], a[k][2], a[n][0], a[n][1], a[n][2], b[n][0], b[n][1], b[n][2], b[k][0], b[k][1], b[k][2],
                        cols[k], cols[n], cols[n], cols[k]);
            }
        }
    }

    private static double[][] section(double[] s, double radius) {
        double sn = s[0], cs = s[1], w = s[2], tk = s[3];
        double rOut = radius, rIn = radius - w, rMid = radius - w / 2;
        return new double[][] {
                {sn * rOut, 0, cs * rOut}, {sn * rMid, tk, cs * rMid}, {sn * rIn, 0, cs * rIn}, {sn * rMid, -tk, cs * rMid}};
    }

    /** Jagged lightning bolt from a to b: a glow rod plus a bright core rod per segment. */
    public void bolt(double ax, double ay, double az, double bx, double by, double bz, int segs, double jag, long seed,
                     double w, int cCore, int cGlow) {
        double dx = bx - ax, dy = by - ay, dz = bz - az;
        double l = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (l < 1e-6) return;
        double nx = dx / l, ny = dy / l, nz = dz / l;
        double ux, uy, uz;
        if (Math.abs(ny) < 0.9) {
            ux = -nz;
            uy = 0;
            uz = nx;
        } else {
            ux = 1;
            uy = 0;
            uz = 0;
        }
        double ul = Math.sqrt(ux * ux + uy * uy + uz * uz);
        ux /= ul;
        uy /= ul;
        uz /= ul;
        double vx = ny * uz - nz * uy, vy = nz * ux - nx * uz, vz = nx * uy - ny * ux;
        double px = ax, py = ay, pz = az;
        for (int i = 1; i <= segs; i++) {
            double f = (double) i / segs;
            double ex = ax + dx * f, ey = ay + dy * f, ez = az + dz * f;
            if (i < segs) {
                double j1 = Rnd.s(seed, i * 2) * jag, j2 = Rnd.s(seed, i * 2 + 1) * jag;
                ex += ux * j1 + vx * j2;
                ey += uy * j1 + vy * j2;
                ez += uz * j1 + vz * j2;
            }
            rod(px, py, pz, ex, ey, ez, w * 2.4, w * 2.4, cGlow, cGlow);
            rod(px, py, pz, ex, ey, ez, w, w, cCore, cCore);
            px = ex;
            py = ey;
            pz = ez;
        }
    }
}
