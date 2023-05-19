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
    private int gameState = 0;

    private double mapHeight = 0;
    private double scale;
    private final List<Point> vertices = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private Point playerStart;
    private Point playerLook = new Point(0, 0, 1);
    private Edge winEdge;

    private final List<Sprite> sprites = new ArrayList<>(); //the non-moving ones
    private final List<Monster> monsters = new ArrayList<>();
    public Player player = new Player();


    public DoomLevel(String mapFile, int width, int height, double renderDistToHeight, double scale) {//double xScale, double zScale) {
        this.scale = scale;

        readMap(mapFile);
        generateBackground();

        camera = new Camera(width, height, new Point(playerStart.x*scale, playerStart.y, playerStart.z*scale),
                playerLook, scale*mapHeight*renderDistToHeight);
        camera.getMesh().setTris(background);
    }

    public void step() {
        camera.getMesh().tempTris = generateSprites();
        //update timed graphics like shoot
        //move monsters
        //have monsters shoot in intervals
        if (player.getHealth() <= 0) gameState = -1;
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

    public int getGameState() {
        return gameState;
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

    public void walk(double amt) {
        Point pos = camera.getPos().div(scale); pos.y = 0;
        Point lookDir = camera.getLookDir().normalize();
        Point headedTo = pos.add(new Point(lookDir.x, 0, lookDir.z).mult(amt/scale));
        System.out.println(pos + " " + headedTo);
        System.out.println(winEdge.v1 + " " + winEdge.v2);
        System.out.println();
        if (vecCross2D(pos, headedTo, winEdge.v1, winEdge.v2)) {
            gameState = 1;
        }

        camera.moveForBackLimited(amt);
    }
    private boolean vecCross2D(Point l1, Point l2, Point s1, Point s2) {
        double epsilon = 0.00001;

        double a1 = l2.z - l1.z; double b1 = l1.x - l2.x;
        double c1 = a1*l1.x + b1*l1.z;
        double a2 = s2.z - s1.z; double b2 = s1.x - s2.x;
        double c2 = a2*s1.x + b2*s1.z;
        double determinant = a1*b2 - a2*b1;
        if (determinant > epsilon) {
            return false;
        }

        Point intersect = new Point((b2*c1 - b1*c2)/determinant, 0, (a1*c2 - a2*c1)/determinant);
        if (Math.abs((intersect.z-l1.z)*(l2.x-l1.x) - (intersect.x-l1.x)*(l2.z-l1.z)) > epsilon
            || Math.abs((intersect.z-s1.z)*(s2.x-s1.x) - (intersect.x-s1.x)*(s2.z-s1.z)) > epsilon) {
            return false;
        }
        if (l2.sub(l1).dotProduct(intersect.sub(l1)) < 0 ||
                l2.sub(l1).dotProduct(intersect.sub(l1)) > Math.pow(l2.sub(l1).length(),2) ||
                s2.sub(s1).dotProduct(intersect.sub(s1)) < 0 ||
                s2.sub(s1).dotProduct(intersect.sub(s1)) > Math.pow(s2.sub(s1).length(),2)) {
            return false;
        }
        return true;
    }
    public void strafe(double amt) {
        Point pos = camera.getPos().div(scale); pos.y = 0;
        Point lookDir = camera.getLookDir().normalize();
        Point headedTo = pos.add(lookDir.crossProduct(new Point(0, 1, 0)).mult(amt/scale));
        if (vecCross2D(pos, headedTo, winEdge.v1, winEdge.v2)) {
            gameState = 1;
        }

        camera.moveRightLeftLimited(amt);
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
                        Point pos = new Point(Double.parseDouble(split[2]), 0, Double.parseDouble(split[3]));
                        monsters.add(new Monster(Integer.parseInt(split[1]),
                                pos, playerStart.sub(pos)));
                    }
                    case "i" -> {
                        Point pos = new Point(Double.parseDouble(split[2]), 0, Double.parseDouble(split[3]));
                        sprites.add(new Item(Integer.parseInt(split[1]),
                                pos, playerStart.sub(pos)));
                    }
                    case "w" -> {
                        Point pos = new Point(Double.parseDouble(split[2]), 0, Double.parseDouble(split[3]));
                        sprites.add(new Weapon(Integer.parseInt(split[1]),
                                pos, playerStart.sub(pos)));
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
