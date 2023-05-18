package map_stuff;

import entity.*;
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
    public final Camera camera;

    private double mapHeight = 0;
    private double scale;
    private final List<Point> vertices = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private Point playerStart;
    private Point playerLook = new Point(0, 0, 1);
    private Edge winEdge;

    private final List<Sprite> sprites = new ArrayList<>(); //the non-moving ones
    private final List<Monster> monsters = new ArrayList<>();
    public Player player;


    public DoomLevel(String mapFile, int width, int height, double renderDistToHeight, double scale) {//double xScale, double zScale) {
        this.scale = scale;

        readMap(mapFile);
        generateBackground();
        player = new Player();
        playerStart = new Point(playerStart.x*scale, playerStart.y, playerStart.z*scale);

        camera = new Camera(width, height, playerStart, playerLook, scale*mapHeight*renderDistToHeight);
        camera.getMesh().setTris(background);
    }

    public void step() {
        camera.getMesh().tempTris = generateSprites();
        //update timed graphics like shoot
        //move monsters
    }

    public void shoot() {
        int victim = camera.lookingAt();
        List<Triangle> tris = camera.getMesh().getAllTris();
        tris.get(victim).attributes[1] = "SHOT 0";
        if (victim%2 == 0) {
            tris.get(victim+1).attributes[1] = "SHOT 0";
        }
        else {
            tris.get(victim-1).attributes[1] = "SHOT 0";
        }
        if (tris.get(victim).attributes[0].contains("MONSTER")) {
            monsters.get(Integer.parseInt(tris.get(victim).attributes[0].substring(8)))
                    .takeDamage(player.getEquipped().shoot());
        }
    }

    public int gameState() {
        if (player.getHealth() <= 0) return -1;
        return 0;
    }

    public List<Triangle> generateSprites() {
        //make sure to add attributes

        for (Sprite s : sprites) {
            double height = mapHeight*s.getHeightPropToCeiling();
            double width = height*s.getWidthPropToHeight();
        }
        for (Monster m : monsters) {

        }
        return new ArrayList<>();
    }

    public Point getPlayerStart() {
        return playerStart;
    }
    public Edge getWinEdge() {
        return winEdge;
    }
    public List<Sprite> getSprites() {
        return sprites;
    }
    public List<Monster> getMonsters() {
        return monsters;
    }

    private void generateBackground() {
        background = new ArrayList<>();
        for (Edge e : edges) {
            Triangle t1 = new Triangle(e.v1.mult(scale),
                    e.v1.add(new Point(0, mapHeight, 0)).mult(scale), e.v2.mult(scale));
            Triangle t2 = new Triangle(e.v1.add(new Point(0, mapHeight, 0)).mult(scale),
                    e.v2.add(new Point(0, mapHeight, 0)).mult(scale), e.v2.mult(scale));
            if (!e.texFile.equals("")) {
                t1 = new Triangle(t1.pts[0], t1.pts[1], t1.pts[2], new Point[]{new Point(1, 0),
                        new Point(0, 0), new Point(1, 1)}, e.texFile);
                new Triangle(t2.pts[0], t2.pts[1], t2.pts[2], new Point[]{new Point(0, 0),
                        new Point(0, 1), new Point(1, 1)}, e.texFile);
            }

            t1.attributes[0] = "WALL";
            t2.attributes[0] = "WALL";

            background.add(t1);
            background.add(t2);
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
                        playerStart = new Point(Double.parseDouble(split[1]), Double.parseDouble(split[3]), Double.parseDouble(split[2]));
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
