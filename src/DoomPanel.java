import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import graphics.Rasterizer;
import graphics.Triangle;
import map_stuff.DoomLevel;

public class DoomPanel extends JPanel implements ActionListener {
    static final int WIDTH = 1200;
    static final int HEIGHT = 600;

    private boolean sound = true;

    private DoomLevel level;
    private Clip bkgMusic;

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
        stage = 0; //0 is very start, 1 is options, 2+ is playing

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

            drawEquipped(g);
            drawPanelBkgd(g);
            drawPanel(g);
        }

        drawOverlays(g);

        drawInactiveScreens(g);
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
    public void drawEquipped(Graphics g)
    {
        if (level.player.timeSinceFired < 17*7) {
            g.drawImage(level.player.getEquipped().getTexture(), 555, 350, 100, 150,null);
        }
        else {
            g.drawImage(level.player.getEquipped().getTexture(), 555, 390, 100, 150,null);
        }
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
    }
    public void draw3D(Graphics g) {
        List<Triangle> view = level.camera.view();
        for (Triangle t : view) {
            if (t.texture != null) {
                List<Color> overlays = new ArrayList<>();

                overlays.add(t.c);
                if (t.attributes[1].contains("SHOT")) {
                    overlays.add(Color.RED);
                }
                Rasterizer.drawTexTriangle(g, t, WIDTH, HEIGHT, overlays);
            }
            else {
                g.setColor(t.c);
                if (t.attributes[1].contains("SHOT") || t.attributes[0].equals("WIN")) {
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
    private void drawInactiveScreens(Graphics g) {
        if (active) return;

        g.setColor(Color.WHITE);
        Font stringFont = new Font( "OCR A Extended", Font.BOLD, 100 );
        g.setFont(stringFont);

        FontMetrics metrics = g.getFontMetrics(stringFont);

        String mainStr = ""; String optStr = "";

        if (level.getGameState() == 1) {
            mainStr = "you win i guess";
            if (stage == 2) optStr = "press enter to go to next level";
        }
        if (level.getGameState() == -1) {
            mainStr = "YOU LOSE";
            optStr = "press enter to restart";
        }
        if (level.getGameState() == 0 && stage == 2) {
            mainStr = "game paused";
            optStr = "press enter to restart";
        }
        g.drawString(mainStr, (WIDTH-metrics.stringWidth(mainStr))/2, (HEIGHT-metrics.getHeight())/2+metrics.getAscent());

        g.setFont(new Font( "OCR A Extended", Font.BOLD, 25 ));
        metrics = g.getFontMetrics(g.getFont());
        g.drawString(optStr, (WIDTH-metrics.stringWidth(optStr))/2, (HEIGHT-metrics.getHeight())/2+metrics.getAscent()+75);
    }
    private void drawStartScreen(Graphics g) {
        if (stage == 0) {
            try {
                g.drawImage(ImageIO.read(new File("./assets/img/title-screen.png")), 0, 0,
                        WIDTH, HEIGHT, null);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            String optStr = "Press enter";
//            g.setColor(Color.WHITE);
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
            g.fillRect(WIDTH/2 - 260, HEIGHT/2 - 220,520, 440);

            g.setColor(Color.BLACK);
            Font stringFont = new Font( "OCR A Extended", Font.BOLD, 15);
            FontMetrics metrics = g.getFontMetrics(stringFont);
            g.setFont(stringFont);
            int i = 1;
            try {
                BufferedReader br = new BufferedReader(new FileReader("./assets/txt/exposition.txt"));
                String s;
                while ((s = br.readLine()) != null) {
                    g.drawString(s, (WIDTH-metrics.stringWidth(s))/2, HEIGHT/2-220+15+15*i);
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
            if (sound) {
                bkgMusic.stop();
                bkgMusic.flush();
            }
            playSound("./assets/sound/win.wav");
            //win
        }
        else if (level.getGameState() == -1) {
            active = false;
            if (sound) {
                bkgMusic.stop();
                bkgMusic.flush();
            }
            playSound("./assets/sound/lose.wav");
            //lose
        }
        else {
            level.update();
        }
    }

    public void playSound(String filePath) {
        if (!sound) return;
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setBkgMusic(String filePath) {
        if (!sound) return;
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File(filePath));
            bkgMusic = AudioSystem.getClip();
            bkgMusic.open(audioInput);
            bkgMusic.start();
            bkgMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
        catch (Exception err){
            err.printStackTrace();
        }
    }

    public class GKeyAdapter extends KeyAdapter {
        final double movement = 25;
        final double rotation = 0.1;
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_R -> {
                    mouseX = 0;
                    firstMove = true;

                    active = false;
                    stage = 0; //0 is very start, 1 is options, 2 is playing

                    level = new DoomLevel("./assets/txt/DoomBasic.txt", WIDTH, HEIGHT, 1.00, 100);

                    if (sound) {
                        bkgMusic.stop();
                        bkgMusic.flush();
                    }
                }
            }
            if (stage == 0) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> {
                        stage++;
                        playSound("./assets/sound/dssgcock.wav");
                    }
                }
            }
            else if (stage == 1) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> {
                        stage++;
                        playSound("./assets/sound/dssgcock.wav");
                        setBkgMusic("./assets/sound/d_e1m1.wav");
                    }
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
                    case KeyEvent.VK_SPACE -> {
                        if (level.shoot()) playSound("./assets/sound/dspunch.wav");
                    }
                    //case KeyEvent.VK_Z -> level.pickUp();
                    case KeyEvent.VK_1 -> {
                        if (level.player.equipt(1)) playSound("./assets/sound/dswpnup.wav");
                    }
                    case KeyEvent.VK_2 -> {
                        if (level.player.equipt(2)) playSound("./assets/sound/dswpnup.wav");
                    }
                    case KeyEvent.VK_3 -> {
                        if (level.player.equipt(3)) playSound("./assets/sound/dswpnup.wav");
                    }
                    case KeyEvent.VK_4 -> {
                        if (level.player.equipt(4)) playSound("./assets/sound/dswpnup.wav");
                    }
                    //case KeyEvent.VK_UP -> level.camera.turnUpDown(rotation);
                    //case KeyEvent.VK_DOWN -> level.camera.turnUpDown(-rotation);
                    case KeyEvent.VK_P -> {
                        playSound("./assets/sound/dssgcock.wav");
                        if (sound) bkgMusic.stop();
                        active = false;
                    }
                }
            }
            else if (stage >= 2) { //if there's more than two levels you can make a loop or smth
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_ENTER && level.getGameState() != 1) {
                    playSound("./assets/sound/dssgcock.wav");
                    level.restart();
                    if (sound) {
                        bkgMusic.flush();
                        bkgMusic.start();
                    }
                    active = true;
                }
                else if (stage == 2 && keyCode == KeyEvent.VK_ENTER && level.getGameState() == 1) {
                    level = new DoomLevel("./assets/txt/DoomLevel2.txt", WIDTH, HEIGHT, 1.00, 100);
                    setBkgMusic("./assets/sound/d_e1m2.wav");
                    active = true;
                    playSound("./assets/sound/dssgcock.wav");
                    stage++;
                }
                else if (keyCode == KeyEvent.VK_P && level.getGameState() == 0) {
                    playSound("./assets/sound/dssgcock.wav");
                    if (sound) bkgMusic.start();
                    active = true;
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
                    level.camera.turnRightLeft(.01*(e.getXOnScreen() - mouseX));
                }
                else {
                    firstMove = false;
                }
            }
            mouseX = e.getXOnScreen();
        }
    }
}