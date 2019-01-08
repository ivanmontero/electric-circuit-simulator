package com.imontero.circuitsimulation;

import com.imontero.circuit.Circuit;
import com.imontero.circuit.CircuitElement;
import com.imontero.circuit.CircuitElementType;
import com.imontero.circuit.Wire;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class GJunction {
    public static final int PIN_RADIUS = 5;

    public List<GCircuitComponent> connections;
    public List<Wire> connectionWires;
    // Set iff this pin is a junction (connected to by >2 wires)
    public CircuitElement junction;
    public Vec position;
    public Circuit c;

    public GJunction(Circuit c, Vec position) {
        this.c = c;
        this.position = position;
        this.connections = new ArrayList<>();
        this.connectionWires = new ArrayList<>();
        this.junction = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.JUNCTION)
                .build();
        c.addCircuitElement(this.junction);
    }

    public Wire addConnection(GCircuitComponent component) {
        this.connections.add(component);
        Wire w = new Wire(component.circuitElement,
                this.junction);
        this.junction.connections.add(w);
        this.connectionWires.add(w);
        c.addWire(w);
        return w;
    }

    public void removeConnection(GCircuitComponent component) {
        int index = connections.indexOf(component);
        connections.remove(index);
        c.removeWire(connectionWires.remove(index));
    }

    public void processMovement(Point start, Point end) {

    }

    public void draw(Graphics2D g, int alpha) {
        g.setColor(new Color(255,255,255, alpha));
        if (connections.size() < 2) {
            g.drawOval(position.x - PIN_RADIUS, position.y - PIN_RADIUS,
                    2*PIN_RADIUS, 2*PIN_RADIUS);
        } else {
            g.fillOval(position.x - PIN_RADIUS, position.y - PIN_RADIUS,
                    2*PIN_RADIUS, 2*PIN_RADIUS);
        }
    }


    public void draw(Graphics2D g) {
        draw(g, 255);
    }

    public static void draw(Graphics2D g, Point position) {
        g.fillOval(position.x - PIN_RADIUS, position.y - PIN_RADIUS,
                2*PIN_RADIUS, 2*PIN_RADIUS);
    }


    public boolean contains(Vec p) {
        return (new Ellipse2D.Double(position.x - PIN_RADIUS, position.y - PIN_RADIUS,
                2*PIN_RADIUS, 2*PIN_RADIUS)).contains(p.x, p.y);
    }
}
