package com.imontero.circuitsimulation;

import com.imontero.circuit.Circuit;
import com.imontero.circuit.CircuitElement;
import com.imontero.circuit.CircuitElementType;
import com.imontero.circuit.Wire;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

public class CircuitDisplay extends JPanel implements ActionListener {
    public static final int TIME_INTERVAL = 16;
    private Timer timer;
    private JFrame window;
    private Circuit circuit;
    private boolean mousePressed = false;
    private boolean selectionMode = false;
    private CircuitElementType elementType = CircuitElementType.BATTERY;
    private Vec mousePosition;
    private int elapsedTime = 0;

    private Set<GJunction> junctions;
    private Set<GCircuitComponent> components;

//    private GBattery batt;

    public CircuitDisplay(JFrame window) {
        timer = new Timer(TIME_INTERVAL, this);

        this.setFocusable(true);
        this.grabFocus();
        this.setBackground(Color.BLACK);

        this.window = window;
        MouseAdapter mouse = new MouseInput();
        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);
        this.addKeyListener(new KeyboardInput());

        circuit = new Circuit();

        junctions = new HashSet<>();
        components = new HashSet<>();

        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        elapsedTime += TIME_INTERVAL;
        update();
        repaint();
    }

    public void update() {
        if (selectedJunction != null) {
//            if (bPin != null && bPin.distance(p) < MIN_BATTERY_PIN_LENGTH) {
//
//            }
            selectedJunction.position = mousePosition;
        }
        if (movingSelected) {
            selected.setPosition(mousePosition.add(displacement));
        }

    }

    @Override
    public void paintComponent(Graphics gOld) {
        super.paintComponent(gOld);
        Graphics2D g = (Graphics2D) gOld;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int alpha = (int)(255*.7+.3*255*Math.sin(elapsedTime/16*.1));
        for (GCircuitComponent gcc : components) {
            if (gcc == selected) {
                gcc.draw(g, alpha);
            } else {
                gcc.draw(g);
            }
        }

        for (GJunction gj : junctions) {
            if (selected != null) {
                if (gj.connections.contains(selected)) {
                    gj.draw(g, alpha);
                    continue;
                }
            }
            gj.draw(g);
        }

        drawSelectionPane(g);
        drawSelectionButton(g);
    }

    public void drawSelectionButton(Graphics2D g) {
        int[] xPoints = {-5, -5, -1, 2, 4, 1, 6};
        int[] yPoints = {-8,  8,  3, 9, 8, 2, 2};
        AffineTransform af = g.getTransform();
        g.translate(getWidth() - 17, 17);
        af.scale(1.1, 1.1);
        g.setColor(selectionMode ? Color.WHITE : Color.BLACK);
        g.fillRect(-10, -10, 20, 20);
        g.setColor(selectionMode ? Color.BLACK : Color.WHITE);
        g.fillPolygon(xPoints, yPoints, xPoints.length);
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.GRAY);
        g.drawRect(-10, -10, 20, 20);
        g.setTransform(af);
    }

    public void drawSelectionPane(Graphics2D g) {
        g.setColor(new Color(23, 23, 23, 128));
        g.fillRect(0, this.getHeight() - 100, this.getWidth(), 100);
    }


    private GCircuitComponent selected = null;
    private GJunction selectedJunction = null;
    private boolean movingSelected = false;
    private Vec displacement = null;
    private class MouseInput extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            mousePressed = true;
            Vec pos = new Vec(e.getX(), e.getY());
            if ((new Rectangle2D.Double(getWidth() - 27, 7, 20, 20))
                    .contains(e.getX(), e.getY())) {
                return;
            }
            if (!selectionMode) {
                switch (elementType) {
                    case BATTERY:
                        // CHECK IF CLICK ON EXISTING JUNCTION.
                        GBattery battery = new GBattery(circuit, pos,
                                Vec.of(pos.x, pos.y - GBattery.MIN_BATTERY_PIN_LENGTH), 5);
                        components.add(battery);
//                        selected = battery;
                        selectedJunction = battery.positiveJunction;
//                        initialPos = battery.positiveJunction.position;
                        junctions.add(battery.positiveJunction);
                        junctions.add(battery.negativeJunction);
                }
            } else {
                if (selected != null) {
                    if (selected.contains(pos)) {
                        // Do moving/translation of the element
//                        initialPos =
                        displacement = selected.getPosition().sub(pos);
                        System.out.println(displacement);
                        movingSelected = true;
                    } else {
                        selectedJunction = selected.getJunction(pos);
                        // Do moving of the junction
                    }
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mousePressed = false;
            selectedJunction = null;
            movingSelected = false;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            Vec pos = new Vec(e.getX(), e.getY());
            if ((new Rectangle2D.Double(getWidth() - 27, 7, 20, 20))
                    .contains(e.getX(), e.getY())) {
                selectionMode = !selectionMode;
                selected = null;
            } else if (selectionMode) {
                for (GCircuitComponent gce : components) {
                    if (gce.contains(pos)) {
                        selected = gce;
                        break;
                    }
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mousePosition = Vec.fromPoint(e.getPoint());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            mousePosition = Vec.fromPoint(e.getPoint());
        }
    }

    private class KeyboardInput extends KeyAdapter {

    }
}
