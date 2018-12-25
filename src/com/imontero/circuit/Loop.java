package com.imontero.circuit;

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

    public boolean hasCircuitElement(CircuitElement ce) {
        return this.elementSet.contains(ce);
    }

    public boolean hasWire(Wire w) {
        return this.wireSet.contains(w);
    }

    public CircuitElement next(CircuitElement curr) {
        return this.elements.get((this.elements.indexOf(curr) + 1) % this.elements.size());
    }

    public Wire next(Wire curr) {
        return this.wires.get((this.wires.indexOf(curr) + 1) % this.wires.size());
    }

    // True if in same direction, false otherwise
    public boolean getDirection(CircuitElement a, CircuitElement b) {
        int ai = elements.indexOf(a);
        int bi = elements.indexOf(b);
        if ((ai == 0 && bi == elements.size()-1) || (ai == elements.size()-1 && bi == 0)) {
            // in this case, an edge case, we reverse the result.
            return ai-bi > 0;
        } else {
            return bi-ai > 0;
        }
    }

    // True if in same direction, false otherwise
    public boolean getDirection(Wire a, Wire b) {
        int ai = wires.indexOf(a);
        int bi = wires.indexOf(b);
        if ((ai == 0 && bi == wires.size()-1) || (ai == wires.size()-1 && bi == 0)) {
            // in this case, an edge case, we reverse the result.
            return ai-bi > 0;
        } else {
            return bi-ai > 0;
        }
    }

    public Wire getNextWire(CircuitElement ce) {
        return wires.get(elements.indexOf(ce));
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(elements.get(0).toString());
        for (int i = 1; i < this.elements.size(); i++) {
            sb.append("->").append(this.elements.get(i));
        }
        return sb.append("->(start)").toString();
    }

    public static class LoopBuilder {
        public List<CircuitElement> elements;
        public List<Wire> wires;

        public LoopBuilder() {
            this.wires = new ArrayList<>();
            this.elements = new ArrayList<>();
        }

        // True if wire addition was successful
        public boolean addWire(Wire w) {
            this.wires.add(w);
            if (elements.isEmpty()) {
                // New wire
                addCircuitElement(w.a);
                addCircuitElement(w.b);
            } else if (wires.size() == 2) {
                // Case where this is the second wire.
                CircuitElement prev = elements.contains(w.a) ? w.a : w.b;
                if (!elements.get(1).equals(prev)) {
                    // Swap elements
                    elements.add(0, elements.remove(1));
                }
                addCircuitElement(w.next(prev));
            } else {
                if (!addCircuitElement(w.next(this.elements.get(this.elements.size() - 1)))) {
                    this.wires.remove(w);
                    return false;
                }
            }
            return true;
        }

        // Returns true if insertion was successful
        private boolean addCircuitElement(CircuitElement ce) {
            assert ce != null;
            if (!this.elements.contains(ce)) {
                this.elements.add(ce);
                return true;
            }
            return false;
        }

        // Removes the end wire.
        public void removeWire() {
            this.elements.remove(this.elements.size() - 1);
            this.wires.remove(this.wires.size() - 1);
        }

        public boolean isComplete() {
            return this.wires.size() > 1 &&
                    this.elements.size() == this.wires.size() &&
                    this.elements.get(0).equals(
                    this.wires.get(this.wires.size() - 1)
                            .next(this.elements.get(this.elements.size() - 1)));
        }

        public boolean isComplete(Wire w) {
            this.wires.add(w);
            boolean complete = isComplete();
            this.wires.remove(w);
            return complete;
        }

        public boolean hasWire(Wire w) {
            return this.wires.contains(w);
        }

        public CircuitElement last() {
            return this.elements.get(this.elements.size() - 1);
        }

        public Loop build() {
            if (!isComplete()) {
                throw new IllegalStateException("com.imontero.circuit.Loop must be complete.");
            }
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
