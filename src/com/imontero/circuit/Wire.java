package com.imontero.circuit;

public class Wire {
    private static int wireIdCounter = 0;
    public final int ID;
    public final CircuitElement a;
    public final CircuitElement b;

    public Wire(CircuitElement a, CircuitElement b) {
        this.ID = wireIdCounter++;
        this.a = a;
        this.b = b;
    }

    public CircuitElement next(CircuitElement element) {
        if (element == this.a) {
            return this.b;
        } else if (element == this.b) {
            return this.a;
        } else {
            return null;
        }
    }

    public boolean containsEndpoint(CircuitElement element) {
        return element == a || element == b;
    }

    public CircuitElement getSharedEndpoint(Wire other) {
        if (containsEndpoint(other.a)) {
            return other.a;
        } else if (containsEndpoint(other.b)) {
            return other.b;
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Wire && ((Wire) other).ID == this.ID;
    }

    @Override
    public int hashCode() {
        return 31 * this.ID;
    }

    public String toString() {
        return a.toString() + "<->" + b.toString() ;
    }
}
