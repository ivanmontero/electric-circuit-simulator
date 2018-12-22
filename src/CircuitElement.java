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
