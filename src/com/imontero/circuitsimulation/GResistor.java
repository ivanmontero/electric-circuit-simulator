package com.imontero.circuitsimulation;

import com.imontero.circuit.CircuitElementType;

import java.awt.*;

// TODO: Finish
public class GResistor implements GCircuitElement {

    public GResistor() {
    }

    @Override
    public void draw(Graphics2D g, int alpha) {

    }

    @Override
    public void setPosition(Vec p) {

    }

    @Override
    public Vec getPosition() {
        return null;
    }

    @Override
    public boolean contains(Vec p) {
        return false;
    }

    @Override
    public boolean containsJunction(Vec p) {
        return false;
    }

    @Override
    public GJunction getJunction(Vec p) {
        return null;
    }
}
