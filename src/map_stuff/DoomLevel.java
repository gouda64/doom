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
import java.util.stream.Stream;

public class DoomLevel {
    private List<Triangle> background = new ArrayList<>();
    public final Camera camera;
    private int gameState = 0;

    private double mapHeight = 0;
    private double renderDist;
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
        readMap(mapFile, 1.5, 1.5); //scale vs hor/ver scale serve diff purposes!!

        renderDist = mapHeight*renderDistToHeight;

        camera = new Camera(width, height, new Point(playerStart.x*scale, playerStart.y, playerStart.z*scale),
                playerLook, scale*renderDist);

        generateBackground();
        camera.getMesh().setTris(background);
    }

    public void update(int delay) {
        camera.getMesh().tempTris = generateSprites();
        if (player.shotTime > -1) player.shotTime--;
        if (Integer.MAX_VALUE - player.timeSinceFired > delay) player.timeSinceFired += delay;

        for (Monster m : monsters) {
            if (!m.isVisible()) continue;
            if (m.shotTime > -1) m.shotTime--;
            if (pointSeePlayer(m.getPosition())) {
                Point camPos = camera.getPos().div(scale);
                camPos.y = 0;
                Point mMove = camPos.sub(m.getPosition()).normalize().mult(m.getSpeed());

                if (camPos.sub(m.getPosition()).length() > camera.getClippingDist()) {
                    double height = mapHeight*m.getHeightPropToCeiling();
                    double width = height*m.getWidthPropToHeight();

                    Point rightNormal = mMove.crossProduct(new Point(0,1,0)).normalize().mult(width/2);
                    Point leftNormal = rightNormal.mult(-1);
                    Point rightPos = rightNormal.add(m.getPosition());
                    Point leftPos = leftNormal.add(m.getPosition());

                    if (camera.lookingDist(rightPos.mult(scale), mMove.mult(scale)) > 1 &&
                            camera.lookingDist(leftPos.mult(scale), mMove.mult(scale)) > 1) {
                        m.setPosition(m.getPosition().add(mMove));
                    }
                }

                m.timeSinceFired += delay; //should be same as timer delay
                if (m.timeSinceFired >= m.getFireDelay()) {
                    player.damage(m.getDamage()-1);
                    player.shotTime += 10;
                    m.timeSinceFired = 0;
                }
            }
        }
        //move monsters
        if (player.getHealth() <= 0) gameState = -1;

    }
    private boolean pointSeePlayer(Point p) {
        Edge lookEdge = new Edge(p, camera.getPos().div(scale));
        lookEdge.v2.y = 0;
        if (lookEdge.length() > renderDist) return false;
        for (Edge e : edges) {
            if (lookEdge.intersects(e)) return false;
        }
        return true;
    }

    public boolean shoot() {
        if (player.getAmmo() > 0 && player.timeSinceFired >= player.getFireDelay()) {
            player.shot();
            int victim = camera.lookingAt();
            if (victim <= 0) return true;
            List<Triangle> tris = camera.getMesh().getAllTris();

            if (tris.get(victim).attributes[0].contains("MONSTER")) {
                tris.get(victim).attributes[1] = "SHOT";
                if (victim % 2 == 0) {
                    tris.get(victim + 1).attributes[1] = "SHOT";
                } else {
                    tris.get(victim - 1).attributes[1] = "SHOT";
                }

                Monster m = monsters.get(Integer.parseInt(tris.get(victim).attributes[0].substring(8)));
                m.shotTime = 5;
                m.takeDamage(player.getEquipped().shoot() + 5);
            }
            return true;
        }
        return false;
    }


    public void pickUp() {
        List<Triangle> tris = camera.getMesh().getAllTris();
        Triangle victim = tris.get(camera.lookingAt());
        
        Point h = camera.getLookDir().crossProduct(victim.pts[2].sub(victim.pts[0]));
        Point q = camera.getPos().sub(victim.pts[0]).crossProduct(victim.pts[1].sub(victim.pts[0]));
        double a = victim.pts[1].sub(victim.pts[0]).dotProduct(h);
        double t = 1/a * victim.pts[2].sub(victim.pts[0]).dotProduct(q);
        if (t > 150) return;

        String[] split = victim.attributes[0].split(" ");

        if (!split[0].equals("SPRITE")) return;
        if (split[1].equals("ITEM")) {
            Item it = (Item)sprites.get(Integer.parseInt(split[2]));
            player.pickUpItem(it.getType());
            it.setVisible(false);
        }
        else if (split[1].equals("WEAPON")) {
            Weapon wp = (Weapon)sprites.get(Integer.parseInt(split[2]));
            player.pickUpWeapon(wp.getType());
            wp.setVisible(false);
        }
    }

    public int getGameState() {
        return gameState;
    }

    public List<Triangle> generateSprites() {
        //make sure to add attributes
        List<Triangle> spriteList = new ArrayList<Triangle>();

        Stream.of(sprites.stream(), monsters.stream()).flatMap(v -> v).forEach(s -> {
            if (!s.isVisible()) return;
            double height = mapHeight*s.getHeightPropToCeiling();
            double width = height*s.getWidthPropToHeight();

            Point camPos = camera.getPos().div(scale);
            camPos.y = 0;
            Point look = camPos.sub(s.getPosition());
            Point rightNormal = look.crossProduct(new Point(0,1,0));
            Point leftNormal = rightNormal.mult(-1);
            rightNormal = (rightNormal.normalize()).mult(width/2);
            leftNormal = (leftNormal.normalize()).mult(width/2);
            Point rightPos = rightNormal.add(s.getPosition());
            Point leftPos = leftNormal.add(s.getPosition());

            if (!pointSeePlayer(rightPos) && !pointSeePlayer(leftPos)) {
                return;
            }

            Triangle t1 = new Triangle(leftPos.mult(scale), leftPos.add(new Point(0, height, 0)).mult(scale), rightPos.mult(scale));
            Triangle t2 = new Triangle(leftPos.add(new Point(0, height, 0)).mult(scale), rightPos.add(new Point(0, height, 0)).mult(scale), rightPos.mult(scale));

            if (s.getTexture() != null) {
                t1 = new Triangle(t1.pts[0], t1.pts[1], t1.pts[2], new Point[]{new Point (0,0),
                        new Point(0,1), new Point (1,0)}, s.getTexture());
                t2 = new Triangle(t2.pts[0], t2.pts[1], t2.pts[2], new Point[]{new Point (0,1),
                        new Point(1,1), new Point (1,0)}, s.getTexture());
            }

            String attr = "";
            if (s instanceof Monster) {
                attr = "MONSTER " + monsters.indexOf((Monster)s); //could be more efficient
                if (((Monster) s).shotTime > -1) {
                    t1.attributes[1] = "SHOT";
                    t2.attributes[1] = "SHOT";
                }
            }
            else if (s instanceof Item) {
                attr = "SPRITE ITEM " + sprites.indexOf(s);
            }
            else if (s instanceof Weapon) {
                attr = "SPRITE WEAPON " + sprites.indexOf(s);
            }
            t1.attributes[0] = attr;
            t2.attributes[0] = attr;

            spriteList.add(t1);
            spriteList.add(t2);
        });

        return spriteList;
    }

    public void walk(double amt) {
        Point pos = camera.getPos().div(scale); pos.y = 0;
        Point lookDir = camera.getLookDir().normalize();
        Point headedTo = pos.add(new Point(lookDir.x, 0, lookDir.z).mult(amt/scale));
        if (new Edge(pos, headedTo).intersects(winEdge)) {
            gameState = 1;
        }
        for (Sprite s : sprites) {
            if (!s.isVisible()) continue;
            if (s.getPosition().sub(headedTo).length() < 1) {
                if (s instanceof Item) {
                    player.pickUpItem(((Item) s).getType());
                }
                else if (s instanceof Weapon) {
                    player.pickUpWeapon(((Weapon) s).getType());
                }
                s.setVisible(false);
            }
        }
        camera.moveForBackLimited(amt);
    }

    public void strafe(double amt) {
        Point pos = camera.getPos().div(scale); pos.y = 0;
        Point lookDir = camera.getLookDir().normalize();
        Point headedTo = pos.add(lookDir.crossProduct(new Point(0, 1, 0)).mult(amt/scale));
        if (new Edge(pos, headedTo).intersects(winEdge)) {
            gameState = 1;
        }

        for (Sprite s : sprites) {
            if (!s.isVisible()) continue;
            if (s.getPosition().sub(headedTo).length() < 1) {
                if (s instanceof Item) {
                    player.pickUpItem(((Item) s).getType());
                }
                else if (s instanceof Weapon) {
                    player.pickUpWeapon(((Weapon) s).getType());
                }
                s.setVisible(false);
            }
        }

        camera.moveRightLeftLimited(amt);
    }

    public void restart() {
        player = new Player();
        camera.setPos(new Point(playerStart.x*scale, playerStart.y, playerStart.z*scale));
        camera.setLookDir(playerLook);
        gameState = 0;

        for (int i = 0; i < monsters.size(); i++) { //could be more optimized
            Monster m = monsters.get(i);
            monsters.set(i, new Monster(m.getType(), m.getStartPos()));
        }
        for (Sprite s : sprites) {
            s.setVisible(true);
        }
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
                t2 = new Triangle(t2.pts[0], t2.pts[1], t2.pts[2], new Point[]{new Point(0, 0),
                        new Point(0, 1), new Point(1, 1)}, e.texFile);
            }

            t1.attributes[0] = "WALL";
            t2.attributes[0] = "WALL";

            background.add(t1);
            background.add(t2);
        }

        Triangle t1 = new Triangle(winEdge.v1.mult(scale).add(new Point(0, 3*playerStart.y, 0)),
                winEdge.v1.add(new Point(0, mapHeight, 0)).mult(scale),
                winEdge.v2.mult(scale).add(new Point(0, 3*playerStart.y, 0)));
        Triangle t2 = new Triangle(winEdge.v1.add(new Point(0, mapHeight, 0)).mult(scale),
                winEdge.v2.add(new Point(0, mapHeight, 0)).mult(scale),
                winEdge.v2.mult(scale).add(new Point(0, 3*playerStart.y, 0)));
        t1.attributes[0] = "WIN";
        t2.attributes[0] = "WIN";
        background.add(t1);
        background.add(t2);
    }

    public void readMap(String fileName, double horScale, double vertScale) {
        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            String s;
            while ((s = in.readLine()) != null) {
                String[] split = s.split(" ");
                switch (split[0]) {
                    case "v" -> {
                        vertices.add(new Point(Double.parseDouble(split[1])*horScale, 0, Double.parseDouble(split[2])*vertScale));
                    }
                    case "e" -> {
                        Edge e = new Edge(vertices.get(Integer.parseInt(split[2])),
                                vertices.get(Integer.parseInt(split[1])));
                        if (split.length > 3) {
                            e.texFile = split[3];
                        }
                        edges.add(e);
                    }
                    case "h" -> {
                        mapHeight = Double.parseDouble(split[1]);
                    }
                    case "m" -> {
                        Point pos = new Point(Double.parseDouble(split[2])*horScale, 0, Double.parseDouble(split[3])*vertScale);
                        monsters.add(new Monster(Integer.parseInt(split[1]), pos));
                    }
                    case "i" -> {
                        Point pos = new Point(Double.parseDouble(split[2])*horScale, 0, Double.parseDouble(split[3])*vertScale);
                        sprites.add(new Item(Integer.parseInt(split[1]), pos));
                    }
                    case "w" -> {
                        Point pos = new Point(Double.parseDouble(split[2])*horScale, 0, Double.parseDouble(split[3])*vertScale);
                        sprites.add(new Weapon(Integer.parseInt(split[1]), pos));
                    }
                    case "we" -> {
                        winEdge = new Edge(vertices.get(Integer.parseInt(split[1])),
                                vertices.get(Integer.parseInt(split[2])));
                    }
                    case "p" -> {
                        playerStart = new Point(Double.parseDouble(split[1])*horScale, Double.parseDouble(split[3]), Double.parseDouble(split[2])*vertScale);
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
