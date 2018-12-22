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

    // Must be the NEXT wire in the sequence, either in the beginning or end.
    public void addWire(Wire w) {
        if (this.wires.isEmpty()) {
            wires.add(w);
            this.elements.add(w.a);
            this.elements.add(w.b);
        } else if (this.wires.size() == 1) {
            wires.add(w);
            CircuitElement prev = elements.contains(w.a) ? w.a : w.b;
            if (!elements.get(1).equals(prev)) {
                // Swap elements
                elements.add(0, elements.remove(1));
            }
            elements.add(w.next(elements.get(elements.size() - 1)));
        } else {
            CircuitElement next = w.next(elements.get(elements.size()-1));
            if (next == null) {
                wires.add(0, w);
                elements.add(0, w.next(elements.get(0)));
            } else {
                wires.add(w);
                elements.add(next);
            }
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
    public boolean equals(Object other) {
        return other instanceof Current && ((Current)other).ID == this.ID;
    }

    @Override
    public int hashCode() {
        return 31 * this.ID;
    }

    @Override
    public String toString() {
        return "" +  this.ID;
    }
}
