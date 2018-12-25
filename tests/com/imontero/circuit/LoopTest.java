package com.imontero.circuit;

import com.imontero.circuit.CircuitElement;
import com.imontero.circuit.CircuitElementType;
import com.imontero.circuit.Loop;
import com.imontero.circuit.Wire;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class LoopTest {
    public static final CircuitElement CE1 = new CircuitElement.CircuitElementBuilder()
            .type(CircuitElementType.JUNCTION)
                .build();
    public static final CircuitElement CE2 = new CircuitElement.CircuitElementBuilder()
            .type(CircuitElementType.JUNCTION)
            .build();
    public static final CircuitElement CE3 = new CircuitElement.CircuitElementBuilder()
            .type(CircuitElementType.JUNCTION)
            .build();

    @Test
    public void hasCircuitElementSimple() {
        Loop.LoopBuilder lb = new Loop.LoopBuilder();
        Wire w1 = new Wire(CE1, CE2);
        Wire w2 = new Wire(CE2, CE1);
        lb.addWire(w1);
        lb.addWire(w2);

        Loop l = lb.build();

        assertTrue(l.hasCircuitElement(CE1));
        assertTrue(l.hasCircuitElement(CE2));
    }

    @Test
    public void hasWireSimple() {
        Loop.LoopBuilder lb = new Loop.LoopBuilder();
        Wire w1 = new Wire(CE1, CE2);
        Wire w2 = new Wire(CE2, CE1);
        lb.addWire(w1);
        lb.addWire(w2);

        Loop l = lb.build();

        assertTrue(l.hasWire(w1));
        assertTrue(l.hasWire(w2));
    }

    @Test(expected = IllegalStateException.class)
    public void builderIncompleteBuild() {
        Loop.LoopBuilder lb = new Loop.LoopBuilder();
        Wire w1 = new Wire(CE1, CE2);

        Loop l = lb.build();
    }

    @Test
    public void builderAddOrderSimple() {
        Loop.LoopBuilder lb = new Loop.LoopBuilder();
        Wire w1 = new Wire(CE1, CE2);
        Wire w2 = new Wire(CE2, CE3);

        assertTrue(lb.elements.isEmpty() && lb.wires.isEmpty());

        assertTrue(lb.addWire(w1));

        assertTrue(lb.wires.size() == 1 && lb.wires.contains(w1));
        assertTrue(lb.elements.size() == 2 && lb.elements.containsAll(
                List.of(CE1, CE2)
        ));

        assertTrue(lb.addWire(w2));

        assertTrue(lb.wires.size() == 2 && lb.wires.containsAll(List.of(w1, w2)));
        assertTrue(lb.elements.size() == 3 && lb.elements.containsAll(
                List.of(CE1, CE2, CE3)
        ));
        assertTrue(lb.elements.get(0).equals(CE1) && lb.elements.get(1).equals(CE2)
                && lb.elements.get(2).equals(CE3)
        );
        assertTrue(lb.wires.get(0).equals(w1) && lb.wires.get(1).equals(w2));

        assertTrue(!lb.isComplete());
    }

    @Test
    public void builderAddLoop() {
        Loop.LoopBuilder lb = new Loop.LoopBuilder();
        Wire w1 = new Wire(CE1, CE2);
        Wire w2 = new Wire(CE2, CE1);

        assertTrue(lb.elements.isEmpty() && lb.wires.isEmpty());

        assertTrue(lb.addWire(w1));

        assertTrue(lb.wires.size() == 1 && lb.wires.contains(w1));
        assertTrue(lb.elements.size() == 2 && lb.elements.containsAll(
                List.of(CE1, CE2)
        ));

        assertTrue(lb.addWire(w2));

        assertTrue(lb.wires.size() == 2 && lb.wires.containsAll(List.of(w1, w2)));
        assertTrue(lb.elements.size() == 2 && lb.elements.containsAll(
                List.of(CE1, CE2)
        ));
        assertTrue(lb.elements.get(0).equals(CE1) && lb.elements.get(1).equals(CE2));
        assertTrue(lb.wires.get(0).equals(w1) && lb.wires.get(1).equals(w2));

        assertTrue(lb.isComplete());
    }

    @Test
    public void builderAddOrderSimpleFlipped() {
        Loop.LoopBuilder lb = new Loop.LoopBuilder();
        Wire w1 = new Wire(CE2, CE1);
        Wire w2 = new Wire(CE3, CE2);

        assertTrue(lb.elements.isEmpty() && lb.wires.isEmpty());

        assertTrue(lb.addWire(w1));

        assertTrue(lb.wires.size() == 1 && lb.wires.contains(w1));
        assertTrue(lb.elements.size() == 2 && lb.elements.containsAll(
                List.of(CE1, CE2)
        ));

        assertTrue(lb.addWire(w2));

        assertTrue(lb.wires.size() == 2 && lb.wires.containsAll(List.of(w1, w2)));
        assertTrue(lb.elements.size() == 3 && lb.elements.containsAll(
                List.of(CE1, CE2, CE3)
        ));
        assertTrue(lb.elements.get(0).equals(CE1) && lb.elements.get(1).equals(CE2)
                && lb.elements.get(2).equals(CE3)
        );
        assertTrue(lb.wires.get(0).equals(w1) && lb.wires.get(1).equals(w2));

        assertTrue(!lb.isComplete());
    }

    @Test
    public void builderRemoveOrderSimple() {
        Loop.LoopBuilder lb = new Loop.LoopBuilder();
        Wire w1 = new Wire(CE2, CE1);
        Wire w2 = new Wire(CE3, CE2);
        lb.addWire(w1);
        lb.addWire(w2);

        lb.removeWire();

        assertTrue(lb.wires.size() == 1 && lb.wires.contains(w1));
        assertTrue(lb.elements.size() == 2 && lb.elements.containsAll(
                List.of(CE1, CE2)
        ));
        assertTrue(lb.elements.get(0).equals(CE1) && lb.elements.get(1).equals(CE2));
        assertTrue(lb.wires.get(0).equals(w1));

        assertTrue(!lb.isComplete());
    }

}