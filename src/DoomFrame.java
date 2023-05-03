import javax.swing.*;

public class DoomFrame extends JFrame {
    public DoomFrame() {
        this.setTitle("DOOM");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        JPanel p = new DoomPanel();
        this.add(p);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
