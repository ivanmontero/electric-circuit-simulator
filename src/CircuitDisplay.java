import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CircuitDisplay extends JPanel implements ActionListener {
    public static final int TIME_INTERVAL = 16;
    private Timer timer;
    private JFrame window;
    private boolean mousePressed = false;

    public CircuitDisplay(JFrame window) {
        timer = new Timer(TIME_INTERVAL, this);

        this.setFocusable(true);
        this.grabFocus();

        this.window = window;
        window.addMouseListener(new MouseInput());
        window.addKeyListener(new KeyboardInput());

        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    public void update() {
//        System.out.println(mousePressed);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        g2d.setColor(new Color(23, 23, 23));
        g2d.fillRect(0, this.getHeight() - 100, this.getWidth(), 100);
    }

    private class MouseInput extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            mousePressed = true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mousePressed = false;
        }
    }

    private class KeyboardInput extends KeyAdapter {

    }
}
