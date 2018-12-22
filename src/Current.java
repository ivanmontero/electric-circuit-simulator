import java.util.ArrayList;
import java.util.List;

public class Current {
    private static int currentIdCounter = 0;
    public final int ID;
    public List<Wire> wires;
    public List<CircuitElement> elements;
    public int magnitude;

    public Current() {
        this.ID = currentIdCounter++;
        this.wires = new ArrayList<>();
        this.elements = new ArrayList<>();
        this.magnitude = 0;
    }

    // Must be the NEXT wire in the sequence
    public void addWire(Wire w) {
        wires.add(w);
        if (this.wires.size() == 1) {
            this.elements.add(w.a);
            this.elements.add(w.b);
        } else if (this.wires.size() == 2) {
            CircuitElement prev = elements.contains(w.a) ? w.a : w.b;
            if (!elements.get(1).equals(prev)) {
                // Swap elements
                elements.add(0, elements.remove(1));
            }
            elements.add(w.next(elements.get(elements.size() - 1)));
        } else {
            elements.add(w.next(elements.get(elements.size()-1)));
        }
    }

    /**
     * Returns the direction this current points (for loop rule)
     * positive if a -> b
     * negative if a <- b
     * zero if the two are the same.
     */
    public int getDirection(CircuitElement a, CircuitElement b) {
        return elements.indexOf(b) - elements.indexOf(a);
    }

    @Override
    public String toString() {
        return "" +  this.ID;
    }
}
