import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

import graphics.Rasterizer;
import graphics.Triangle;
import map_stuff.DoomLevel;

public class DoomPanel extends JPanel implements ActionListener {
    static final int WIDTH = 1200;
    static final int HEIGHT = 600;

    private DoomLevel level;

    private int mouseX;
    private boolean firstMove;

    private boolean active;
    private int stage;
    
    private Timer timer;

    public DoomPanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setLayout(null);
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new GKeyAdapter());
        this.addMouseMotionListener(new GMouseAdapter());

        mouseX = 0;
        firstMove = true;

        active = false;
        stage = 0; //0 is very start, 1 is options, 2 is playing

        level = new DoomLevel("./assets/txt/DoomBasic.txt", WIDTH, HEIGHT, 1.00, 100);

        timer = new Timer(17, this); //60 fps
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (stage != 0) draw3D(g);
        drawStartScreen(g);

        if (active) {
            g.setColor(Color.WHITE);
            g.fillRect(WIDTH/2-2, HEIGHT/2-12, 4, 24);
            g.fillRect(WIDTH/2-12, HEIGHT/2-2, 24, 4);

            drawPanelBkgd(g);
            drawPanel(g);
        }

        drawOverlays(g);

        drawEndscreens(g);
    }

    private void drawOverlays(Graphics g) {
        if (stage < 2) return;
        Graphics2D g2 = (Graphics2D) g;

        if (active && level.player.shotTime > -1) {
            g2.setColor(Color.RED);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.25f));
            g2.fillRect(0, 0, WIDTH, 500);
        }
        if (!active) {
            g2.setColor(Color.BLACK);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
            g2.fillRect(0, 0, WIDTH, HEIGHT);
        }

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1f));
    }
    private void drawPanelBkgd(Graphics g) {
        g.setColor(new Color(0.7f, 0.7f, 0.7f));//new Color (210, 180, 140));
        g.drawRect(0, 500,1200, HEIGHT-500);
        g.fillRect(0, 500,1200, HEIGHT-500);
        g.setColor(Color.BLACK);
        Font stringFont = new Font( "OCR A Extended", Font.PLAIN, 25 );
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
    }
    public void draw3D(Graphics g) {
        List<Triangle> view = level.camera.view();
        for (Triangle t : view) {
            if (t.texture != null) {
                Rasterizer.drawTexTriangle(g, t, WIDTH, HEIGHT);
            }
            else {
                g.setColor(t.c);
                if (t.attributes[1].contains("SHOT")) {
                    g.setColor(Color.RED);
                }
                fillTriangle(g, t);
            }
        }

    }
    public void drawPanel(Graphics g){
        g.setColor(Color.RED);
        String health = Integer.toString(level.player.getHealth());
        String ammo = Integer.toString(level.player.getAmmo());
        String armor = Integer.toString(level.player.getArmor());
        Font stringFont = new Font( "OCR A Extended", Font.BOLD, 45 );
        g.setFont(stringFont);
        g.drawString(health + "%",400 + (3-health.length())*15,550);
        g.drawString(armor + "%",995 + (3-armor.length())*15,550);
        g.drawString(ammo,100 + (3-ammo.length())*15,550);
        int inventory = level.player.getInventorySize();
        int mult = 0;
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));

        while (inventory>0)
        {
            g2.setColor(Color.RED);
            if (level.player.getEquipped().equals(level.player.getInventory()[mult])) {
                if (level.player.timeSinceFired >= level.player.getFireDelay()) {
                    g2.setColor(Color.WHITE);
                }
                else {
                    g2.setColor(Color.GRAY);
                }
            }
            g2.drawRect(606 + mult*49, 515,45, 45);
            g2.drawString(Integer.toString(mult+1), 615 + mult*49, 555);
            mult ++;
            inventory--;
        }
        int remaining = 6-level.player.getInventorySize();
        g2.setColor(Color.BLACK);
        while (remaining>0)
        {
            g2.drawRect(606 + mult*49, 515,45, 45);
            g2.drawString(Integer.toString(mult+1), 615 + mult*49, 555);
            mult ++;
            remaining--;
        }


    }
    private void drawEndscreens(Graphics g) {
        g.setColor(Color.WHITE);
        Font stringFont = new Font( "OCR A Extended", Font.BOLD, 100 );
        g.setFont(stringFont);

        FontMetrics metrics = g.getFontMetrics(stringFont);

        if (level.getGameState() == 1) {
            String winStr = "you win i guess";
            g.drawString(winStr, (WIDTH-metrics.stringWidth(winStr))/2, (HEIGHT-metrics.getHeight())/2+metrics.getAscent());
        }
        if (level.getGameState() == -1) {
            String loseStr = "YOU LOSE";
            String optStr = "press enter to restart";
            g.drawString(loseStr, (WIDTH-metrics.stringWidth(loseStr))/2, (HEIGHT-metrics.getHeight())/2+metrics.getAscent());

            g.setFont(new Font( "OCR A Extended", Font.BOLD, 25 ));
            metrics = g.getFontMetrics(g.getFont());
            g.drawString(optStr, (WIDTH-metrics.stringWidth(optStr))/2, (HEIGHT-metrics.getHeight())/2+metrics.getAscent()+75);
        }
    }
    private void drawStartScreen(Graphics g) {
        if (stage == 0) {
            try {
                g.drawImage(ImageIO.read(new File("./assets/img/title-screen.png")), 0, 0,
                        WIDTH, HEIGHT, null); //TODO: edit title img
            } catch (IOException e) {
                e.printStackTrace();
            }

//            String optStr = "Press enter";
//            g.setColor(Color.YELLOW);
//            Font stringFont = new Font( "OCR A Extended", Font.BOLD, 40 );
//            FontMetrics metrics = g.getFontMetrics(stringFont);
//            g.setFont(stringFont);
//            g.drawString(optStr, (WIDTH-metrics.stringWidth(optStr))/2, HEIGHT-40);
        }
        else if (stage == 1) {
            try {
                g.drawImage(ImageIO.read(new File("./assets/img/title-screen.png")), 0, 0,
                        WIDTH, HEIGHT, null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            g.setColor(new Color(0.7f, 0.7f, 0.7f));
            g.fillRect(WIDTH/2 - 150, HEIGHT/2 - 155,300, 310);

            g.setColor(Color.YELLOW);
            Font stringFont = new Font( "OCR A Extended", Font.BOLD, 15);
            FontMetrics metrics = g.getFontMetrics(stringFont);
            g.setFont(stringFont);
            int i = 1;
            try {
                BufferedReader br = new BufferedReader(new FileReader("./assets/txt/exposition.txt"));
                String s;
                while ((s = br.readLine()) != null) {
                    g.drawString(s, (WIDTH-metrics.stringWidth(s))/2, HEIGHT/2-155+15+15*i);
                    i++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    public static void drawTriangle(Graphics g, Triangle t) {
        g.drawLine(WIDTH-(int) t.pts[0].x, HEIGHT-(int) t.pts[0].y, WIDTH-(int) t.pts[1].x, HEIGHT-(int) t.pts[1].y);
        g.drawLine(WIDTH-(int) t.pts[0].x, HEIGHT-(int) t.pts[0].y, WIDTH-(int) t.pts[2].x, HEIGHT-(int) t.pts[2].y);
        g.drawLine(WIDTH-(int) t.pts[2].x, HEIGHT-(int) t.pts[2].y, WIDTH-(int) t.pts[1].x, HEIGHT-(int) t.pts[1].y);
    }
    public static void fillTriangle(Graphics g, Triangle t) {
        int[] x = {WIDTH-(int) t.pts[0].x, WIDTH-(int) t.pts[1].x, WIDTH-(int) t.pts[2].x};
        int[] y = {HEIGHT-(int) t.pts[0].y, HEIGHT-(int) t.pts[1].y, HEIGHT-(int) t.pts[2].y};
        Polygon p = new Polygon(x, y, 3);
        g.fillPolygon(p);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();

        if (!active) return;

        //update game here
        if (level.getGameState() == 1) {
            active = false;
            //win
        }
        else if (level.getGameState() == -1) {
            active = false;
            //lose
        }
        else {
            level.update();
        }
    }

    public static void music() {
        try {
            File musicPath = new File("./");
            if(musicPath.exists()){
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
            }
            else{

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public class GKeyAdapter extends KeyAdapter {
        final double movement = 25;
        final double rotation = 0.1;
        @Override
        public void keyPressed(KeyEvent e) {
            if (stage == 0 || stage == 1) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> stage++;
                }
            }
            if (active) {
                switch (e.getKeyCode()) {
                    //case KeyEvent.VK_CONTROL -> level.camera.moveY(movement);
                    //case KeyEvent.VK_SHIFT -> level.camera.moveY(-movement);
                    case KeyEvent.VK_D -> level.strafe(movement);//level.camera.moveRightLeft(movement);
                    case KeyEvent.VK_A -> level.strafe(-movement);//level.camera.moveRightLeft(-movement);
                    case KeyEvent.VK_W -> level.walk(movement);//level.camera.moveForBack(movement);
                    case KeyEvent.VK_S -> level.walk(-movement);//level.camera.moveForBack(-movement);
                    case KeyEvent.VK_LEFT -> level.camera.turnRightLeft(-rotation);
                    case KeyEvent.VK_RIGHT -> level.camera.turnRightLeft(rotation);
                    case KeyEvent.VK_SPACE -> level.shoot();
                    //case KeyEvent.VK_Z -> level.pickUp();
                    case KeyEvent.VK_1 -> level.player.equipt(1);
                    case KeyEvent.VK_2 -> level.player.equipt(2);
                    case KeyEvent.VK_3 -> level.player.equipt(3);
                    case KeyEvent.VK_4 -> level.player.equipt(4);
                    //case KeyEvent.VK_UP -> level.camera.turnUpDown(rotation);
                    //case KeyEvent.VK_DOWN -> level.camera.turnUpDown(-rotation);

                }
            }
            else if (stage == 2) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> {
                        level.restart();
                        active = true;
                    }
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
        }
    }
}
