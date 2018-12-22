import java.util.*;

public class Circuit {
    public Set<Wire> wires;
    public Set<CircuitElement> elements;
    public Set<Loop> loops;
    public Map<Wire, Current> wireToCurrent;
    public Map<Wire, Integer> loopUsage;

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
        wireToCurrent = new HashMap<>();
        Set<CircuitElement> junctions = new HashSet<>();

//        for (Loop l : loops) {
//            for (int i = 0; i < l.elements.size(); i++) {
//                CircuitElement e = l.elements.get(i);
//                if (e.type.PINS == 2 || e.connections.size() == 2) {
//                    Wire a = e.connections.get(0);
//                    Wire b = e.connections.get(1);
//                    if (!wireToCurrent.containsKey(a) &&
//                            !wireToCurrent.containsKey(b)) {
//                        Current c = new Current();
//                        c.addWire(a);
//                        c.addWire(b);
//                        wireToCurrent.put(a, c);
//                        wireToCurrent.put(b, c);
//                    } else if (!wireToCurrent.containsKey(a)) {
//                        Current bc = wireToCurrent.get(b);
//                        bc.addWire(a);
//                        wireToCurrent.put(a, bc);
//                    } else if (!wireToCurrent.containsKey(b)) {
//                        Current ac = wireToCurrent.get(b);
//                        ac.addWire(b);
//                        wireToCurrent.put(b, ac);
//                    }
//                } else {
//                    // multipin
//                    // before the junction
//                    Wire a = l.wires.get(l.loopCorrection(i-1));
//                    Wire b = l.wires.get(i);
//                    for (Wire w : e.connections) {
//
//                    }
//                }
//            }
//        }
        // Since the first wire in a loop HAS to have a current, we will
        // add it to the list.
        for (Loop l : loops) {
            Wire prev = l.wires.get(l.elements.size() - 1);
            if (!wireToCurrent.containsKey(prev)) {
                wireToCurrent.put(prev, new Current());
                wireToCurrent.get(prev).addWire(prev);
            }
            for (int i = 0; i < l.wires.size()-1; i++) {
                Wire curr = l.wires.get(i);
                CircuitElement ce = curr.getSharedEndpoint(prev);
                if (wireToCurrent.containsKey(curr)) {
                    prev = curr;
                    continue;
                }
                if (ce.type.PINS == 2) {
                    // In this case, both have the same current
                    Current c = wireToCurrent.get(prev);
                    c.addWire(curr);
                    wireToCurrent.put(curr, c);
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
                                    if (!l.equals(ol)) {
                                        if (ol.hasWire(w)) {
                                            Current c = new Current();
                                            c.addWire(curr);
                                            wireToCurrent.put(curr, c);
                                            junctions.add(ce);
                                            break;
                                        }
                                    }
                                }
                                if (wireToCurrent.containsKey(curr)) {
                                    break;
                                }
                            }
                        }
                    }
                    if (!wireToCurrent.containsKey(curr)) {
                        Current c = wireToCurrent.get(prev);
                        c.addWire(curr);
                        wireToCurrent.put(curr, c);
                    }
                }
                prev = curr;
            }
        }
        // Quick sanity check: Make sure that all 2 pin elements have the
        // same current
        for (CircuitElement ce : elements) {
            if (ce.type.PINS == 2 && ce.connections.size() == 2) {
                Wire a = ce.connections.get(0);
                Wire b = ce.connections.get(1);
                if (wireToCurrent.containsKey(a) && wireToCurrent.containsKey(b)) {
                    if (!wireToCurrent.get(a).equals(wireToCurrent.get(b))) {
                        // Arbitrary choice: choose the first one
                        wireToCurrent.put(b, wireToCurrent.get(a));
                        wireToCurrent.get(a).addWire(b);
                    }
                }

            }
        }
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
