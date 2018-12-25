package com.imontero.circuit;

import java.util.*;

// TODO: Allow removal of wires

/**
 * A Circuit is a representation of an electric circuit composed of ideal wires and several electric
 * components (resistors, junctions, etc.) that can compute the electric current through and
 * potential difference across its elements.
 */
public class Circuit {
    /** The wires contained in this circuit. */
    public Set<Wire> wires;
    /** The elements contained in this circuit. */
    public Set<CircuitElement> elements;
    /** The Kirchhoff loops contained in this circuit configuration. */
    public Set<Loop> loops;
    /** The mapping from individual wires to the "Branch" they are contained in. */
    public Map<Wire, Branch> wireToBranch;
    /**
     * The "active" branches contained in this circuit configuration. (e.g. The consecutive wires
     * that will contain the same current. An "active" branch is one that can possibly have a
     * current through it.
     */
    public Set<Branch> branches;
    /** The junctions in this circuit that contain more than two active wires */
    public Set<CircuitElement> junctions;

    /**
     * Instantiates a new, empty circuit.
     */
    public Circuit() {
        this.wires = new HashSet<>();
        this.elements = new HashSet<>();
        this.loops = new HashSet<>();
    }

    /**
     * Adds a new circuit element to this.
     *
     * @param ce The circuit element to insert into the circuit.
     */
    public void addCircuitElement(CircuitElement ce) {
        this.elements.add(ce);
    }

    /**
     * Adds the given wire to this.
     *
     * @requires Elements w.a and w.b exist in this.
     * @param w The wire to insert into the circuit.
     */
    public void addWire(Wire w) {
        if (this.wires.contains(w)) {
            return;
        }
        this.wires.add(w);

        w.b.connections.add(w);
        w.a.connections.add(w);

        // When a wire is added, it's possible that new loops were created in the circuit. We will
        // search for all the loops created by this wire.
        findLoops(w, new Loop.LoopBuilder());
        // With the possibly found loops, we will find all the active branches in this new
        // configuration.
        findBranches();
    }

    /**
     * Removes the given wire from this.
     *
     * @param w The wire to remove from this circuit.
     */
    public void removeWire(Wire w) {
        // TODO: Test
        if (!this.wires.contains(w)) {
            return;
        }
        this.wires.remove(w);

        w.b.connections.remove(w);
        w.a.connections.remove(w);

        // When a wire is removed, it's possible that loops have been broken. We will remove all
        // loops which depended on the removed wire.
        this.loops.removeIf(loop -> loop.wires.contains(w));
        // With the possibly removed loops, we will find all the active branches in this new
        // configuration.
        findBranches();
    }

    /**
     * Helper method to find loops contained in the current circuit configuration. This method is
     * recursive; it depends on previous calls to determine a loop's existence.
     *
     * @param current The current wire in consideration
     * @param path The current path taken to the current wire.
     */
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

