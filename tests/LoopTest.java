import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class LoopTest {

    @Test
    public void builderAddOrderSimple() {
        Loop.LoopBuilder lb = new Loop.LoopBuilder();
        CircuitElement ce1 = new CircuitElement.CircuitElementBuilder()
                .type(CircuitElementType.JUNCTION)
                .build();
        CircuitElement ce2 = new CircuitElement.CircuitElementBuilder()
                .type(CircuitElementType.JUNCTION)
                .build();
        CircuitElement ce3 = new CircuitElement.CircuitElementBuilder()
                .type(CircuitElementType.JUNCTION)
                .build();
        Wire w1 = new Wire(ce1, ce2);
        Wire w2 = new Wire(ce2, ce3);

        assertTrue(lb.elements.isEmpty() && lb.wires.isEmpty());

        assertTrue(lb.addWire(w1));

        assertTrue(lb.wires.size() == 1 && lb.wires.contains(w1));
        assertTrue(lb.elements.size() == 2 && lb.elements.containsAll(
                List.of(ce1, ce2)
        ));

        assertTrue(lb.addWire(w2));

        assertTrue(lb.wires.size() == 2 && lb.wires.containsAll(List.of(w1, w2)));
        assertTrue(lb.elements.size() == 3 && lb.elements.containsAll(
                List.of(ce1, ce2, ce3)
        ));
        assertTrue(lb.elements.get(0).equals(ce1) && lb.elements.get(1).equals(ce2)
                && lb.elements.get(2).equals(ce3)
        );
        assertTrue(lb.wires.get(0).equals(w1) && lb.wires.get(1).equals(w2));

        assertTrue(!lb.isComplete());
    }

    @Test
    public void builderAddLoop() {
        Loop.LoopBuilder lb = new Loop.LoopBuilder();
        CircuitElement ce1 = new CircuitElement.CircuitElementBuilder()
                .type(CircuitElementType.JUNCTION)
                .build();
        CircuitElement ce2 = new CircuitElement.CircuitElementBuilder()
                .type(CircuitElementType.JUNCTION)
                .build();
        Wire w1 = new Wire(ce1, ce2);
        Wire w2 = new Wire(ce2, ce1);

        assertTrue(lb.elements.isEmpty() && lb.wires.isEmpty());

        assertTrue(lb.addWire(w1));

        assertTrue(lb.wires.size() == 1 && lb.wires.contains(w1));
        assertTrue(lb.elements.size() == 2 && lb.elements.containsAll(
                List.of(ce1, ce2)
        ));

        assertTrue(lb.addWire(w2));

        assertTrue(lb.wires.size() == 2 && lb.wires.containsAll(List.of(w1, w2)));
        assertTrue(lb.elements.size() == 2 && lb.elements.containsAll(
                List.of(ce1, ce2)
        ));
        assertTrue(lb.elements.get(0).equals(ce1) && lb.elements.get(1).equals(ce2));
        assertTrue(lb.wires.get(0).equals(w1) && lb.wires.get(1).equals(w2));

        assertTrue(lb.isComplete());
    }

    @Test
    public void builderAddOrderSimpleFlipped() {
        Loop.LoopBuilder lb = new Loop.LoopBuilder();
        CircuitElement ce1 = new CircuitElement.CircuitElementBuilder()
                .type(CircuitElementType.JUNCTION)
                .build();
        CircuitElement ce2 = new CircuitElement.CircuitElementBuilder()
                .type(CircuitElementType.JUNCTION)
                .build();
        CircuitElement ce3 = new CircuitElement.CircuitElementBuilder()
                .type(CircuitElementType.JUNCTION)
                .build();
        Wire w1 = new Wire(ce2, ce1);
        Wire w2 = new Wire(ce3, ce2);

        assertTrue(lb.elements.isEmpty() && lb.wires.isEmpty());

        assertTrue(lb.addWire(w1));

        assertTrue(lb.wires.size() == 1 && lb.wires.contains(w1));
        assertTrue(lb.elements.size() == 2 && lb.elements.containsAll(
                List.of(ce1, ce2)
        ));

        assertTrue(lb.addWire(w2));

        assertTrue(lb.wires.size() == 2 && lb.wires.containsAll(List.of(w1, w2)));
        assertTrue(lb.elements.size() == 3 && lb.elements.containsAll(
                List.of(ce1, ce2, ce3)
        ));
        assertTrue(lb.elements.get(0).equals(ce1) && lb.elements.get(1).equals(ce2)
                && lb.elements.get(2).equals(ce3)
        );
        assertTrue(lb.wires.get(0).equals(w1) && lb.wires.get(1).equals(w2));

        assertTrue(!lb.isComplete());
    }

    @Test
    public void builderRemoveOrderSimple() {
        Loop.LoopBuilder lb = new Loop.LoopBuilder();
        CircuitElement ce1 = new CircuitElement.CircuitElementBuilder()
                .type(CircuitElementType.JUNCTION)
                .build();
        CircuitElement ce2 = new CircuitElement.CircuitElementBuilder()
                .type(CircuitElementType.JUNCTION)
                .build();
        CircuitElement ce3 = new CircuitElement.CircuitElementBuilder()
                .type(CircuitElementType.JUNCTION)
                .build();
        Wire w1 = new Wire(ce2, ce1);
        Wire w2 = new Wire(ce3, ce2);
        lb.addWire(w1);
        lb.addWire(w2);

        lb.removeWire();

        assertTrue(lb.wires.size() == 1 && lb.wires.contains(w1));
        assertTrue(lb.elements.size() == 2 && lb.elements.containsAll(
                List.of(ce1, ce2)
        ));
        assertTrue(lb.elements.get(0).equals(ce1) && lb.elements.get(1).equals(ce2));
        assertTrue(lb.wires.get(0).equals(w1));

        assertTrue(!lb.isComplete());
    }

}