package com.imontero.circuit;

public enum CircuitElementType {
    JUNCTION(-1), BATTERY(2), RESISTOR(2);

    // -1 if infinite number
    final int PINS;

    CircuitElementType(int pins) {
        this.PINS = pins;
    }
}
