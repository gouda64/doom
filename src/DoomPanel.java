import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import graphics.Triangle;
import map_stuff.DoomLevel;

public class DoomPanel extends JPanel implements ActionListener {
    static final int WIDTH = 1200;
    static final int HEIGHT = 600;

    private DoomLevel level;

    private int mouseX;
    private int mouseY;
    private boolean firstMove;

    private boolean active;

    private Game game;
    private Timer timer;

    public DoomPanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setLayout(null);
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new GKeyAdapter());
        this.addMouseMotionListener(new GMouseAdapter());

        game = new Game();

        timer = new Timer(17, this);
        timer.start();

        mouseX = 0;
        mouseY = 0;
        firstMove = true;

        active = true;

//        camera = new camera(WIDTH, HEIGHT, "./assets/Doom_E1M1.txt",
//                            new Point(-3150, 100, -3150), new Point(0, 0, 1), 700);

        level = new DoomLevel("./assets/DoomTest.txt", WIDTH, HEIGHT, 1.5, 100);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);

        g.setColor(Color.WHITE);
        g.fillRect(WIDTH/2-2, HEIGHT/2-12, 4, 24);
        g.fillRect(WIDTH/2-12, HEIGHT/2-2, 24, 4);
//        g.setColor(Color.BLACK);
//        g.drawRect(WIDTH/2-2, HEIGHT/2-12, 4, 24);
//        g.drawRect(WIDTH/2-12, HEIGHT/2-2, 24, 4);

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
        g.setColor(Color.WHITE);
        g.drawRect(575, 400, 50, 100);

        drawPanel(g);


    }
    public void draw(Graphics g) {
        List<Triangle> view = level.camera.view();
        for (int i = 0; i < view.size(); i++) {
            Triangle t = view.get(i);
            //Rasterizer.drawTexTriangle(g, t, WIDTH, HEIGHT);
            //g.setColor(Color.WHITE);

            g.setColor(t.c);

            if (t.attributes[1].contains("SHOT")) {
                int num = Integer.parseInt(t.attributes[1].substring(5));
                double max = 0.25 * timer.getDelay() * 3.6;
                //TODO: move to step (not that important anyways)
                if (num > max) {
                    t.attributes[1] = "";
                } else if (i == 0 || t.attributes != view.get(i-1).attributes) {
                    t.attributes[1] = "SHOT " + (num + 1);
                }

                g.setColor(new Color((int) Math.min(255, t.c.getRed() + max - Math.abs(max / 2 - num)),
                        t.c.getGreen(), t.c.getBlue()));
            }
            fillTriangle(g, t);
        }

    }

    public void drawPanel(Graphics g){
        g.setColor(Color.RED);
        String health = Integer.toString(game.getPlayer().getHealth());
        String ammo = Integer.toString(game.getPlayer().getAmmo());
        String armor = Integer.toString(game.getPlayer().getArmor());
        Font stringFont = new Font( "Monospaced", Font.BOLD, 45 );
        g.setFont(stringFont);
        g.drawString(health + "%",400 + (3-health.length())*15,550);
        g.drawString(armor + "%",995 + (3-armor.length())*15,550);
        g.drawString(ammo,100 + (3-ammo.length())*15,550);
        int inventory = game.getPlayer().getInventorySize();
        int mult = 0;
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));

        while (inventory>0)
        {
            g2.drawRect(606 + mult*49, 515,45, 45);
            g2.drawString(Integer.toString(mult+1), 615 + mult*49, 555);
            mult ++;
            inventory--;
        }
        int remaining = 6-game.getPlayer().getInventorySize();
        g2.setColor(Color.BLACK);
        while (remaining>0)
        {
            g2.drawRect(606 + mult*49, 515,45, 45);
            g2.drawString(Integer.toString(mult+1), 615 + mult*49, 555);
            mult ++;
            remaining--;
        }


    }

    public static void drawTriangle(Graphics g, Triangle t) {
        g.drawLine(WIDTH-(int) t.pts[0].x, HEIGHT-(int) t.pts[0].y, WIDTH-(int) t.pts[1].x, HEIGHT-(int) t.pts[1].y);
        g.drawLine(WIDTH-(int) t.pts[0].x, HEIGHT-(int) t.pts[0].y, WIDTH-(int) t.pts[2].x, HEIGHT-(int) t.pts[2].y);
        g.drawLine(WIDTH-(int) t.pts[2].x, HEIGHT-(int) t.pts[2].y, WIDTH-(int) t.pts[1].x, HEIGHT-(int) t.pts[1].y);
    }
    public void fillTriangle(Graphics g, Triangle t) {
        int[] x = {WIDTH-(int) t.pts[0].x, WIDTH-(int) t.pts[1].x, WIDTH-(int) t.pts[2].x};
        int[] y = {HEIGHT-(int) t.pts[0].y, HEIGHT-(int) t.pts[1].y, HEIGHT-(int) t.pts[2].y};
        Polygon p = new Polygon(x, y, 3);
        g.fillPolygon(p);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!active) return;

        //update game here
        if (level.getGameState() == 1) {
            System.out.println("won!");
            active = false;
            //win
        }
        else if (level.getGameState() == -1) {
            active = false;
            //lose
        }
        else {
            level.step();
        }

        repaint();
    }

    public class GKeyAdapter extends KeyAdapter {
        final double movement = 50;
        final double rotation = 0.2;
        @Override
        public void keyPressed(KeyEvent e) {
            if (active) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE -> level.camera.moveY(movement);
                    case KeyEvent.VK_SHIFT -> level.camera.moveY(-movement);
                    case KeyEvent.VK_D -> level.strafe(movement);//level.camera.moveRightLeft(movement);
                    case KeyEvent.VK_A -> level.strafe(-movement);//level.camera.moveRightLeft(-movement);
                    case KeyEvent.VK_W -> level.walk(movement);//level.camera.moveForBack(movement);
                    case KeyEvent.VK_S -> level.walk(-movement);//level.camera.moveForBack(-movement);
                    case KeyEvent.VK_LEFT -> level.camera.turnRightLeft(-rotation);
                    case KeyEvent.VK_RIGHT -> level.camera.turnRightLeft(rotation);
                    case KeyEvent.VK_CONTROL -> level.shoot();
                    //case KeyEvent.VK_UP -> level.camera.turnUpDown(rotation);
                    //case KeyEvent.VK_DOWN -> level.camera.turnUpDown(-rotation);
                }
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

    public class GMouseAdapter extends MouseAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            if (active) {
                if (!firstMove) {
                    //level.camera.turnUpDown(-0.005*(e.getYOnScreen() - mouseY));
                    level.camera.turnRightLeft(.03*(e.getXOnScreen() - mouseX));
                }
                else {
                    firstMove = false;
                }
            }
            mouseX = e.getXOnScreen();
            mouseY = e.getYOnScreen();
        }
    }
}
