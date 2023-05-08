import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import graphics.Camera;
import graphics.Triangle;

public class DoomPanel extends JPanel {
    static final int WIDTH = 640;
    static final int HEIGHT = 5*WIDTH/8;
    //a little messed up bc og doom has rectangle pixels
    private Camera camera;

    public DoomPanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setLayout(null);
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new DoomKeyAdapter());

        camera = new Camera(WIDTH, HEIGHT, "./src/graphics/test-cube.txt");
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        draw(g);
    }
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        for (Triangle t : camera.view()) {
            drawTri(g, t);
        }
        //g.drawString("DOOM", WIDTH/2, HEIGHT/2);
    }
    private static void drawTri(Graphics g, Triangle t) {
        g.drawLine(WIDTH-(int) t.p1.x, HEIGHT-(int) t.p1.y, WIDTH-(int) t.p2.x, HEIGHT-(int) t.p2.y);
        g.drawLine(WIDTH-(int) t.p1.x, HEIGHT-(int) t.p1.y, WIDTH-(int) t.p3.x, HEIGHT-(int) t.p3.y);
        g.drawLine(WIDTH-(int) t.p3.x, HEIGHT-(int) t.p3.y, WIDTH-(int) t.p2.x, HEIGHT-(int) t.p2.y);
    }

    public class DoomKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_SPACE -> camera.moveY(1);
                case KeyEvent.VK_SHIFT -> camera.moveY(-1);
                case KeyEvent.VK_D -> camera.moveRightLeft(1);
                case KeyEvent.VK_A -> camera.moveRightLeft(-1);
                case KeyEvent.VK_W -> camera.moveForBack(1);
                case KeyEvent.VK_S -> camera.moveForBack(-1);
                case KeyEvent.VK_LEFT -> camera.turnRightLeft(-0.2);
                case KeyEvent.VK_RIGHT -> camera.turnRightLeft(0.2);
                case KeyEvent.VK_UP -> camera.turnUpDown(0.2);
                case KeyEvent.VK_DOWN -> camera.turnUpDown(-0.2);
            }
            repaint();
        }
        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
