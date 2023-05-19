package graphics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Triangle {
    public Point[] pts;
    public Color c = new Color(0, 0, 0);
    public Point[] texPts;
    public BufferedImage texture;
    public String[] attributes = new String[2];

    public Triangle(Point p1, Point p2, Point p3) {
        pts = new Point[]{p1, p2, p3};
        Arrays.fill(attributes, "");

        texPts = new Point[]{new Point(0, 0), new Point(0, 1),
                new Point(1, 0)};
    }
    public Triangle(Point p1, Point p2, Point p3, Point[] texPts, BufferedImage texture) {
        pts = new Point[]{p1, p2, p3};
        Arrays.fill(attributes, "");

        this.texPts = texPts;
        this.texture = texture;
    }
    public Triangle(Point p1, Point p2, Point p3, Point[] texPts, String texFile) {
        pts = new Point[]{p1, p2, p3};
        Arrays.fill(attributes, "");

        this.texPts = texPts;
        try {
            this.texture = ImageIO.read(new File(texFile));
        }
        catch (IOException e) {
            System.out.println("image read failed");
            e.printStackTrace();
        }
    }

    public double avgZ() {
        double avg = 0;
        for (int i = 0; i < pts.length; i++) {
            avg += pts[i].z;
        }
        return avg / pts.length;
    }

    public String toString() {
        return "p1 - " + pts[0].toString() +
                "\n p2 - " + pts[1].toString() +
                "\n p3 - " + pts[2].toString();
    }
}
