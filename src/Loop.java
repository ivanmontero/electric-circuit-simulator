import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: Make immutable
// Two sets: wireSet, elementSet, for quick search.
// Two lists: wires, elements. These will contain the order.
public class Loop {
    public List<Wire> wires;
    public Set<CircuitElement> elements;

    public Loop() {
        this.wires = new ArrayList<>();
        this.elements = new HashSet<>();
    }

    public Loop(List<Wire> wires, Set<CircuitElement> elements) {
        this.wires = wires;
        this.elements = elements;
    }

    public String toString() {
        if (wires.isEmpty()) {
            return "empty";
        } else if (wires.size() == 1) {
            return wires.get(0).a.toString() + "->" + wires.get(0).b.toString() + "->/";
        }
        StringBuilder sb = new StringBuilder();
        Wire startWire = wires.get(0);
        CircuitElement start;
        CircuitElement curr;
        if (wires.get(1).containsEndpoint(startWire.b)) {
            sb.append(startWire.a);
            start = startWire.a;
            curr = startWire.b;
        } else {
            sb.append(startWire.b);
            start = startWire.b;
            curr = startWire.a;
        }
        for (int i = 1; i < wires.size(); i++) {
            sb.append("->").append(curr);
            curr = wires.get(i).next(curr);
            if (i == wires.size() - 1) {
                if (!curr.equals(start)) {
                    // Broken loop
                    sb.append("->").append(curr).append("->/");
                } else {
                    sb.append("->").append("(start)");
                }
            }
        }
        return sb.toString();
    }

    public <T> boolean isMutuallyExclusive(Set<T> s1, Set<T> s2, T e) {
        return (s1.contains(e) && !s2.contains(e)) ||
                (!s1.contains(e) && s2.contains(e));
    }

    public boolean hasCircuitElement(CircuitElement e) {
        return this.elements.contains(e);
    }

    public boolean hasWire(Wire w) {
        return this.wires.contains(w);
    }

    public Loop copy() {
        return new Loop(new ArrayList<>(this.wires), new HashSet<>(this.elements));
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Loop)) {
            return false;
        } else {
            Loop o = (Loop) other;
            return this.elements.equals(o.elements) && new HashSet<>(this.wires).equals(new HashSet<>(o.wires));
        }
    }

    @Override
    public int hashCode() {
        return 31 * new HashSet<>(wires).hashCode() + elements.hashCode();
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
                CircuitElement prev = elements.contains(w.a) ? w.a : w.b;
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
            if (this.elementSet.add(ce)) {
                this.elements.add(ce);
                return true;
            }
            return false;
        }

        // Removes the end wire.
        public void removeWire() {
            this.wires.remove(this.wires.size() - 1);
            this.elements.remove(this.elements.size() - 1);
        }

        public boolean isComplete() {
            return this.elements.get(0).equals(this.wires.get(this.wires.size() - 1).next(this.elements.get(this.elements.size() - 1)));
        }

        public boolean hasWire(Wire w) {
            return this.wires.contains(w);
        }

        public Loop build() {
            assert isComplete();
            // TODO: Add constructor to Loop
            return null;
        }
    }
}
