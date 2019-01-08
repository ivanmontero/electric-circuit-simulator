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

    public List<GCircuitElement> connections;
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

    public Wire addConnection(GCircuitElement component, CircuitElement componentElement) {
        this.connections.add(component);
        Wire w = new Wire(componentElement,
                this.junction);

        // connect the wire by passing this element to
        this.junction.connections.add(w);
        this.connectionWires.add(w);
        c.addWire(w);
        return w;
    }

    public void removeConnection(GCircuitElement component) {
        int index = connections.indexOf(component);
        connections.remove(index);
        c.removeWire(connectionWires.remove(index));
    }

    public void draw(Graphics2D g, int alpha) {
        g.setColor(new Color(255,255,255, alpha));
        draw(g, position, connections.size() >= 2);
    }


    public void draw(Graphics2D g) {
        draw(g, 255);
    }

    public static void draw(Graphics2D g, Vec position, boolean connected) {
        if (connected) {
            g.fillOval(position.x - PIN_RADIUS, position.y - PIN_RADIUS,
                    2*PIN_RADIUS, 2*PIN_RADIUS);
        } else {
            g.drawOval(position.x - PIN_RADIUS, position.y - PIN_RADIUS,
                    2*PIN_RADIUS, 2*PIN_RADIUS);
        }

    }


    public boolean contains(Vec p) {
        return (new Ellipse2D.Double(position.x - PIN_RADIUS, position.y - PIN_RADIUS,
                2*PIN_RADIUS, 2*PIN_RADIUS)).contains(p.x, p.y);
    }
}
