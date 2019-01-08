package com.imontero.circuitsimulation;

import com.imontero.circuit.Circuit;
import com.imontero.circuit.CircuitElement;
import com.imontero.circuit.CircuitElementType;
import com.imontero.circuit.Wire;

import java.awt.*;

// A simple wrapper
public abstract class GCircuitComponent {
    public CircuitElement circuitElement;
    public Circuit c;

    protected GCircuitComponent(Circuit c) {
        this.c = c;
    }

    public abstract void connect(GJunction other);
    public abstract void disconnect(GJunction other);
    public abstract void setPosition(Vec p);
    public abstract Vec getPosition();
    public abstract boolean contains(Vec p);

    public abstract void draw(Graphics2D g);
    public abstract void draw(Graphics2D g, int alpha);

    // Returns a junction if one exists at the point, null
    // otherwise.
    public abstract GJunction getJunction(Vec p);



}
