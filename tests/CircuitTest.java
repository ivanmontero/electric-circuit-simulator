import org.junit.Test;

import static org.junit.Assert.*;

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
        assertTrue(c.loops.isEmpty());

        c.addWire(w12);
        assertTrue(c.loops.isEmpty());

        c.addWire(w21);
        assertTrue(c.loops.size() == 1);
    }

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
        assertTrue(c.loops.isEmpty());

        c.addWire(w12);
        assertTrue(c.loops.isEmpty());

        c.addWire(w21);
        assertTrue(c.loops.size() == 1);
    }

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
        assertTrue(c.loops.isEmpty());
        assertTrue(c.elements.size() == 5);

        c.addWire(wbj1);
        assertTrue(c.loops.isEmpty());

        c.addWire(wj11);
        assertTrue(c.loops.isEmpty());

        c.addWire(w1j2);
        assertTrue(c.loops.isEmpty());

        c.addWire(wj2b);
        assertTrue(c.loops.size() == 1);

        c.addWire(wj12);
        assertTrue(c.loops.size() == 1);

        c.addWire(w2j2);
        assertTrue(c.loops.size() == 3);
    }

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
        assertTrue(c.loops.isEmpty());
        assertTrue(c.elements.size() == 5);

        c.addWire(wbj1);
        assertTrue(c.loops.isEmpty());

        c.addWire(wj11);
        assertTrue(c.loops.isEmpty());

        c.addWire(w1j2);
        assertTrue(c.loops.isEmpty());

        c.addWire(wj2b);
        assertTrue(c.loops.size() == 1);

        c.addWire(wj12);
        assertTrue(c.loops.size() == 1);

        c.addWire(w2j2);
        assertTrue(c.loops.size() == 3);
    }
}