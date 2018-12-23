import java.util.*;

// TODO: Allow removal of wires
// TODO: Independent Equations
public class Circuit {
    public Set<Wire> wires;
    public Set<CircuitElement> elements;
    public Set<Loop> loops;
    public Map<Wire, Branch> wireToBranch;
    // Only holds "active" branches: one that possibly have a
    // current.
    public Set<Branch> branches;
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
        //   - if more than 2, we have different branches; create different branches
        //     for the branches, and store the junction for possible use with
        //     junction rule.
        wireToBranch = new HashMap<>();
        junctions = new HashSet<>();
        branches = new HashSet<>();
        // Since the first wire in a loop HAS to have a current, we will
        // add it to the list.
        for (Loop l : loops) {
            Wire prev = l.wires.get(l.wires.size() - 1);
            if (!wireToBranch.containsKey(prev)) {
                associateWireWithBranch(prev, new Branch());
            }
            for (int i = 0; i < l.wires.size()-1; i++) {
                Wire curr = l.wires.get(i);
                if (wireToBranch.containsKey(curr)) {
                    prev = curr;
                    continue;
                }
                CircuitElement ce = curr.getSharedEndpoint(prev);
                if (ce.type.PINS == 2) {
                    // In this case, both have the same current
                    associateWireWithBranch(curr, wireToBranch.get(prev));
                } else {
                    // Assuming it can have any amount of pins

                    // to determine if we treat it differently, we will see if
                    // there any other valid branches (e.g., owned by a
                    // different loop)
                    if (isMultiJunction(ce)) {
                        if (l.next(ce).type.PINS == 2 &&
                                    wireToBranch.containsKey(l.next(curr))) {
                                associateWireWithBranch(curr, wireToBranch.get(l.next(curr)));
                            } else {
                                associateWireWithBranch(curr, new Branch());
                            }
                            junctions.add(ce);
                    } else {
                        associateWireWithBranch(curr, wireToBranch.get(prev));
                    }
                }
                prev = curr;
            }
            // Since we started with prev equal to the last wire, we skipped
            // evaluating the very last circuit element. It is possible that
            // this element is a multi junction.
            CircuitElement last = l.elements.get(l.elements.size()-1);
            if (last.type.PINS != 2 && isMultiJunction(last)) {
                junctions.add(last);
            }
        }
    }

    // All we need to do is find one wire that is contained in one
    // loop but not the other.
    private boolean isMultiJunction(CircuitElement ce) {
        for (Wire w : ce.connections) {
            for (Loop l : loops) {
                for (Loop ol : loops) {
                    if (!l.equals(ol)) {
                        if ((l.hasWire(w) && !ol.hasWire(w))
                                || (!l.hasWire(w) && ol.hasWire(w))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void associateWireWithBranch(Wire w, Branch c) {
        wireToBranch.put(w, c);
        c.addWire(w);
        branches.add(c);
    }

    // We know that, in given complete circuit, the amount of unknowns
    // we have are the currents (voltages can be then derived from the
    // current), which equal to the number of active branches B. Let J
    // equal to the amount of junctions. We will use Gaussian
    // Elimination on the linear Kirchhoff equations to solve for
    // these unknowns. We will need B independent equations, which we
    // can obtain by using J-1 different junction equations and
    // B-(J-1) different loop rule equations.
    public void solve() {
        // Get independent equations
        int b = branches.size(), j = junctions.size();

        List<CircuitElement> iJunctions = new ArrayList<>();
        ArrayList<Loop> iLoops = new ArrayList<>();
        Iterator<CircuitElement> jIter = junctions.iterator();
        while (j > 0 && iJunctions.size() < j-1) {
            iJunctions.add(jIter.next());
        }
        Iterator<Loop> lIter = loops.iterator();
        while (iLoops.size() + iJunctions.size() < branches.size()) {
            iLoops.add(lIter.next());
        }
        assert branches.size() == iJunctions.size() + iLoops.size();

        // Create augmented matrix to solve.
        // Bx(B+1) matrix, last column is the "solution" part.
        double[][] mat = new double[b][b+1];
        // We will map branches (currents) to a specific index.
        List<Branch> bMappings = new ArrayList<>();
        Iterator<Branch> bIter = branches.iterator();
        for (int i = 0; i < b; i++) {
            bMappings.add(i, bIter.next());
        }

        // Populate the matrix

        // Start with loop equations
        for (int i = 0; i < iLoops.size(); i++) {
            Loop l = iLoops.get(i);
            for (CircuitElement ce : l.elements) {
                if (ce.type.PINS != 2) {
                    continue;
                }
                Branch br = wireToBranch.get(l.getNextWire(ce));
                switch (ce.type) {
                    case BATTERY:
                        // Negative, since we are putting it on the other side
                        // of the equality (the "equal" side)
                        mat[i][b] +=
                                -ce.potentialDifference
                                        * ce.getPotentialDifferenceSign(l, br);
                        break;
                    case RESISTOR:
                        mat[i][bMappings.indexOf(br)] +=
                                ce.resistance * ce.getPotentialDifferenceSign(l, br);
                    default:
                        break;
                }
            }
        }

        // Add junction equations
        for (int i = 0; i < iJunctions.size(); i++) {
            CircuitElement ce = iJunctions.get(i);
            // Find currents that are in and out.
            for (Wire w : ce.connections) {
                if (wireToBranch.containsKey(w)) {
                    Branch br = wireToBranch.get(w);
                    int row = iLoops.size() + i;
                    if (br.getDirection(ce, w.next(ce))) {
                        // If in same direction as going out from junction
                        mat[row][bMappings.indexOf(br)] = -1;
                    } else {
                        mat[row][bMappings.indexOf(br)] = 1;
                    }
                }
            }
        }

        // TODO: Gaussian Elimination
    }

//    // True if the potential difference is positive. MUST be a 2 pin element.
//    private boolean isPotentialDifferencePositive() {
//
//    }
}
