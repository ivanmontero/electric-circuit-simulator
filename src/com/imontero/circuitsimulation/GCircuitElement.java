package com.imontero.circuitsimulation;

import com.imontero.circuit.Circuit;
import com.imontero.circuit.CircuitElement;
import com.imontero.circuit.CircuitElementType;
import com.imontero.circuit.Wire;

import java.awt.*;

// A simple wrapper
public interface GCircuitElement {
    void draw(Graphics2D g, int alpha);

    void setPosition(Vec p);
    Vec getPosition();

    boolean contains(Vec p);
    boolean containsJunction(Vec p);
    GJunction getJunction(Vec p);
//    public CircuitElement circuitElement;
//    public Circuit c;
//
//    protected GCircuitElement(Circuit c) {
//        this.c = c;
//    }
//
//    public abstract void connect(GJunction other);
//    public abstract void disconnect(GJunction other);
//    public abstract void setPosition(Vec p);
//    public abstract Vec getPosition();
//    public abstract boolean contains(Vec p);
//
//    public abstract void draw(Graphics2D g);
//    public abstract void draw(Graphics2D g, int alpha);
//
//    // Returns a junction if one exists at the point, null
//    // otherwise.
//    public abstract GJunction getJunction(Vec p);
//
//

}
