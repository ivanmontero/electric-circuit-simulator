import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

// TODO: Factor out circuit elements to final variables.
public class CircuitTest {

    @Test
    public void addCircuitElementSimple() throws Exception {
        Circuit c = new Circuit();
        CircuitElement ce = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(5.0)
                .build();

        assertTrue(c.elements.isEmpty());
        c.addCircuitElement(ce);
        assertTrue(c.elements.size() == 1);
        assertTrue(c.elements.contains(ce));
    }

    /*
     * +---+
     * |   |
     * B   R
     * |   |
     * +---+
     */
    @Test
    public void addWireLoopConsistencySimple() throws Exception {
        Circuit c = new Circuit();
        CircuitElement ce1 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(5.0)
                .build();
        CircuitElement ce2 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(2.0)
                .build();
        Wire w12 = new Wire(ce1, ce2);
        Wire w21 = new Wire(ce2, ce1);

        c.addCircuitElement(ce1);
        c.addCircuitElement(ce2);
        /*
         *
         *
         * B   R
         *
         *
         */
        assertTrue(c.loops.isEmpty());
        assertTrue(c.elements.size() == 2);

        c.addWire(w12);
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

        c.addWire(w21);
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

    /*
     * +---+
     * |   |
     * B   R
     * |   |
     * +---+
     */
    @Test
    public void addWireLoopConsistencySimpleFlippedWires() throws Exception {
        Circuit c = new Circuit();
        CircuitElement ce1 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(5.0)
                .build();
        CircuitElement ce2 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(2.0)
                .build();

        Wire w12 = new Wire(ce1, ce2);
        Wire w21 = new Wire(ce1, ce2);

        c.addCircuitElement(ce1);
        c.addCircuitElement(ce2);
        /*
         *
         *
         * B   R
         *
         *
         */
        assertTrue(c.loops.isEmpty());
        assertTrue(c.elements.size() == 2);

        c.addWire(w12);
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

        c.addWire(w21);
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

    /*
     * +---J---+
     * |   |   |
     * B   R   R
     * |   |   |
     * +---J---+
     */
    @Test
    public void addWireLoopConsistencyJunction() throws Exception {
        Circuit c = new Circuit();
        CircuitElement b = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(5.0)
                .build();
        CircuitElement ce1 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(2.0)
                .build();
        CircuitElement ce2 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(2.0)
                .build();
        CircuitElement j1 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.JUNCTION)
                .build();
        CircuitElement j2 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.JUNCTION)
                .build();

        Wire wbj1 = new Wire(b, j1);
        Wire wj11 = new Wire(j1, ce1);
        Wire wj12 = new Wire(j1, ce2);
        Wire w1j2 = new Wire(ce1, j2);
        Wire w2j2 = new Wire(ce2, j2);
        Wire wj2b = new Wire(j2, b);

        c.addCircuitElement(b);
        c.addCircuitElement(ce1);
        c.addCircuitElement(ce2);
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

        c.addWire(wj11);
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

        c.addWire(w1j2);
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

        c.addWire(wj12);
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

        c.addWire(w2j2);
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
        assertTrue(c.wireToBranch.get(wj11).equals(c.wireToBranch.get(w1j2)));
        assertTrue(c.wireToBranch.get(wj12).equals(c.wireToBranch.get(w2j2)));

        assertFalse(c.wireToBranch.get(wbj1).equals(c.wireToBranch.get(w1j2)));
        assertFalse(c.wireToBranch.get(wj11).equals(c.wireToBranch.get(w2j2)));
        assertFalse(c.wireToBranch.get(wj12).equals(c.wireToBranch.get(wj2b)));
    }

    /*
     * +---J---+
     * |   |   |
     * B   R   R
     * |   |   |
     * +---J---+
     */
    @Test
    public void addWireLoopConsistencyJunctionFlippedWires() throws Exception {
        Circuit c = new Circuit();
        CircuitElement b = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.BATTERY)
                .potentialDifference(5.0)
                .build();
        CircuitElement ce1 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(2.0)
                .build();
        CircuitElement ce2 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.RESISTOR)
                .resistance(2.0)
                .build();
        CircuitElement j1 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.JUNCTION)
                .build();
        CircuitElement j2 = (new CircuitElement.CircuitElementBuilder())
                .type(CircuitElementType.JUNCTION)
                .build();

        Wire wbj1 = new Wire(j1, b);
        Wire wj11 = new Wire(j1, ce1);
        Wire wj12 = new Wire(ce2, j1);
        Wire w1j2 = new Wire(ce1, j2);
        Wire w2j2 = new Wire(j2, ce2);
        Wire wj2b = new Wire(j2, b);

        c.addCircuitElement(b);
        c.addCircuitElement(ce1);
        c.addCircuitElement(ce2);
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

        c.addWire(wj11);
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

        c.addWire(w1j2);
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

        c.addWire(wj12);
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

        c.addWire(w2j2);
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
        assertTrue(c.wireToBranch.get(wj11).equals(c.wireToBranch.get(w1j2)));
        assertTrue(c.wireToBranch.get(wj12).equals(c.wireToBranch.get(w2j2)));

        assertFalse(c.wireToBranch.get(wbj1).equals(c.wireToBranch.get(w1j2)));
        assertFalse(c.wireToBranch.get(wj11).equals(c.wireToBranch.get(w2j2)));
        assertFalse(c.wireToBranch.get(wj12).equals(c.wireToBranch.get(wj2b)));
    }
}