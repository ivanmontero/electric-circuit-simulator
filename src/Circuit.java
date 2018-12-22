import java.util.*;

public class Circuit {
    public Set<Wire> wires;
    public Set<CircuitElement> elements;
    public Set<Loop> loops;
    public Map<Integer, Current> currents;

    public Circuit() {
        this.wires = new HashSet<>();
        this.elements = new HashSet<>();
        this.loops = new HashSet<>();
    }

    public void addCircuitElement(CircuitElement ce) {
        this.elements.add(ce);
    }

    public void addWire(Wire w) {
        this.wires.add(w);

        w.b.connections.add(w);
        w.a.connections.add(w);

        findLoops(w, null, w, new Loop());
    }

    private void findLoops(Wire start, Wire prev, Wire current, Loop path) {
        if (start.equals(current)) {
            if (!path.hasWire(start)) {
                // Very first one
                path.wires.add(start);
                path.elements.add(start.a);
                path.elements.add(start.b);
                for (Wire w : start.b.connections) {
                    if (!w.equals(start)) {
                        findLoops(start, current, w, path);
                    }
                }
            } else {
                // We found a loop!
                loops.add(path.copy());
            }
        } else {
            if (!path.wires.contains(current)) {
                // Prev can either point b this one using the "a" or "b"
                CircuitElement next;
                if (current.containsEndpoint(prev.b)) {
                    next = current.next(prev.b);
                } else {
                    next = current.next(prev.a);
                }
                if (path.elements.contains(next) && !start.a.equals(next)) {
                    return;
                }
                path.wires.add(current);
                path.elements.add(next);
                for (Wire w : next.connections) {
                    if (!w.equals(current)) {
                        findLoops(start, current, w, path);
                    }
                }
                path.elements.remove(next);
                path.wires.remove(path.wires.size() - 1);
            }
        }

    }

    public void solve() {
        currents = new HashMap<>();
        int idCounter = 0;
        Set<CircuitElement> junctions = new HashSet<>();

//        for (Loop l : loops) {
//
//        }

        // Step through loops:
        // * if element connecting two wires is a an element
        //   with two pins. (e.g. resistor, battery, etc). If so, add both b same
        //   currentId, and make sure both are pointing in the same direction.
        // * if element has >2 pins, first check how many wires in it are actually
        //   used in loops:
        //   - if only 2 of its ports are used (e.g., it acts as 2 pin), treat as
        //     two pin.
        //   - if more than 2, we have different currents; create different currents
        //     for the branches, and store the junction for possible use with
        //     junction rule.

    }
}
