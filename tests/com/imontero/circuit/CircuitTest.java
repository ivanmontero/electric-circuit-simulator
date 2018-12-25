package com.imontero.circuit;

import com.imontero.circuit.*;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

public class CircuitTest {
    public static final double EPSILON = 0.0001;

    /* =================== Solve =================== */

    @Test
    public void solveSimple() {
        Circuit c = createCompleteNoJunctionCircuit();
        c.solve();
        assertEquals(.05, c.branches.iterator().next().current, EPSILON);
    }

    @Test
    public void solveMultiJunction() {
        Circuit c = new Circuit();
        CircuitElement b = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(5.0)
                .build();
        CircuitElement r1 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(100.0)
                .build();
        CircuitElement r2 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(200.0)
                .build();
        CircuitElement j1 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.JUNCTION)
                .build();
        CircuitElement j2 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.JUNCTION)
                .build();
        Wire wbj1 = new Wire(b, j1);
        Wire wj1r1 = new Wire(j1, r1);
        Wire wj1r2 = new Wire(j1, r2);
        Wire wr1j2 = new Wire(r1, j2);
        Wire wr2j2 = new Wire(r2, j2);
        Wire wj2b = new Wire(j2, b);
        c.addCircuitElement(b);
        c.addCircuitElement(r1);
        c.addCircuitElement(r2);
        c.addCircuitElement(j1);
        c.addCircuitElement(j2);
        c.addWire(wbj1);
        c.addWire(wj1r1);
        c.addWire(wr1j2);
        c.addWire(wj2b);
        c.addWire(wj1r2);
        c.addWire(wr2j2);
        b.setDirection(wj2b, wbj1);

        c.solve();

        assertEquals(3, c.branches.size());
        for (Branch br : c.branches) {
            double sign;
            // We evaluate the currents going to/from j1.
            if (br.wires.contains(wbj1)) {
                // Battery feeds current into the intersection.
                sign = br.getDirection(b, j1) ? 1 : -1;
                assertEquals(0.075 * sign, br.current, EPSILON);
            } else if (br.wires.contains(wj1r1)) {
                // Current exits the intersection.
                sign = br.getDirection(j1, r1) ? 1 : -1;
                assertEquals(0.05 * sign, br.current, EPSILON);
            } else if (br.wires.contains(wj1r2)) {
                // Current exits the intersection.
                sign = br.getDirection(j1, r2) ? 1 : -1;
                assertEquals(0.025 * sign, br.current, EPSILON);
            }
        }

    }


    /* =================== Add com.imontero.circuit.Circuit Element =================== */

    @Test
    public void addCircuitElementSimple() throws Exception {
        Circuit c = new Circuit();
        CircuitElement b = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(5.0)
                .build();

        assertTrue(c.elements.isEmpty());
        c.addCircuitElement(b);
        assertTrue(c.elements.size() == 1);
        assertTrue(c.elements.contains(b));
    }


    /* =================== Add com.imontero.circuit.Wire =================== */

    @Test
    public void addWireLoopConsistencySimple() throws Exception {
        Circuit c = new Circuit();
        CircuitElement b = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(5.0)
                .build();
        CircuitElement r = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(100.0)
                .build();
        Wire wbr = new Wire(b, r);
        Wire wrb = new Wire(r, b);

        c.addCircuitElement(b);
        c.addCircuitElement(r);
        /*
         *
         *
         * B   R
         *
         *
         */
        assertTrue(c.loops.isEmpty());
        assertTrue(c.elements.size() == 2);

        c.addWire(wbr);
        /*
         * +---+
         * |   |
         * B   R
         *
         *
         */
        assertTrue(c.loops.isEmpty());
        assertTrue(c.wireToBranch.isEmpty());
        assertTrue(c.branches.isEmpty());
        assertTrue(c.junctions.isEmpty());

        c.addWire(wrb);
        /*
         * +---+
         * |   |
         * B   R
         * |   |
         * +---+
         */
        assertTrue(c.loops.size() == 1);
        assertTrue(c.wireToBranch.size() == 2);
        assertTrue(c.branches.size() == 1);
        assertTrue(c.junctions.isEmpty());
        assertTrue(new HashSet<>(c.wireToBranch.values()).size() == 1);
    }

    @Test
    public void addWireLoopConsistencySimpleFlippedWires() throws Exception {
        Circuit c = new Circuit();
        CircuitElement b = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(5.0)
                .build();
        CircuitElement r = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(100.0)
                .build();

        Wire wbr = new Wire(b, r);
        Wire wrb = new Wire(b, r);

        c.addCircuitElement(b);
        c.addCircuitElement(r);
        /*
         *
         *
         * B   R
         *
         *
         */
        assertTrue(c.loops.isEmpty());
        assertTrue(c.elements.size() == 2);

        c.addWire(wbr);
        /*
         * +---+
         * |   |
         * B   R
         *
         *
         */
        assertTrue(c.loops.isEmpty());
        assertTrue(c.wireToBranch.isEmpty());
        assertTrue(c.branches.isEmpty());
        assertTrue(c.junctions.isEmpty());

        c.addWire(wrb);
        /*
         * +---+
         * |   |
         * B   R
         * |   |
         * +---+
         */
        assertTrue(c.loops.size() == 1);
        assertTrue(c.wireToBranch.size() == 2);
        assertTrue(c.branches.size() == 1);
        assertTrue(c.junctions.isEmpty());
        assertTrue(new HashSet<>(c.wireToBranch.values()).size() == 1);
    }

    @Test
    public void addWireLoopConsistencyJunction() throws Exception {
        Circuit c = new Circuit();
        CircuitElement b = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(5.0)
                .build();
        CircuitElement r1 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(100.0)
                .build();
        CircuitElement r2 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(200.0)
                .build();
        CircuitElement j1 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.JUNCTION)
                .build();
        CircuitElement j2 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.JUNCTION)
                .build();

        Wire wbj1 = new Wire(b, j1);
        Wire wj1r1 = new Wire(j1, r1);
        Wire wj1r2 = new Wire(j1, r2);
        Wire wr1j2 = new Wire(r1, j2);
        Wire wr2j2 = new Wire(r2, j2);
        Wire wj2b = new Wire(j2, b);

        c.addCircuitElement(b);
        c.addCircuitElement(r1);
        c.addCircuitElement(r2);
        c.addCircuitElement(j1);
        c.addCircuitElement(j2);
        /*
         *     J
         *
         * B   R   R
         *
         *     J
         */
        assertTrue(c.loops.isEmpty());
        assertTrue(c.elements.size() == 5);

        c.addWire(wbj1);
        /*
         * +---J
         * |
         * B   R   R
         *
         *     J
         */
        assertTrue(c.loops.isEmpty());
        assertTrue(c.wireToBranch.isEmpty());
        assertTrue(c.branches.isEmpty());
        assertTrue(c.junctions.isEmpty());

        c.addWire(wj1r1);
        /*
         * +---J
         * |   |
         * B   R   R
         *
         *     J
         */
        assertTrue(c.loops.isEmpty());
        assertTrue(c.wireToBranch.isEmpty());
        assertTrue(c.branches.isEmpty());
        assertTrue(c.junctions.isEmpty());

        c.addWire(wr1j2);
        /*
         * +---J
         * |   |
         * B   R   R
         *     |
         *     J
         */
        assertTrue(c.loops.isEmpty());
        assertTrue(c.wireToBranch.isEmpty());
        assertTrue(c.branches.isEmpty());
        assertTrue(c.junctions.isEmpty());

        c.addWire(wj2b);
        /*
         * +---J
         * |   |
         * B   R   R
         * |   |
         * +---J
         */
        assertTrue(c.loops.size() == 1);
        assertTrue(c.wireToBranch.size() == 4);
        assertTrue(c.branches.size() == 1);
        assertTrue(c.junctions.isEmpty());

        c.addWire(wj1r2);
        /*
         * +---J---+
         * |   |   |
         * B   R   R
         * |   |
         * +---J
         */
        assertTrue(c.loops.size() == 1);
        assertTrue(c.wireToBranch.size() == 4);
        assertTrue(c.branches.size() == 1);
        assertTrue(c.junctions.isEmpty());

        c.addWire(wr2j2);
        /*
         * +---J---+
         * |   |   |
         * B   R   R
         * |   |   |
         * +---J---+
         */
        assertTrue(c.loops.size() == 3);
        assertTrue(c.wireToBranch.size() == 6);
        assertTrue(c.branches.size() == 3);
        assertTrue(c.junctions.size() == 2);

        assertTrue(new HashSet<>(c.wireToBranch.values()).size() == 3);
        assertTrue(c.wireToBranch.get(wbj1).equals(c.wireToBranch.get(wj2b)));
        assertTrue(c.wireToBranch.get(wj1r1).equals(c.wireToBranch.get(wr1j2)));
        assertTrue(c.wireToBranch.get(wj1r2).equals(c.wireToBranch.get(wr2j2)));

        assertFalse(c.wireToBranch.get(wbj1).equals(c.wireToBranch.get(wr1j2)));
        assertFalse(c.wireToBranch.get(wj1r1).equals(c.wireToBranch.get(wr2j2)));
        assertFalse(c.wireToBranch.get(wj1r2).equals(c.wireToBranch.get(wj2b)));
    }

    @Test
    public void addWireLoopConsistencyJunctionFlippedWires() throws Exception {
        Circuit c = new Circuit();
        CircuitElement b = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(5.0)
                .build();
        CircuitElement r1 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(100.0)
                .build();
        CircuitElement r2 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(200.0)
                .build();
        CircuitElement j1 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.JUNCTION)
                .build();
        CircuitElement j2 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.JUNCTION)
                .build();

        Wire wbj1 = new Wire(j1, b);
        Wire wj1r1 = new Wire(j1, r1);
        Wire wj1r2 = new Wire(r2, j1);
        Wire wr1j2 = new Wire(r1, j2);
        Wire wr2j2 = new Wire(j2, r2);
        Wire wj2b = new Wire(j2, b);

        c.addCircuitElement(b);
        c.addCircuitElement(r1);
        c.addCircuitElement(r2);
        c.addCircuitElement(j1);
        c.addCircuitElement(j2);
        /*
         *     J
         *
         * B   R   R
         *
         *     J
         */
        assertTrue(c.loops.isEmpty());
        assertTrue(c.elements.size() == 5);

        c.addWire(wbj1);
        /*
         * +---J
         * |
         * B   R   R
         *
         *     J
         */
        assertTrue(c.loops.isEmpty());
        assertTrue(c.wireToBranch.isEmpty());
        assertTrue(c.branches.isEmpty());
        assertTrue(c.junctions.isEmpty());

        c.addWire(wj1r1);
        /*
         * +---J
         * |   |
         * B   R   R
         *
         *     J
         */
        assertTrue(c.loops.isEmpty());
        assertTrue(c.wireToBranch.isEmpty());
        assertTrue(c.branches.isEmpty());
        assertTrue(c.junctions.isEmpty());

        c.addWire(wr1j2);
        /*
         * +---J
         * |   |
         * B   R   R
         *     |
         *     J
         */
        assertTrue(c.loops.isEmpty());
        assertTrue(c.wireToBranch.isEmpty());
        assertTrue(c.branches.isEmpty());
        assertTrue(c.junctions.isEmpty());

        c.addWire(wj2b);
        /*
         * +---J
         * |   |
         * B   R   R
         * |   |
         * +---J
         */
        assertTrue(c.loops.size() == 1);
        assertTrue(c.wireToBranch.size() == 4);
        assertTrue(c.branches.size() == 1);
        assertTrue(c.junctions.isEmpty());

        c.addWire(wj1r2);
        /*
         * +---J---+
         * |   |   |
         * B   R   R
         * |   |
         * +---J
         */
        assertTrue(c.loops.size() == 1);
        assertTrue(c.wireToBranch.size() == 4);
        assertTrue(c.branches.size() == 1);
        assertTrue(c.junctions.isEmpty());

        c.addWire(wr2j2);
        /*
         * +---J---+
         * |   |   |
         * B   R   R
         * |   |   |
         * +---J---+
         */
        assertTrue(c.loops.size() == 3);
        assertTrue(c.wireToBranch.size() == 6);
        assertTrue(c.branches.size() == 3);
        assertTrue(c.junctions.size() == 2);

        assertTrue(new HashSet<>(c.wireToBranch.values()).size() == 3);
        assertTrue(c.wireToBranch.get(wbj1).equals(c.wireToBranch.get(wj2b)));
        assertTrue(c.wireToBranch.get(wj1r1).equals(c.wireToBranch.get(wr1j2)));
        assertTrue(c.wireToBranch.get(wj1r2).equals(c.wireToBranch.get(wr2j2)));

        assertFalse(c.wireToBranch.get(wbj1).equals(c.wireToBranch.get(wr1j2)));
        assertFalse(c.wireToBranch.get(wj1r1).equals(c.wireToBranch.get(wr2j2)));
        assertFalse(c.wireToBranch.get(wj1r2).equals(c.wireToBranch.get(wj2b)));
    }

    /* =================== Helper Functions =================== */

    /**
     * +---+
     * |   |
     * B   R
     * |   |
     * +---+
     * B= 5 V
     * R= 100 ohms
     */
    private Circuit createCompleteNoJunctionCircuit() {
        Circuit c = new Circuit();
        CircuitElement b = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(5.0)
                .build();
        CircuitElement r = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(100.0)
                .build();
        Wire wbr = new Wire(b, r);
        Wire wrb = new Wire(r, b);
        c.addCircuitElement(b);
        c.addCircuitElement(r);
        c.addWire(wbr);
        c.addWire(wrb);
        return c;
    }

    /**
     * +---J1--+
     * |   |   |
     * B   R1  R2
     * |   |   |
     * +---J2--+
     * B= 5 V, pos towards J1
     * R1= 100 ohms
     * R2= 200 ohms
     */
    private Circuit createCompleteTwoJunctionCircuit() {
        Circuit c = new Circuit();
        CircuitElement b = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(5.0)
                .build();
        CircuitElement r1 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(100.0)
                .build();
        CircuitElement r2 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(200.0)
                .build();
        CircuitElement j1 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.JUNCTION)
                .build();
        CircuitElement j2 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.JUNCTION)
                .build();
        Wire wbj1 = new Wire(b, j1);
        Wire wj1r1 = new Wire(j1, r1);
        Wire wj1r2 = new Wire(j1, r2);
        Wire wr1j2 = new Wire(r1, j2);
        Wire wr2j2 = new Wire(r2, j2);
        Wire wj2b = new Wire(j2, b);
        c.addCircuitElement(b);
        c.addCircuitElement(r1);
        c.addCircuitElement(r2);
        c.addCircuitElement(j1);
        c.addCircuitElement(j2);
        c.addWire(wbj1);
        c.addWire(wj1r1);
        c.addWire(wr1j2);
        c.addWire(wj2b);
        c.addWire(wj1r2);
        c.addWire(wr2j2);
        b.setDirection(wj2b, wbj1);
        return c;
    }
}