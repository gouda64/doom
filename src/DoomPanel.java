import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DoomPanel extends JPanel {
    static final int WIDTH = 640;
    static final int HEIGHT = 5*WIDTH/8;
    //a little messed up bc og doom has rectangle pixels

    public DoomPanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setLayout(null);
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new DoomKeyAdapter());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        draw(g);
    }
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString("DOOM", WIDTH/2, HEIGHT/2);
    }

    public static class DoomKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
//            switch (e.getKeyCode()) {
//                case KeyEvent.VK_SPACE -> ;
//                case KeyEvent.VK_SHIFT -> ;
//                case KeyEvent.VK_D -> ;
//                case KeyEvent.VK_A -> ;
//                case KeyEvent.VK_W -> ;
//                case KeyEvent.VK_S -> ;
//                case KeyEvent.VK_LEFT -> ;
//                case KeyEvent.VK_RIGHT -> ;
//                case KeyEvent.VK_UP -> ;
//                case KeyEvent.VK_DOWN -> ;
//            }
        }
        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}
