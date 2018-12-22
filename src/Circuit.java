import java.util.*;

public class Circuit {
    public Set<Wire> wires;
    public Set<CircuitElement> elements;
    public Set<Loop> loops;
    public Map<Wire, Current> wireToCurrent;
    public Set<Current> currents;
    public Set<CircuitElement> junctions;

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

        findLoops(w, new Loop.LoopBuilder());
        findCurrents();
    }

    private void findLoops(Wire current, Loop.LoopBuilder path) {
        if (path.isComplete(current)) {
            path.wires.add(current);
            loops.add(path.build());
            path.wires.remove(path.wires.size() - 1);
        } else if (path.addWire(current)) {
            for (Wire w : path.last().connections) {
                if (!w.equals(current)) {
                    findLoops(w, path);
                }
            }
            path.removeWire();
        }

    }

    public void findCurrents() {
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
        wireToCurrent = new HashMap<>();
        junctions = new HashSet<>();
        currents = new HashSet<>();
        // Since the first wire in a loop HAS to have a current, we will
        // add it to the list.
        for (Loop l : loops) {
            Wire prev = l.wires.get(l.elements.size() - 1);
            if (!wireToCurrent.containsKey(prev)) {
                associateWireWithCurrent(prev, new Current());
            }
            for (int i = 0; i < l.wires.size()-1; i++) {
                Wire curr = l.wires.get(i);
                if (wireToCurrent.containsKey(curr)) {
                    prev = curr;
                    continue;
                }
                CircuitElement ce = curr.getSharedEndpoint(prev);
                if (ce.type.PINS == 2) {
                    // In this case, both have the same current
                    associateWireWithCurrent(curr, wireToCurrent.get(prev));
                } else {
                    // Assuming it can have any amount of pins

                    // to determine if we treat it differently, we will see if
                    // there any other valid branches (e.g., owned by a
                    // different loop)
                    if (loops.size() > 1) {
                        for (Wire w : ce.connections) {
                            if (!w.equals(prev) && !w.equals(curr)) {
                                // check if any other loops own this wire
                                for (Loop ol : loops) {
                                    if (!l.equals(ol) && ol.hasWire(w)) {
                                        // check if element after junction
                                        // is one that has a 2 pin that is
                                        // set
                                        if (l.next(ce).type.PINS == 2 &&
                                                wireToCurrent.containsKey(l.next(curr))) {
                                            associateWireWithCurrent(curr, wireToCurrent.get(l.next(curr)));
                                        } else {
                                            associateWireWithCurrent(curr, new Current());
                                        }
                                        junctions.add(ce);
                                        break;
                                    }
                                }
                                if (wireToCurrent.containsKey(curr)) {
                                    break;
                                }
                            }
                        }
                    }
                    if (!wireToCurrent.containsKey(curr)) {
                        associateWireWithCurrent(curr, wireToCurrent.get(prev));
                    }
                }
                prev = curr;
            }
        }
    }

    private void associateWireWithCurrent(Wire w, Current c) {
        wireToCurrent.put(w, c);
        c.addWire(w);
        currents.add(c);
    }

    public void solve() {

    }
}
