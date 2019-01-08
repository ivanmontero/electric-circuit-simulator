package com.imontero.circuitsimulation;

import com.imontero.circuit.Circuit;
import com.imontero.circuit.CircuitElement;
import com.imontero.circuit.CircuitElementType;
import com.imontero.circuit.Wire;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

// TODO: Finish
public class GWire implements GCircuitElement {
    public static final int LINE_WIDTH = 2;

    public Circuit c;
    public Wire wire;
    public GJunction aJunction;
    public GJunction bJunction;

    public GWire(Circuit c, Vec aPosition, Vec bPosition) {
        this.c = c;
        this.aJunction = new GJunction(c, aPosition);
        this.bJunction = new GJunction(c, bPosition);
        this.wire = new Wire(aJunction.junction, bJunction.junction);
        c.addWire(this.wire);
    }

    @Override
    public void draw(Graphics2D g, int alpha) {
        g.setColor(new Color(255, 255, 255, alpha));
        draw(g, aJunction.position, bJunction.position, false);
    }

    public static void draw(Graphics2D g, Vec aPos, Vec bPos, boolean junctions) {
        double rad = Math.atan2(bPos.y - aPos.y, bPos.x - aPos.x) + Math.PI /2;
//        Point mid = new Point((aPin.x + bPin.x) / 2, (aPin.y+bPin.y) / 2);
        Vec mid = bPos.mid(aPos);
        AffineTransform old = g.getTransform();
        g.setStroke(new BasicStroke(LINE_WIDTH));
        g.translate(mid.x, mid.y);
        g.rotate(rad);
//            g.setColor(Color.WHITE);
//        int color = (int)(255*.7+.3*255*Math.sin(elapsedTime/16*.1));
        int dist = (int) bPos.dist(aPos) - GJunction.PIN_RADIUS * 2;
        g.drawLine(0, -dist/2, 0, dist/2);

//        g.drawLine(0, BATTERY_GAP / 2, 0, dist/2 - GJunction.PIN_RADIUS);
//        g.drawLine(-BATTERY_NEG_WIDTH/2, BATTERY_GAP / 2,
//                BATTERY_NEG_WIDTH / 2, BATTERY_GAP / 2);
//        g.drawLine(-BATTERY_POS_WIDTH/2, -BATTERY_GAP / 2,
//                BATTERY_POS_WIDTH / 2, -BATTERY_GAP / 2);
//        g.drawLine(0, -BATTERY_GAP / 2, 0, -dist/2 + GJunction.PIN_RADIUS);
        g.setTransform(old);
        if (junctions) {
            GJunction.draw(g, aPos, false);
            GJunction.draw(g, bPos, false);
        }
    }

    @Override
    public void setPosition(Vec p) {
        Vec delta = p.sub(getPosition());
        aJunction.position = aJunction.position.add(delta);
        bJunction.position = bJunction.position.add(delta);
    }

    @Override
    public Vec getPosition() {
        return bJunction.position.mid(aJunction.position);
    }

    @Override
    public boolean contains(Vec p) {
        Vec aPos = aJunction.position;
        Vec bPos = bJunction.position;
        double rad = Math.atan2(bPos.y - aPos.y, bPos.x - aPos.x) + Math.PI /2;
        int dist = (int) aPos.dist(bPos);
        Point mid = new Point((aPos.x + bPos.x) / 2, (aPos.y + bPos.y) / 2);
        AffineTransform at = new AffineTransform();
        at.translate(mid.x, mid.y);
        at.rotate(rad);
        return at.createTransformedShape(new Rectangle2D.Double(-20/2,
                -dist/2 + GJunction.PIN_RADIUS,
                20, dist - GJunction.PIN_RADIUS*2)).contains(p.x, p.y);
    }

    @Override
    public boolean containsJunction(Vec p) {
        return aJunction.contains(p) || bJunction.contains(p);
    }

    @Override
    public GJunction getJunction(Vec p) {
        if (aJunction.contains(p)) {
            return aJunction;
        } else if (bJunction.contains(p)) {
            return bJunction;
        }
        return null;
    }
}
