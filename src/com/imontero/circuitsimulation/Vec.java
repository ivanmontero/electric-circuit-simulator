package com.imontero.circuitsimulation;

import java.awt.*;

public class Vec {
    public final int x;
    public final int y;

    public Vec(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec add(Vec o) {
        return new Vec(this.x + o.x, this.y + o.y);
    }

    public Vec sub(Vec o) {
        return new Vec(this.x - o.x, this.y - o.y);
    }

    public Vec norm() {
        double mag = this.magnitude();
        return new Vec((int) (this.x / mag), (int) (this.y / mag));
    }

    public Vec mid(Vec o) {
        return new Vec((o.x + this.x) / 2, (o.y + this.y) / 2);
    }

    public double magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public double dist(Vec o) {
        int dx = this.x - o.x;
        int dy = this.y - o.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static Vec of(int x, int y) {
        return new Vec(x, y);
    }

    public static Vec fromPoint(Point p) {
        return new Vec(p.x, p.y);
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}
