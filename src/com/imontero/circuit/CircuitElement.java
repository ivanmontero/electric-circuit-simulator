package com.imontero.circuit;

import java.util.ArrayList;

// TODO: Write assertions.
public class CircuitElement {
    private static int circuitElementIdCounter = 0;
    public final int ID;
    public CircuitElementType type;
    // If only has 2 connections:
    // Scalars are measured from 0 to 1.
    public ArrayList<Wire> connections;

    /* Junction */
    // Assertions:
    //  * Has multiple connections
    //  * Sum current in == Sum current out

    /* Battery */
    // Assertions:
    //  * Has only 2 connections
    //  * Has a direction
    // Fields:
    public double potentialDifference = 0;

    /* Resistor */
    // Assertions:
    //  * Has only 2 connections
    // Fields:
    public double resistance = 0;

    public CircuitElement() {
        this.ID = circuitElementIdCounter++;
    }

    private CircuitElement(CircuitElementBuilder builder) {
        this();
        this.type = builder.type;
        this.connections = builder.connections;
        this.potentialDifference = builder.potentialDifference;
        this.resistance = builder.resistance;
    }

    // Convenience method for 2 pin circuits
    public Wire next(Wire w) {
        if (connections.get(0).equals(w)) {
            return connections.get(1);
        } else {
            return connections.get(0);
        }
    }

    /*
    Must provide the branch and loop this element is contained in.
    Only works for 2 pin elements.
    -1 if negative, 1 if pos. 0 if none.
     */
    public int getPotentialDifferenceSign(Loop loop, Branch branch) {
        Wire a = connections.get(0);
        Wire b = connections.get(1);
        switch(this.type) {
            case BATTERY:
                // Takes into account its own direction
                return loop.getDirection(a, b) ? 1 : -1;
            case RESISTOR:
                return loop.getDirection(a, b) == branch.getDirection(a, b) ? -1 : 1;
            default:
                return 0;
        }
    }

    // ONLY called when two wires are attached.
    public void setDirection(Wire a, Wire b) {
        assert connections.contains(a) && connections.contains(b);
        if (!connections.get(0).equals(a)) {
            connections.add(0, connections.remove(1));
        }
    }

    public String toString() {
        return ID + ":" + type;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof CircuitElement && ((CircuitElement) other).ID == this.ID;
    }

    @Override
    public int hashCode() {
        return 31 * this.ID;
    }

    public static class CircuitElementBuilder {
        private CircuitElementType type;
        private ArrayList<Wire> connections;
        private double potentialDifference;
        private double resistance;

        public CircuitElementBuilder() {
            connections = new ArrayList<>();
        }

        public CircuitElementBuilder type(CircuitElementType type) {
            this.type = type;
            return this;
        }

        public CircuitElementBuilder addConnection(Wire wire) {
            this.connections.add(wire);
            return this;
        }

        public CircuitElementBuilder potentialDifference(double potentialDifference) {
            this.potentialDifference = potentialDifference;
            return this;
        }

        public CircuitElementBuilder resistance(double resistance) {
            this.resistance = resistance;
            return this;
        }

        public CircuitElement build() {
            return new CircuitElement(this);
        }
    }

}
