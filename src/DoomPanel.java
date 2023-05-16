import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import graphics.Camera;
import graphics.Rasterizer;
import graphics.Triangle;

public class DoomPanel extends JPanel {
    static final int WIDTH = 1200;
    static final int HEIGHT = 600;

    private final Camera camera;

    private int mouseX;
    private int mouseY;
    private boolean firstMove;

    private Game game;

    public DoomPanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setLayout(null);
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new GKeyAdapter());
        this.addMouseMotionListener(new GMouseAdapter());

        game = new Game();


        mouseX = 0;
        mouseY = 0;
        firstMove = true;

        camera = new Camera(WIDTH, HEIGHT, "./src/graphics/test-cube.txt");
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
        g.setColor(new Color (210, 180, 140));
        g.drawRect(0, 500,1200, 600);
        g.fillRect(0, 500,1200, 600);
        g.setColor(Color.BLACK);
        Font stringFont = new Font( "Monospaced", Font.PLAIN, 25 );
        g.setFont(stringFont);
        g.drawString("Ammo", 110, 590);
        g.drawLine(300,500,300,600);
        g.drawLine(600,500,600,600);
        g.drawLine(900,500,900,600);
        g.drawString("Health", 407, 590);
        g.drawString("Inventory", 680, 590);
        g.drawString("Armor", 1010, 590);


    }
    public void draw(Graphics g) {
        for (Triangle t : camera.view()) {
            //g.setColor(t.c);
            Rasterizer.drawTexTriangle(g, t, WIDTH, HEIGHT);
            g.setColor(Color.WHITE);
            drawTriangle(g, t);
        }

    }

    public void drawPanel(Graphics g){
        g.setColor(Color.BLACK);

    }

    public static void drawTriangle(Graphics g, Triangle t) {
        g.drawLine(WIDTH-(int) t.pts[0].x, HEIGHT-(int) t.pts[0].y, WIDTH-(int) t.pts[1].x, HEIGHT-(int) t.pts[1].y);
        g.drawLine(WIDTH-(int) t.pts[0].x, HEIGHT-(int) t.pts[0].y, WIDTH-(int) t.pts[2].x, HEIGHT-(int) t.pts[2].y);
        g.drawLine(WIDTH-(int) t.pts[2].x, HEIGHT-(int) t.pts[2].y, WIDTH-(int) t.pts[1].x, HEIGHT-(int) t.pts[1].y);
    }

    public class GKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            final double movement = 0.3;
            final double rotation = 0.2;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_SPACE -> camera.moveY(movement);
                case KeyEvent.VK_SHIFT -> camera.moveY(-movement);
                case KeyEvent.VK_D -> camera.moveRightLeft(movement);
                case KeyEvent.VK_A -> camera.moveRightLeft(-movement);
                case KeyEvent.VK_W -> camera.moveForBack(movement);
                case KeyEvent.VK_S -> camera.moveForBack(-movement);
                case KeyEvent.VK_LEFT -> camera.turnRightLeft(-rotation);
                case KeyEvent.VK_RIGHT -> camera.turnRightLeft(rotation);
                //case KeyEvent.VK_UP -> camera.turnUpDown(rotation);
                //case KeyEvent.VK_DOWN -> camera.turnUpDown(-rotation);
            }
            repaint();
        }
        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

    public class GMouseAdapter extends MouseAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            if (!firstMove) {
                //camera.turnUpDown(-0.005*(e.getYOnScreen() - mouseY));
                camera.turnRightLeft(.005*(e.getXOnScreen() - mouseX));
            }
            else {
                firstMove = false;
            }
            mouseX = e.getXOnScreen();
            mouseY = e.getYOnScreen();

            repaint();
        }
    }
}
