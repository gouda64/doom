package graphics;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Camera {
    private final double renderDist;
    private final int width;
    private final int height;
    private final double fov;
    private final Mesh mesh;
    private final double[][] projectionMatrix;
    private Point pos;
    private Point lookDir;
    private double yaw;

    public Camera(int width, int height, String mapFile, Point startPos, Point startLook, int renderDist) {
        this.width = width;
        this.height = height;

        this.renderDist = renderDist;

        fov = 90;
        projectionMatrix = matProjection(fov,(double) this.height/this.width, 0.5, 1000);

        pos = startPos;
        lookDir = startLook;
        yaw = 0;

//        List<Triangle> testTri = new ArrayList<>();
//        testTri.add(new Triangle(new Point(10, 10, 10), new Point(20, 20, 10), new Point(30, 10, 10)));
//        mesh = new Mesh(testTri);
        // for debugging

        mesh = new Mesh(new ArrayList<>());
        mesh.readObj(mapFile);
    }
    public Camera(int width, int height, Point startPos, Point startLook, double renderDist) {
        this.width = width;
        this.height = height;

        this.renderDist = renderDist;

        fov = 90;
        projectionMatrix = matProjection(fov,(double) this.height/this.width, 0.5, 1000);

        pos = startPos;
        lookDir = startLook;
        yaw = 0;

        mesh = new Mesh(new ArrayList<>());
    }

    public Mesh getMesh() {
        return mesh;
    }
    public Point getPos() {return pos;}
    public Point getLookDir() {
        return lookDir;
    }

    public List<Triangle> view() {
        //10??
        double[][] worldMat = Matrix.translation(0, 0, 10);

        lookDir = Matrix.multiplyVecMat(new Point(0, 0, 1), Matrix.rotY(yaw));

        double[][] camMat = pointAt(pos, pos.add(lookDir), new Point(0, 1, 0))[1];
        List<Triangle> trisToDraw = cullAndProject(worldMat, camMat);
        sortByZ(trisToDraw);

        List<Triangle> view = new ArrayList<>();
        for (Triangle t : trisToDraw) {
            view.addAll(getClippedTris(t));
        }

        return view;
    }

    private List<Triangle> cullAndProject(double[][] worldMat, double[][] camMat) {
        List<Triangle> trisToDraw = new ArrayList<>();

        for (Triangle t : mesh.getAllTris()) {
            if (renderDist != -1) {
                boolean canBreak = true;
                for (Point p : t.pts) {
                    if (pos.sub(p).length() < renderDist) canBreak = false;
                }
                if (canBreak) continue;
            }

            Triangle tTransformed = transformTriByMat(t, worldMat);

            Point normal, line1, line2;
            line1 = tTransformed.pts[1].sub(tTransformed.pts[0]);
            line2 = tTransformed.pts[2].sub(tTransformed.pts[0]);
            normal = line1.crossProduct(line2).normalize();

            if (normal.dotProduct(tTransformed.pts[0].sub(pos)) < 0) {
                //Point lightDirection = new Point(0, 0, -
                double dp = getLighting(tTransformed, normal);
                Color c = new Color((float) dp, (float) dp, (float) dp);

                Triangle tView = transformTriByMat(tTransformed, camMat);

                Triangle[] clippedTris = triClipToPlane(new Point(0, 0, 0.1), new Point(0, 0, 1), tView);
                for (Triangle tri : clippedTris) {

                    Triangle tProjected = transformTriByMat(tri, projectionMatrix);

                    tProjected.c = c;

                    Point addP = new Point(1, 1, 0);
                    for (int i = 0; i < tProjected.pts.length; i++) {
                        tProjected.pts[i] = tProjected.pts[i].add(addP);
                    }
                    for (int i = 0; i < tProjected.pts.length; i++) {
                        tProjected.pts[i].x *= 0.5 * width;
                        tProjected.pts[i].y *= 0.5 * height;
                    }

                    trisToDraw.add(tProjected);
                }
            }
        }
        return trisToDraw;
    }

    private List<Triangle> getClippedTris(Triangle t) {
        List<Triangle> clippedTris = new ArrayList<>();
        clippedTris.add(t);

        int newTris = 1;
        for (int i = 0; i < 4; i++) {

            while (newTris > 0) {
                Triangle test = clippedTris.remove(0);
                newTris--;
                Triangle[] clippedTemp = switch (i) {
                    case 0 -> //top
                            triClipToPlane(new Point(0, 0, 0), new Point(0, 1, 0), test);
                    case 1 -> //bottom
                            triClipToPlane(new Point(0, height - 1, 0), new Point(0, -1, 0), test);
                    case 2 -> //left
                            triClipToPlane(new Point(0, 0, 0), new Point(1, 0, 0), test);
                    case 3 -> //right
                            triClipToPlane(new Point(width - 1, 0, 0), new Point(-1, 0, 0), test);
                    default -> new Triangle[0];
                };

                Collections.addAll(clippedTris, clippedTemp);
            }
            newTris = clippedTris.size();
        }

        return clippedTris;
    }

    private double getLighting(Triangle t, Point normal) {
        double dp = 0;
        double dist = Math.abs(pos.sub(t.pts[0]).dotProduct(normal));
        if (renderDist != -1) {
<<<<<<< HEAD
            dp = Math.max(0, 1 - Math.min((pos.sub(t.pts[0]
                            .add(t.pts[1]).add(t.pts[2]).mult(1.0/3))
                    .length())/(3.0/2*renderDist), 0.95));
=======
//            dp = Math.max(0, 1 - Math.min((pos.sub(t.pts[0]
//                            .add(t.pts[1]).add(t.pts[2]).mult(1.0/3))
//                    .length())/(3.0/2*renderDist), 0.95));
>>>>>>> ecbfc8424b00e804bc04441446b4266868dae7a6
            dp = Math.max(0, 1 - Math.min(dist/(5.0/4*renderDist), 0.95));
        }
        return dp;
    }
    private void sortByZ(List<Triangle> tris) {
        Comparator<Triangle> compareByZ = (t1, t2) -> {
            double z1 = Math.max(t1.pts[0].z, Math.max(t1.pts[1].z, t1.pts[2].z));//t1.avgZ();
            double z2 = Math.max(t2.pts[0].z, Math.max(t2.pts[1].z, t2.pts[2].z));;//t2.avgZ();
            if (z1-z2 < 0) {
                return 1;
            }
            if (z1-z2 > 0){
                return -1;
            }
            return 0;
        };
        tris.sort(compareByZ);
    }
    private Triangle transformTriByMat(Triangle t, double[][] mat) {
        Triangle newT = new Triangle(Matrix.multiplyVecMat(t.pts[0], mat),
                Matrix.multiplyVecMat(t.pts[1], mat), Matrix.multiplyVecMat(t.pts[2], mat), t.texPts, t.texture);
        newT.c  = t.c; newT.attributes = t.attributes;
        return newT;
    }

    private static double[][] matProjection(double fovDeg, double aspectRatio, double zNear, double zFar) {
        double scaleFactor = 1/Math.tan(Math.toRadians(fovDeg)/2);
        double[][] projMat = new double[4][4];
        projMat[0][0] = aspectRatio*scaleFactor;
        projMat[1][1] = scaleFactor;
        projMat[2][2] = zFar/(zFar-zNear);
        projMat[3][2] = -1*(zFar*zNear)/(zFar-zNear);
        projMat[2][3] = 1;
        return projMat;
    }

    private static Point vectorIntersectPlane(Point pPoint, Point pNormal, Point lStart, Point lEnd) {
        pNormal = pNormal.normalize();
        double t = (pNormal.dotProduct(pPoint) - lStart.dotProduct(pNormal)) /
                (lEnd.dotProduct(pNormal) - lStart.dotProduct(pNormal));
        return lStart.add(lEnd.sub(lStart).mult(t));
    }

    private static Triangle[] triClipToPlane(Point pPoint, Point pNormal, Triangle t) {
        pNormal = pNormal.normalize();
        double[] dists = new double[3];
        for (int i = 0; i < 3; i++) {
            Point temp = t.pts[i];
            dists[i] = pNormal.dotProduct(temp) - pNormal.dotProduct(pPoint);
        }

        Point[] inside = new Point[3]; int inNum = 0;
        Point[] outside = new Point[3]; int outNum = 0;
        Point[] insideTex = new Point[3];
        Point[] outsideTex = new Point[3];

        for (int i = 0; i < dists.length; i++) {
            if (dists[i] >= 0) {
                inside[inNum] = t.pts[i];
                insideTex[inNum] = t.texPts[i];
                inNum++;
            }
            else {
                outside[outNum] = t.pts[i];
                outsideTex[outNum] = t.texPts[i];
                outNum++;
            }
        }

        switch (inNum) {
            case 0:
                return new Triangle[]{};
            case 3:
                return new Triangle[]{t};
            case 1:
                Triangle newT = new Triangle(null, null, null);
                newT.texture = t.texture;

                newT.pts[0] = inside[0];
                newT.pts[1] = vectorIntersectPlane(pPoint, pNormal, inside[0], outside[0]);
                newT.pts[2] = vectorIntersectPlane(pPoint, pNormal, inside[0], outside[1]);

                newT.texPts[0] = insideTex[0];
                double t1 = (pNormal.dotProduct(pPoint) - inside[0].dotProduct(pNormal)) /
                        (outside[0].dotProduct(pNormal) - inside[0].dotProduct(pNormal));

                newT.texPts[1] = new Point(t1*(outsideTex[0].x - insideTex[0].x) + insideTex[0].x,
                        t1*(outsideTex[0].y - insideTex[0].y) + insideTex[0].y);

                t1 = (pNormal.dotProduct(pPoint) - inside[0].dotProduct(pNormal)) /
                        (outside[1].dotProduct(pNormal) - inside[0].dotProduct(pNormal));
                newT.texPts[2]= new Point(t1*(outsideTex[1].x - insideTex[0].x) + insideTex[0].x,
                        t1*(outsideTex[1].y - insideTex[0].y) + insideTex[0].y);

                newT.c = t.c;
                newT.attributes = t.attributes;

                return new Triangle[]{newT};
            default: //quad case
                Triangle newT1 = new Triangle(null, null, null);
                Triangle newT2 = new Triangle(null, null, null);

                newT1.texture = t.texture;
                newT2.texture = t.texture;

                newT1.pts[0] = inside[0];
                newT1.pts[1] = inside[1];
                newT1.pts[2] = vectorIntersectPlane(pPoint, pNormal, inside[0], outside[0]);

                newT1.texPts[0] = insideTex[0];
                newT1.texPts[1] = insideTex[1];
                t1 = (pNormal.dotProduct(pPoint) - inside[0].dotProduct(pNormal)) /
                        (outside[0].dotProduct(pNormal) - inside[0].dotProduct(pNormal));
                newT1.texPts[2] = new Point(t1*(outsideTex[0].x - insideTex[0].x) + insideTex[0].x,
                        t1*(outsideTex[0].y - insideTex[0].y) + insideTex[0].y);

                newT2.pts[0] = inside[1];
                newT2.pts[1] = newT1.pts[2];
                newT2.pts[2] = vectorIntersectPlane(pPoint, pNormal, inside[1], outside[0]);

                newT2.texPts[0] = insideTex[1];
                newT2.texPts[1] = newT1.texPts[2];
                t1 = (pNormal.dotProduct(pPoint) - inside[1].dotProduct(pNormal)) /
                        (outside[0].dotProduct(pNormal) - inside[1].dotProduct(pNormal));
                newT2.texPts[2] = new Point(t1*(outsideTex[0].x - insideTex[1].x) + insideTex[1].x,
                        t1*(outsideTex[0].y - insideTex[1].y) + insideTex[1].y);

                newT1.c = t.c; newT2.c = t.c;
                newT1.attributes = t.attributes; newT2.attributes = t.attributes;

                return new Triangle[]{newT1, newT2};
        }
    }

    private static double[][][] pointAt(Point pos, Point target, Point up) {
        Point newForward = target.sub(pos).normalize();
        Point newUp = up.sub(newForward.mult(up.dotProduct(newForward))).normalize();
        Point newRight = newUp.crossProduct(newForward);

        double[][] mat = new double[4][4];
        mat[0][0] = newRight.x; mat[0][1] = newRight.y; mat[0][2] = newRight.z;
        mat[1][0] = newUp.x; mat[1][1] = newUp.y; mat[1][2] = newUp.z;
        mat[2][0] = newForward.x; mat[2][1] = newForward.y; mat[2][2] = newForward.z;
        mat[3][0] = pos.x; mat[3][1] = pos.y; mat[3][2] = pos.z; mat[3][3] = 1;

        double[][] inverseMat = new double[4][4];
        inverseMat[0][0] = newRight.x; inverseMat[1][0] = newRight.y; inverseMat[2][0] = newRight.z;
        inverseMat[0][1] = newUp.x; inverseMat[1][1] = newUp.y; inverseMat[2][1] = newUp.z;
        inverseMat[0][2] = newForward.x; inverseMat[1][2] = newForward.y; inverseMat[2][2] = newForward.z;
        inverseMat[3][0] = -pos.dotProduct(newRight); inverseMat[3][1] = -pos.dotProduct(newUp);
        inverseMat[3][2] = -pos.dotProduct(newForward); inverseMat[3][3] = 1;

        return new double[][][] {mat, inverseMat};
    }

    public int lookingAt() {
        double epsilon = 0.00001;
        int looking = 0;
        double minT = Double.MAX_VALUE;
        List<Triangle> allTris = mesh.getAllTris();
        for (int i = 0; i < allTris.size(); i++) {
            Triangle tri = allTris.get(i);
            Point h = lookDir.crossProduct(tri.pts[2].sub(tri.pts[0]));
            Point q = pos.sub(tri.pts[0]).crossProduct(tri.pts[1].sub(tri.pts[0]));

            double a = tri.pts[1].sub(tri.pts[0]).dotProduct(h);
            if (a > -epsilon && a < epsilon) continue;
            double u = 1 / a * pos.sub(tri.pts[0]).dotProduct(h);
            if (u < 0 || u > 1) continue;
            double v = 1 / a * lookDir.dotProduct(q);
            if (v < 0 || u + v > 1) continue;

            double t = 1 / a * tri.pts[2].sub(tri.pts[0]).dotProduct(q);
            if (t < epsilon) continue;
            if (t < minT) {
                minT = t;
                looking = i;
            }
        }
        return looking;
    }
    private double lookingDist(Point rayO, Point rayDir) {
        double epsilon = 0.00001; //could make it parameter but prob unnecessary
        double minT = Double.MAX_VALUE;
        for (Triangle tri : mesh.getAllTris()) {
            Point h = rayDir.crossProduct(tri.pts[2].sub(tri.pts[0]));
            Point q = rayO.sub(tri.pts[0]).crossProduct(tri.pts[1].sub(tri.pts[0]));

            double a = tri.pts[1].sub(tri.pts[0]).dotProduct(h);
            if (a > -epsilon && a < epsilon) continue;
            double u = 1/a * rayO.sub(tri.pts[0]).dotProduct(h);
            if (u < 0 || u > 1) continue;
            double v = 1/a * rayDir.dotProduct(q);
            if (v < 0 || u + v > 1) continue;

            double t = 1/a * tri.pts[2].sub(tri.pts[0]).dotProduct(q);
            if (t < epsilon) continue;
            if (t < minT) {
                minT = t;
            }
        }
        return minT;
    }

    public void moveY(double amt) {
        pos.y += amt;
    }
    public void moveForBack(double amt) {
        pos = pos.add(new Point(lookDir.x, 0, lookDir.z).mult(amt));
    }
    public void moveForBackLimited(double amt) {
        Point moveVec = new Point(lookDir.x, 0, lookDir.z).mult(amt);
        double lookingDist = lookingDist(pos, moveVec);
        if (lookingDist < 1) return;
        pos = pos.add(moveVec);
    }
    public void moveRightLeft(double amt) {
        pos = pos.add(lookDir.crossProduct(new Point(0, 1, 0)).mult(amt));
    }
    public void moveRightLeftLimited(double amt) {
        Point moveVec = lookDir.crossProduct(new Point(0, 1, 0)).mult(amt);
        double lookingDist = lookingDist(pos, moveVec);
        if (lookingDist < 1) return;
        pos = pos.add(moveVec);
    }
    public void turnRightLeft(double amt) {
        yaw += amt;
        yaw %= 2*Math.PI;
    }
//    public void turnUpDown(double amt) {
//        pitch += amt;
//        if (pitch >= Math.PI/2) {
//            pitch = Math.PI/2 - 0.01;
//        }
//        else if (pitch <= -Math.PI/2) {
//            pitch = -Math.PI/2 + 0.01;
//        }
//    }
}