    /**
     * Helper method to find the active branches in the current circuit configuration.
     *
     * @requires all loops in the current configuration to have been found
     */
    private void findBranches() {
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

    /**
     * Helper method to associate the given wire w with the branch c.
     *
     * @param w The wire to associate with a branch
     * @param c The branch which the wire will be associated with.
     * @requires the wire w is the next wire, whether preceding or succeeding, the branch's wires.
     */
    private void associateWireWithBranch(Wire w, Branch c) {
        wireToBranch.put(w, c);
        c.addWire(w);
        branches.add(c);
    }

    /**
     * Helper method to determine if the passed in junction is used by multiple active wires.
     *
     * @param ce The junction to evaluate
     * @requires ce is a junction
     * @return true iff this junction has multiple active wires.
     */
    private boolean isMultiJunction(CircuitElement ce) {
        // All we need to do is find one wire that is contained in one
        // loop but not the other.
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

    /**
     * Evaluates the current circuit configuration and determines the electric current through
     * each branch and element, which, then, potential difference can be determined from.
     *
     * @requires loops and branches of the current circuit configuration have been found.
     */
    public void solve() {
        // Always evaluate before accessing current values / potential
        // differences
        //
        // We know that, in given complete circuit, the amount of unknowns
        // we have are the currents (voltages can be then derived from the
        // current), which equal to the number of active branches B. Let J
        // equal to the amount of junctions. We will use Gaussian
        // Elimination on the linear Kirchhoff equations to solve for
        // these unknowns. We will need B independent equations, which we
        // can obtain by using J-1 different junction equations and
        // B-(J-1) different loop rule equations.

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

        // Run gauss elimination on the Kirchhoff equations
        gaussianElimination(mat);

        // Through back substitution, get the currents
        double[] currents = backSubstitution(mat);

        // Now, set the currents for the branches
        for (int i = 0; i < bMappings.size(); i++) {
            bMappings.get(i).current = currents[i];
        }
    }

    /**
     * Performs gaussian elimination on the given augmented matrix
     *
     * @param mat A square matrix with an extra column at the end representing the equality section.
     */
    private void gaussianElimination(double[][] mat) {
        // Assumes square matrix w/ additional last column as the
        // equal side. Assumes there is a single answer, and that
        // the rows are independent.
        int n = mat.length;
        for (int k = 0; k < n; k++) {
            // Step 1: find the kth pivot, which we will use the element
            // the largest absolute value for computational stability.
            int iMax = k;
            double vMax = mat[iMax][k];
            for (int i = k+1; i < n; i++) {
                if(Math.abs(mat[i][k]) > vMax) {
                    iMax = i;
                    vMax = Math.abs(mat[i][k]);
                }
            }

            // Swap the current row with the max row
            if (iMax != k) {
                swapRows(mat, k, iMax);
            }

            // Row elimination
            for (int i = k+1; i < n; i++) {
                // Factor f to set the kth element of the current
                // row to zero.
                double f = mat[i][k] / mat[k][k];

                // Subtract the kth row element times f from the
                // current row.
                for (int j = k+1; j < n+1; j++) {
                    mat[i][j] -= mat[k][j]*f;
                }

                // Fill lower triangular matrix with zero
                // (To rid of floating point error)
                mat[i][k] = 0.0;
            }
        }
    }

    /**
     * A helper method to swap rows in the given augmented matrix.
     *
     * @param mat A square matrix with an extra column at the end representing the equality section.
     * @param r1 The index of one of the rows to swap.
     * @param r2 The index of the other row to swap.
     */
    private void swapRows(double[][] mat, int r1, int r2) {
        // Assumes square matrix w/ additional last column as the
        // equal side.
        int n = mat.length;
        for (int j = 0; j < n + 1; j++) {
            double temp = mat[r1][j];
            mat[r1][j] = mat[r2][j];
            mat[r2][j] = temp;
        }
    }

    /**
     * Performs back substitution on the given matrix to obtain the solution to the matrix.
     *
     * @param mat The matrix to perform back substitution on.
     * @requires Gaussian elimination already done on the given matrix.
     * @return The solution to the augmented matrix.
     */
    private double[] backSubstitution(double[][] mat) {
        // Assumes square matrix w/ additional last column as the
        // equal side.
        int n = mat.length;
        double[] ans = new double[n];

        // Start calculating from the last row to the first.
        for (int i = n-1; i >= 0; i--) {
            // Start with RHS
            ans[i] = mat[i][n];

            // To solve for the value of column i, we will
            // use the previous values solved for on the
            // LHS and subtract them from the RHS
            // Since the matrix is upper triangular, we can
            // start with j+1
            for (int j = i+1; j < n; j++) {
                ans[i] -= mat[i][j] * ans[j];
            }

            // Now, divide the RHS by the coefficient of the
            // unknown being currently calculated.
            ans[i] /= mat[i][i];
        }
        return ans;
    }
}
