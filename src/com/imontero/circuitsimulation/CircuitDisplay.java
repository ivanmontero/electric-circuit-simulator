package com.imontero.circuitsimulation;

import com.imontero.circuit.Circuit;
import com.imontero.circuit.CircuitElement;
import com.imontero.circuit.Wire;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class CircuitDisplay extends JPanel implements ActionListener {
    public static final int TIME_INTERVAL = 16;
    private Timer timer;
    private JFrame window;
    private Circuit circuit;
    private boolean mousePressed = false;
    private Point mousePosition;

    private GBattery batt;

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

        batt = new GBattery(new Point(window.getWidth()/2, window.getHeight()/2),
                new Point(window.getWidth()/2, window.getHeight()/2+30), 5);

        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    public void update() {
        if (mousePressed) {
            if (batt.aSelected) {
                batt.setNegativePinPosition(mousePosition);
            }
            if (batt.bSelected) {
                batt.setPositivePinPosition(mousePosition);
            }
        }
    }

    @Override
    public void paintComponent(Graphics gOld) {
        super.paintComponent(gOld);
        Graphics2D g = (Graphics2D) gOld;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        batt.draw(g);

        drawSelectionPane(g);
    }


    public void drawSelectionPane(Graphics2D g) {
        g.setColor(new Color(23, 23, 23, 128));
        g.fillRect(0, this.getHeight() - 100, this.getWidth(), 100);
        //
    }

    // TODO: Make series components be the component, connected to wires
    // and each "pin" being a junction....
    private class GBattery implements Drawable {
        public final int PIN_RADIUS = 5;
        public final int BATTERY_GAP = 6;
        public final int BATTERY_POS_WIDTH = 50;
        public final int BATTERY_NEG_WIDTH = 30;
        public final int MIN_BATTERY_PIN_LENGTH = 50;
        public final int LINE_WIDTH = 2;

        private CircuitElement battery;
        private Wire a;
        private Wire b;
        private Point aPin;
        private Point bPin;

        public boolean aSelected, bSelected;

        public GBattery(Point aPin, Point bPin, double potentialDifference) {
            setNegativePinPosition(aPin);
            setPositivePinPosition(bPin);
            battery = new CircuitElement.CircuitElementBuilder()
                    .potentialDifference(potentialDifference)
                    .build();
            circuit.addCircuitElement(battery);
        }

        public void connectToPositivePin(Wire w) {
            this.b = w;
            if (this.a != null) {
                this.battery.connections.set(0, a);
                this.battery.connections.set(1, b);
                this.battery.setDirection(a, b);
            }
        }

        public void connectToNegativePin(Wire w) {
            this.a = w;
            if (this.b != null) {
                this.battery.connections.set(0, a);
                this.battery.connections.set(1, b);
                this.battery.setDirection(a, b);
            }
        }

        public void setNegativePinPosition(Point p) {
            if (bPin != null && bPin.distance(p) < MIN_BATTERY_PIN_LENGTH) {
                double rad = Math.atan2(p.y - bPin.y, p.x - bPin.x);
                p = new Point(bPin.x + (int)(MIN_BATTERY_PIN_LENGTH * Math.cos(rad)),
                        (int) (bPin.y + MIN_BATTERY_PIN_LENGTH * Math.sin(rad)));
            }
            this.aPin = p;
        }

        public void setPositivePinPosition(Point p) {
            if (aPin != null && aPin.distance(p) < MIN_BATTERY_PIN_LENGTH) {
                double rad = Math.atan2(p.y - aPin.y, p.x - aPin.x);
                p = new Point(aPin.x + (int)(MIN_BATTERY_PIN_LENGTH * Math.cos(rad)),
                        (int) (aPin.y + MIN_BATTERY_PIN_LENGTH * Math.sin(rad)));
            }
            this.bPin = p;
        }

        public void translate(int dx, int dy) {
            this.aPin = new Point(aPin.x + dx, aPin.y + dy);
            this.bPin = new Point(bPin.x + dx, aPin.y + dy);
        }

        @Override
        public void draw(Graphics2D g) {
            double rad = Math.atan2(bPin.y - aPin.y, bPin.x - aPin.x) + Math.PI /2;
            Point mid = new Point((aPin.x + bPin.x) / 2, (aPin.y+bPin.y) / 2);
            AffineTransform old = g.getTransform();
            g.setStroke(new BasicStroke(LINE_WIDTH));
            g.translate(mid.x, mid.y);
            g.rotate(rad);
            g.setColor(Color.WHITE);
            int dist = (int) bPin.distance(aPin);
            g.drawOval(-PIN_RADIUS, dist/2-PIN_RADIUS,
                    2*PIN_RADIUS, 2*PIN_RADIUS);
            g.drawLine(0, BATTERY_GAP / 2, 0, dist/2 - PIN_RADIUS);
            g.drawLine(-BATTERY_NEG_WIDTH/2, BATTERY_GAP / 2,
                    BATTERY_NEG_WIDTH / 2, BATTERY_GAP / 2);
            g.drawLine(-BATTERY_POS_WIDTH/2, -BATTERY_GAP / 2,
                    BATTERY_POS_WIDTH / 2, -BATTERY_GAP / 2);
            g.drawLine(0, -BATTERY_GAP / 2, 0, -dist/2 + PIN_RADIUS);
            g.drawOval(-PIN_RADIUS, -dist/2-PIN_RADIUS,
                    2*PIN_RADIUS, 2*PIN_RADIUS);
            g.setTransform(old);
        }

        public boolean positivePinContains(Point p) {
            return (new Ellipse2D.Double(bPin.x - PIN_RADIUS, bPin.y - PIN_RADIUS,
                    PIN_RADIUS*2, PIN_RADIUS*2)).contains(p);
        }

        public boolean negativePinContains(Point p) {
            return (new Ellipse2D.Double(aPin.x - PIN_RADIUS, aPin.y - PIN_RADIUS,
                    PIN_RADIUS*2, PIN_RADIUS*2)).contains(p);
        }

        public boolean batteryContains(Point p) {
            double rad = Math.atan2(bPin.y - aPin.y, bPin.x - aPin.x) + Math.PI/2;
            int dist = (int) bPin.distance(aPin);
            Point mid = new Point((aPin.x + bPin.x) / 2, (aPin.y+bPin.y) / 2);
            AffineTransform at = new AffineTransform();
            at.translate(mid.x, mid.y);
            at.rotate(rad);
            return at.createTransformedShape(new Rectangle2D.Double(-20/2,
                    -dist/2 + PIN_RADIUS,
                    20, dist - PIN_RADIUS*2)).contains(p);
        }
    }

    private class MouseInput extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            mousePressed = true;
            if (batt.positivePinContains(mousePosition)) {
                batt.bSelected = true;
            }
            if (batt.negativePinContains(mousePosition)) {
                batt.aSelected = true;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mousePressed = false;
            batt.aSelected = false;
            batt.bSelected = false;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mousePosition = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            mousePosition = e.getPoint();
        }
    }

    // TODO: FIX DRAGGING............
    private class KeyboardInput extends KeyAdapter {

    }

    private interface Drawable {
        void draw(Graphics2D g);
    }
}
