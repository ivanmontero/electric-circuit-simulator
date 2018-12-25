# Electric Circuit Simulator

A graphical application that utilizes Kirchhoff's laws to simulate and solve a given circuit in real time.

## Organization
 - `src/com/imontero/circuit/` contains all circuit representation and evaluation code.
 - `src/com/imontero/circuitsimulation/` contains all the graphical user interface code.
 - `tests/com/imontero/circuit/` contains all circuit representation and evaluation testing code.

## Roadmap
 - [x] Wire and simple circuit element representation
 - [x] Find loops in a given circuit configuration
 - [x] Determine branches a given circuit configuration
 - [x] Solve Kirchhoff's equations using Gaussian Elimination
 - [ ] Create graphical representation and interaction with a circuit
 - [ ] Display circuit information (current, potential difference, etc.)
 - [ ] Add capacitors, and other time-changing circuit elements
 - [ ] Add alternating current generator
 - [ ] Add graphs to show properties over time