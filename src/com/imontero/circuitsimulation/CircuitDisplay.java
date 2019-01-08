package com.imontero.circuitsimulation;

import com.imontero.circuit.Circuit;
import com.imontero.circuit.CircuitElementType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

public class CircuitDisplay extends JPanel implements ActionListener {
    public static final int TIME_INTERVAL = 16;
    public static final int SIZE = 15;
    private Timer timer;
    private JFrame window;
    private Circuit circuit;
    private boolean mousePressed = false;
    private boolean selectionMode = false;
    private CircuitElementType elementType = CircuitElementType.BATTERY;
    // change to make it compatible with wires ^
    private Vec mousePosition = Vec.of(0, 0);
    private int elapsedTime = 0;

    private Set<GJunction> junctions;
    private Set<GCircuitElement> elements;

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
        elements = new HashSet<>();

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
            // do processing here
            selectedJunction.position = clipToPin(mousePosition);
        }
        if (movingSelected) {
            selected.setPosition(clipToPin(mousePosition).add(displacement));

        }

    }

    @Override
    public void paintComponent(Graphics gOld) {
        super.paintComponent(gOld);
        Graphics2D g = (Graphics2D) gOld;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int alpha = (int)(255*.7+.3*255*Math.sin(elapsedTime/16*.1));
        for (GCircuitElement gcc : elements) {
            if (gcc == selected) {
                gcc.draw(g, alpha);
            } else {
                gcc.draw(g, 255);
            }
        }

        for (GJunction gj : junctions) {
            if (selected != null) {
                if (gj.connections.contains(selected)) {
                    gj.draw(g, alpha);
                    continue;
                }
            }
            gj.draw(g, 255);
        }
        g.setColor(Color.WHITE);
        Vec pin = clipToPin(mousePosition);
        g.fillRect(pin.x, pin.y, 1, 1);

        drawSelectionPane(g);
        drawSelectionButton(g);
    }

    public Vec toPinCoords(Vec pixelCoords) {
        return Vec.of(pixelCoords.x / SIZE, pixelCoords.y / SIZE);
    }

    public Vec toPixelCoords(Vec pinCoords) {
        return Vec.of(pinCoords.x * SIZE + SIZE/2, pinCoords.y * SIZE + SIZE/2);
    }

    public Vec clipToPin(Vec pixelCoords) {
        return toPixelCoords(toPinCoords(pixelCoords));
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

    public static int SELECTION_ELEMENT_DISPLACEMENT = 10;
    public void drawSelectionPane(Graphics2D g) {
        // draw pane
        g.setColor(new Color(23, 23, 23, 128));
        g.fillRect(0, this.getHeight() - 100, this.getWidth(), 100);
        // draw battery
        if (elementType == CircuitElementType.BATTERY) {
            g.setColor(new Color(46, 46, 46, 128));
            g.fillRect(0, this.getHeight() - 100, 100, 100);
        }
        g.setColor(Color.WHITE);
        GBattery.draw(g, Vec.of(SELECTION_ELEMENT_DISPLACEMENT,
                this.getHeight() - 100 + SELECTION_ELEMENT_DISPLACEMENT),
                Vec.of(100 - SELECTION_ELEMENT_DISPLACEMENT,
                        this.getHeight() - SELECTION_ELEMENT_DISPLACEMENT),
                false, false);

    }


    private GCircuitElement selected = null;
    private GJunction selectedJunction = null;
    private boolean movingSelected = false;
    private Vec displacement = null;
    private class MouseInput extends MouseAdapter {
        // Triggered once
        @Override
        public void mousePressed(MouseEvent e) {
            mousePressed = true;
            Vec pos = new Vec(e.getX(), e.getY());
            if ((new Rectangle2D.Double(getWidth() - 27, 7, 20, 20))
                    .contains(e.getX(), e.getY())) {
                return;
            }
            // Selection mode is when the user is editing the position of elements.
            if (!selectionMode) {
//                Vec pinPos = toPixelCoords(toPinCoords(pos));
                Vec pinPos = clipToPin(pos);
                switch (elementType) {
                    case BATTERY:
                        // CHECK IF CLICK ON EXISTING JUNCTION.
                        GBattery battery = new GBattery(circuit, pinPos,
                                Vec.of(pinPos.x, pinPos.y - GBattery.MIN_BATTERY_PIN_LENGTH), 5);
                        elements.add(battery);
//                        selected = battery;
                        selectedJunction = battery.positiveJunction;
//                        initialPos = battery.positiveJunction.position;
                        junctions.add(battery.positiveJunction);
                        junctions.add(battery.negativeJunction);
                        break;
                }
            } else {
                if (selected != null) {
                    if (selected.contains(pos)) {
                        // Do moving/translation of the element!!!!!
                        displacement = selected.getPosition().sub(clipToPin(pos));
                        System.out.println(displacement);
                        movingSelected = true;
                    } else if (selected.containsJunction(pos)) {
                        // Move junction
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
                for (GCircuitElement gce : elements) {
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
