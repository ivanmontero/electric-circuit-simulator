package com.imontero.circuitsimulation;

import com.imontero.circuit.Circuit;
import com.imontero.circuit.CircuitElement;
import com.imontero.circuit.CircuitElementType;
import com.imontero.circuit.Wire;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class GBattery extends GCircuitComponent {
    public final int BATTERY_GAP = 6;
    public final int BATTERY_POS_WIDTH = 50;
    public final int BATTERY_NEG_WIDTH = 30;
    public static final int MIN_BATTERY_PIN_LENGTH = 50;
    public final int LINE_WIDTH = 2;

    public Wire negativeWire;
    public Wire positiveWire;
    public GJunction negativeJunction;
    public GJunction positiveJunction;

    public GBattery(Circuit c, Vec negativePosition, Vec positivePosition, double voltage) {
        super(c);
        this.circuitElement = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(voltage)
                .build();
        c.addCircuitElement(this.circuitElement);
        this.negativeJunction = new GJunction(c, negativePosition);
        this.positiveJunction = new GJunction(c, positivePosition);
        this.negativeWire = this.negativeJunction.addConnection(this);
        this.positiveWire = this.positiveJunction.addConnection(this);
        this.circuitElement.setDirection(negativeWire, positiveWire);
    }

    public GBattery(Circuit c, GJunction negativeJunction, Vec positivePosition, double voltage) {
        super(c);
        this.circuitElement = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(voltage)
                .build();
        c.addCircuitElement(this.circuitElement);
        this.positiveJunction = new GJunction(c, positivePosition);
        this.negativeWire = this.negativeJunction.addConnection(this);
        this.positiveWire = this.positiveJunction.addConnection(this);
    }

    public void setPotentialDifference(double voltage) {
        this.circuitElement.potentialDifference = voltage;
    }

    @Override
    public void connect(GJunction other) {

    }

    @Override
    public void disconnect(GJunction other) {

    }

    @Override
    public void setPosition(Vec p) {
        Vec delta = p.sub(getPosition());
        negativeJunction.position = negativeJunction.position.add(delta);
        positiveJunction.position = positiveJunction.position.add(delta);


        // Check if new connections were made
    }

    @Override
    public Vec getPosition() {
        return negativeJunction.position.mid(positiveJunction.position);
    }

    @Override
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
    public void draw(Graphics2D g) {
        draw(g, 255);
    }

    @Override
    public void draw(Graphics2D g, int alpha) {
        Vec nPos = negativeJunction.position;
        Vec pPos = positiveJunction.position;
        double rad = Math.atan2(pPos.y - nPos.y, pPos.x - nPos.x) + Math.PI /2;
//        Point mid = new Point((aPin.x + bPin.x) / 2, (aPin.y+bPin.y) / 2);
        Vec mid = pPos.mid(nPos);
        AffineTransform old = g.getTransform();
        g.setStroke(new BasicStroke(LINE_WIDTH));
        g.translate(mid.x, mid.y);
        g.rotate(rad);
//            g.setColor(Color.WHITE);
//        int color = (int)(255*.7+.3*255*Math.sin(elapsedTime/16*.1));
        g.setColor(new Color(255,255,255, alpha));
        int dist = (int) pPos.dist(nPos);
        g.drawLine(0, BATTERY_GAP / 2, 0, dist/2 - GJunction.PIN_RADIUS);
        g.drawLine(-BATTERY_NEG_WIDTH/2, BATTERY_GAP / 2,
                BATTERY_NEG_WIDTH / 2, BATTERY_GAP / 2);
        g.drawLine(-BATTERY_POS_WIDTH/2, -BATTERY_GAP / 2,
                BATTERY_POS_WIDTH / 2, -BATTERY_GAP / 2);
        g.drawLine(0, -BATTERY_GAP / 2, 0, -dist/2 + GJunction.PIN_RADIUS);
        g.setTransform(old);
    }

    @Override
    public GJunction getJunction(Vec p) {
        if (negativeJunction.contains(p)) {
            return negativeJunction;
        } else if (positiveJunction.contains(p)) {
            return positiveJunction;
        }
        return null;
    }
}
//private class GBattery implements Drawable {
//    public final int PIN_RADIUS = 5;
//    public final int BATTERY_GAP = 6;
//    public final int BATTERY_POS_WIDTH = 50;
//    public final int BATTERY_NEG_WIDTH = 30;
//    public final int MIN_BATTERY_PIN_LENGTH = 50;
//    public final int LINE_WIDTH = 2;
//
//    private CircuitElement battery;
//    private Wire a;
//    private Wire b;
//    private Point aPin;
//    private Point bPin;
//
//    public boolean aSelected, bSelected;
//
//    public GBattery(Point aPin, Point bPin, double potentialDifference) {
//        setNegativePinPosition(aPin);
//        setPositivePinPosition(bPin);
//        battery = new CircuitElement.CircuitElementBuilder()
//                .potentialDifference(potentialDifference)
//                .build();
//        circuit.addCircuitElement(battery);
//    }
//
//    public void connectToPositivePin(Wire w) {
//        this.b = w;
//        if (this.a != null) {
//            this.battery.connections.set(0, a);
//            this.battery.connections.set(1, b);
//            this.battery.setDirection(a, b);
//        }
//    }
//
//    public void connectToNegativePin(Wire w) {
//        this.a = w;
//        if (this.b != null) {
//            this.battery.connections.set(0, a);
//            this.battery.connections.set(1, b);
//            this.battery.setDirection(a, b);
//        }
//    }
//
//    public void setNegativePinPosition(Point p) {
//        if (bPin != null && bPin.distance(p) < MIN_BATTERY_PIN_LENGTH) {
//            double rad = Math.atan2(p.y - bPin.y, p.x - bPin.x);
//            p = new Point(bPin.x + (int)(MIN_BATTERY_PIN_LENGTH * Math.cos(rad)),
//                    (int) (bPin.y + MIN_BATTERY_PIN_LENGTH * Math.sin(rad)));
//        }
//        this.aPin = p;
//    }
//
//    public void setPositivePinPosition(Point p) {
//        if (aPin != null && aPin.distance(p) < MIN_BATTERY_PIN_LENGTH) {
//            double rad = Math.atan2(p.y - aPin.y, p.x - aPin.x);
//            p = new Point(aPin.x + (int)(MIN_BATTERY_PIN_LENGTH * Math.cos(rad)),
//                    (int) (aPin.y + MIN_BATTERY_PIN_LENGTH * Math.sin(rad)));
//        }
//        this.bPin = p;
//    }
//
//    public void translate(int dx, int dy) {
//        this.aPin = new Point(aPin.x + dx, aPin.y + dy);
//        this.bPin = new Point(bPin.x + dx, aPin.y + dy);
//    }
//
//    @Override
//    public void draw(Graphics2D g) {
//        double rad = Math.atan2(bPin.y - aPin.y, bPin.x - aPin.x) + Math.PI /2;
//        Point mid = new Point((aPin.x + bPin.x) / 2, (aPin.y+bPin.y) / 2);
//        AffineTransform old = g.getTransform();
//        g.setStroke(new BasicStroke(LINE_WIDTH));
//        g.translate(mid.x, mid.y);
//        g.rotate(rad);
////            g.setColor(Color.WHITE);
//        int color = (int)(255*.7+.3*255*Math.sin(elapsedTime/16*.1));
//        g.setColor(new Color(color, color, color));
//        int dist = (int) bPin.distance(aPin);
//        g.drawOval(-PIN_RADIUS, dist/2-PIN_RADIUS,
//                2*PIN_RADIUS, 2*PIN_RADIUS);
//        g.drawLine(0, BATTERY_GAP / 2, 0, dist/2 - PIN_RADIUS);
//        g.drawLine(-BATTERY_NEG_WIDTH/2, BATTERY_GAP / 2,
//                BATTERY_NEG_WIDTH / 2, BATTERY_GAP / 2);
//        g.drawLine(-BATTERY_POS_WIDTH/2, -BATTERY_GAP / 2,
//                BATTERY_POS_WIDTH / 2, -BATTERY_GAP / 2);
//        g.drawLine(0, -BATTERY_GAP / 2, 0, -dist/2 + PIN_RADIUS);
//        g.drawOval(-PIN_RADIUS, -dist/2-PIN_RADIUS,
//                2*PIN_RADIUS, 2*PIN_RADIUS);
//        g.setTransform(old);
//    }
//
//    public boolean positivePinContains(Point p) {
//        return (new Ellipse2D.Double(bPin.x - PIN_RADIUS, bPin.y - PIN_RADIUS,
//                PIN_RADIUS*2, PIN_RADIUS*2)).contains(p);
//    }
//
//    public boolean negativePinContains(Point p) {
//        return (new Ellipse2D.Double(aPin.x - PIN_RADIUS, aPin.y - PIN_RADIUS,
//                PIN_RADIUS*2, PIN_RADIUS*2)).contains(p);
//    }
//
//    public boolean batteryContains(Point p) {
//        double rad = Math.atan2(bPin.y - aPin.y, bPin.x - aPin.x) + Math.PI/2;
//        int dist = (int) bPin.distance(aPin);
//        Point mid = new Point((aPin.x + bPin.x) / 2, (aPin.y+bPin.y) / 2);
//        AffineTransform at = new AffineTransform();
//        at.translate(mid.x, mid.y);
//        at.rotate(rad);
//        return at.createTransformedShape(new Rectangle2D.Double(-20/2,
//                -dist/2 + PIN_RADIUS,
//                20, dist - PIN_RADIUS*2)).contains(p);
//    }
//}