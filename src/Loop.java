import java.util.*;

public class Loop {
    public final List<Wire> wires;
    private final Set<Wire> wireSet;
    public final List<CircuitElement> elements;
    private final Set<CircuitElement> elementSet;

    private Loop(LoopBuilder lb) {
        this.wires = Collections.unmodifiableList(
                new ArrayList<>(lb.wires));
        this.elements = Collections.unmodifiableList(
                new ArrayList<>(lb.elements));
        this.wireSet = Collections.unmodifiableSet(
                new HashSet<>(this.wires));
        this.elementSet = Collections.unmodifiableSet(
                new HashSet<>(this.elements));
    }

    public boolean hasCircuitElement(CircuitElement e) {
        return this.elementSet.contains(e);
    }

    public boolean hasWire(Wire w) {
        return this.wireSet.contains(w);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Loop)) {
            return false;
        } else {
            Loop o = (Loop) other;
            return this.elementSet.equals(o.elementSet) && this.wireSet.equals(o.wireSet);
        }
    }

    @Override
    public int hashCode() {
        return 31 * this.wireSet.hashCode() + elementSet.hashCode();
    }

    public static class LoopBuilder {
        public Set<CircuitElement> elementSet;
        public List<Wire> wires;
        public List<CircuitElement> elements;

        public LoopBuilder() {
            this.wires = new ArrayList<>();
            this.elements = new ArrayList<>();
            this.elementSet = new HashSet<>();
        }

        // True if wire addition was successful
        public boolean addWire(Wire w) {
            this.wires.add(w);
            if (elements.isEmpty()) {
                // New wire
                addCircuitElement(w.a);
                addCircuitElement(w.b);
                return true;
            } else if (wires.size() == 2) {
                // Case where this is the second wire.
                CircuitElement prev = elementSet.contains(w.a) ? w.a : w.b;
                if (!elements.get(1).equals(prev)) {
                    // Swap elements
                    elements.add(0, elements.remove(1));
                }
                addCircuitElement(w.next(prev));
                return true;
            } else {
                return addCircuitElement(w.next(this.elements.get(this.elements.size() - 1)))
                        || isComplete();
            }
        }

        // Returns true if insertion was successful
        private boolean addCircuitElement(CircuitElement ce) {
            assert ce != null;
            if (this.elementSet.add(ce)) {
                this.elements.add(ce);
                return true;
            }
            return false;
        }

        // Removes the end wire.
        public void removeWire() {
            this.wires.remove(this.wires.size() - 1);
            CircuitElement removed = this.elements.remove(this.elements.size() - 1);
            this.elementSet.remove(removed);
        }

        public boolean isComplete() {
            return this.wires.size() > 1 &&
                    this.elements.get(0).equals(
                    this.wires.get(this.wires.size() - 1)
                            .next(this.elements.get(this.elements.size() - 1)));
        }

        public boolean hasWire(Wire w) {
            return this.wires.contains(w);
        }

        public CircuitElement last() {
            return this.elements.get(this.elements.size() - 1);
        }

        public Loop build() {
            assert isComplete();
            return new Loop(this);
        }

        public String toString() {
            if (elements.isEmpty()) {
                return "empty";
            }
            StringBuilder sb = new StringBuilder(elements.get(0).toString());
            for (int i = 1; i < elements.size(); i++) {
                sb.append("->").append(elements.get(i));
            }
            sb.append(isComplete() ? "->(start)" : "->/");
            return sb.toString();
        }
    }
}
