package com.imontero.circuitsimulation;

import com.imontero.circuit.Circuit;
import com.imontero.circuit.CircuitElement;
import com.imontero.circuit.CircuitElementType;
import com.imontero.circuit.Wire;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class GBattery implements GCircuitElement {
    public static final int BATTERY_GAP = 6;
    public static final int BATTERY_POS_WIDTH = 50;
    public static final int BATTERY_NEG_WIDTH = 30;
    public static final int MIN_BATTERY_PIN_LENGTH = 50;
    public static final int LINE_WIDTH = 2;

    public Circuit c;
    public CircuitElement battery;
    public Wire negativeWire;
    public Wire positiveWire;
    public GJunction negativeJunction;
    public GJunction positiveJunction;

    public GBattery(Circuit c, Vec negativePosition, Vec positivePosition, double voltage) {
        this.battery = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(voltage)
                .build();
        c.addCircuitElement(this.battery);
        this.c = c;
        this.negativeJunction = new GJunction(c, negativePosition);
        this.positiveJunction = new GJunction(c, positivePosition);
        this.negativeWire = this.negativeJunction.addConnection(this, battery);
        this.positiveWire = this.positiveJunction.addConnection(this, battery);
    }

    public GBattery(Circuit c, GJunction negativeJunction, Vec positivePosition, double voltage) {
        this.battery = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(voltage)
                .build();
        c.addCircuitElement(this.battery);
        this.c = c;
        this.negativeJunction = negativeJunction;
        this.positiveJunction = new GJunction(c, positivePosition);
        this.negativeWire = this.negativeJunction.addConnection(this, battery);
        this.positiveWire = this.positiveJunction.addConnection(this, battery);
    }

    public void setPotentialDifference(double voltage) {
        this.battery.potentialDifference = voltage;
    }

    public void setPosition(Vec p) {
        Vec delta = p.sub(getPosition());
        negativeJunction.position = negativeJunction.position.add(delta);
        positiveJunction.position = positiveJunction.position.add(delta);
        // Check if new connections were made
    }

    public Vec getPosition() {
        return negativeJunction.position.mid(positiveJunction.position);
    }

    public boolean contains(Vec p) {
        Vec nPos = negativeJunction.position;
        Vec pPos = positiveJunction.position;
        double rad = Math.atan2(pPos.y - nPos.y, pPos.x - nPos.x) + Math.PI /2;
        int dist = (int) nPos.dist(pPos);
        Point mid = new Point((nPos.x + pPos.x) / 2, (nPos.y + pPos.y) / 2);
        AffineTransform at = new AffineTransform();
        at.translate(mid.x, mid.y);
        at.rotate(rad);
        return at.createTransformedShape(new Rectangle2D.Double(-20/2,
                -dist/2 + GJunction.PIN_RADIUS,
                20, dist - GJunction.PIN_RADIUS*2)).contains(p.x, p.y) ||
                at.createTransformedShape(new Rectangle2D.Double(-BATTERY_POS_WIDTH/2,
                        -BATTERY_GAP/2, BATTERY_POS_WIDTH, BATTERY_GAP)).contains(p.x, p.y);
    }

    @Override
    public boolean containsJunction(Vec p) {
        return positiveJunction.contains(p) || negativeJunction.contains(p);
    }

    public void draw(Graphics2D g, int alpha) {
        g.setColor(new Color(255,255,255, alpha));
        draw(g, negativeJunction.position, positiveJunction.position,
                negativeJunction.connections.size() >= 2,
                positiveJunction.connections.size() >= 2);
    }

    public static void draw(Graphics2D g, Vec nPos, Vec pPos, boolean nConnected, boolean pConnected) {
        double rad = Math.atan2(pPos.y - nPos.y, pPos.x - nPos.x) + Math.PI /2;
//        Point mid = new Point((aPin.x + bPin.x) / 2, (aPin.y+bPin.y) / 2);
        Vec mid = pPos.mid(nPos);
        AffineTransform old = g.getTransform();
        g.setStroke(new BasicStroke(LINE_WIDTH));
        g.translate(mid.x, mid.y);
        g.rotate(rad);
//            g.setColor(Color.WHITE);
//        int color = (int)(255*.7+.3*255*Math.sin(elapsedTime/16*.1));
        int dist = (int) pPos.dist(nPos);
        g.drawLine(0, BATTERY_GAP / 2, 0, dist/2 - GJunction.PIN_RADIUS);
        g.drawLine(-BATTERY_NEG_WIDTH/2, BATTERY_GAP / 2,
                BATTERY_NEG_WIDTH / 2, BATTERY_GAP / 2);
        g.drawLine(-BATTERY_POS_WIDTH/2, -BATTERY_GAP / 2,
                BATTERY_POS_WIDTH / 2, -BATTERY_GAP / 2);
        g.drawLine(0, -BATTERY_GAP / 2, 0, -dist/2 + GJunction.PIN_RADIUS);
        g.setTransform(old);
        GJunction.draw(g, nPos, nConnected);
        GJunction.draw(g, pPos, pConnected);
    }

    public GJunction getJunction(Vec p) {
        if (negativeJunction.contains(p)) {
            return negativeJunction;
        } else if (positiveJunction.contains(p)) {
            return positiveJunction;
        }
        return null;
    }
}