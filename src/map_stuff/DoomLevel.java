package map_stuff;

import entity.Item;
import entity.Monster;
import entity.Sprite;
import entity.Weapon;
import graphics.Camera;
import graphics.Point;
import graphics.Triangle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DoomLevel {
    private List<Triangle> background = new ArrayList<>();
    public Camera camera;

    private double mapHeight = 0;
    private final List<Point> vertices = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private final List<Sprite> sprites = new ArrayList<>(); //the non-moving ones
    private final List<Monster> monsters = new ArrayList<>();
    private Point playerStart;
    private Point playerLook = new Point(0, 0, 1);
    private Edge winEdge;


    public DoomLevel(String mapFile, int width, int height, double renderDist) {
        readMap(mapFile);
        generateBackground();

        camera = new Camera(width, height, playerStart, playerLook, renderDist);
        camera.getMesh().tris = background;
    }

    public List<Triangle> generateSprites() {
        for (Sprite s : sprites) {
            double height = mapHeight*s.getHeightPropToCeiling();
            double width = height*s.getWidthPropToHeight();

        }
        for (Monster m : monsters) {

        }
        return null;
    }

    public Point getPlayerStart() {
        return playerStart;
    }

    public Edge getWinEdge() {
        return winEdge;
    }

    private void generateBackground() {
        background = new ArrayList<>();
        for (Edge e : edges) {
            if (e.texFile.equals("")) {
                background.add(new Triangle(e.v1, e.v1.add(new Point(0, mapHeight, 0)), e.v2));
                background.add(new Triangle(e.v1.add(new Point(0, mapHeight, 0)),
                        e.v2.add(new Point(0, mapHeight, 0)), e.v2));
                continue;
            }

            background.add(new Triangle(e.v1, e.v1.add(new Point(0, mapHeight, 0)), e.v2,
                    new Point[]{new Point(1, 0), new Point(0, 0), new Point(1, 1)}, e.texFile));
            background.add(new Triangle(e.v1.add(new Point(0, mapHeight, 0)),
                    e.v2.add(new Point(0, mapHeight, 0)), e.v2,
                    new Point[]{new Point(0, 0), new Point(0, 1), new Point(1, 1)}, e.texFile));
        }
    }
    public void readMap(String fileName) {
        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            String s;
            while ((s = in.readLine()) != null) {
                String[] split = s.split(" ");
                switch (split[0]) {
                    case "v" -> {
                        vertices.add(new Point(Double.parseDouble(split[1]), 0, Double.parseDouble(split[2])));
                    }
                    case "e" -> {
                        Edge e = new Edge(vertices.get(Integer.parseInt(split[1])),
                                vertices.get(Integer.parseInt(split[2])));
                        if (split.length > 3) {
                            e.texFile = split[3];
                        }
                        edges.add(e);
                    }
                    case "h" -> {
                        mapHeight = Double.parseDouble(split[1]);
                    }
                    case "m" -> {
                        monsters.add(new Monster(Integer.parseInt(split[1]),
                                new Point(Double.parseDouble(split[2]), 0, Double.parseDouble(split[3]))));
                    }
                    case "i" -> {
                        sprites.add(new Item(Integer.parseInt(split[1]),
                                new Point(Double.parseDouble(split[2]), 0, Double.parseDouble(split[3]))));
                    }
                    case "w" -> {
                        sprites.add(new Weapon(Integer.parseInt(split[1]),
                                new Point(Double.parseDouble(split[2]), 0, Double.parseDouble(split[3]))));
                    }
                    case "we" -> {
                        winEdge = new Edge(vertices.get(Integer.parseInt(split[1])),
                                vertices.get(Integer.parseInt(split[2])));
                    }
                    case "p" -> {
                        playerStart = new Point(Double.parseDouble(split[1]), 0, Double.parseDouble(split[2]));
                    }
                    case "pv" -> {
                        playerLook = new Point(Double.parseDouble(split[1]), 0, Double.parseDouble(split[2]));
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
