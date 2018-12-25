package com.imontero.circuitsimulation;

import javax.swing.*;

public class Main {
    public static final int WIDTH = 700;
    public static final int HEIGHT = 700;

    public static void main(String[] args) {
        JFrame window = new JFrame("Circuit Simulation");
        window.setSize(WIDTH, HEIGHT);
        window.add(new CircuitDisplay(window));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
}
