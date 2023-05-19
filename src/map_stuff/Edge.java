package map_stuff;

import graphics.Point;

public class Edge {
    Point v1;
    Point v2;
    String texFile = "";

    public Edge(Point v1, Point v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public boolean intersects(Edge other) {
        double epsilon = 0.00001;

        double a1 = v2.z - v1.z; double b1 = v1.x - v2.x;
        double c1 = a1*v1.x + b1*v1.z;
        double a2 = other.v2.z - other.v1.z; double b2 = other.v1.x - other.v2.x;
        double c2 = a2*other.v1.x + b2*other.v1.z;
        double determinant = a1*b2 - a2*b1;
        if (Math.abs(determinant) < epsilon) {
            return false;
        }

        Point intersect = new Point((b2*c1 - b1*c2)/determinant, 0, (a1*c2 - a2*c1)/determinant);
        if (Math.abs((intersect.z-v1.z)*(v2.x-v1.x) - (intersect.x-v1.x)*(v2.z-v1.z)) > epsilon
                || Math.abs((intersect.z-other.v1.z)*(other.v2.x-other.v1.x) -
                (intersect.x-other.v1.x)*(other.v2.z-other.v1.z)) > epsilon) {
            return false;
        }
        if (v2.sub(v1).dotProduct(intersect.sub(v1)) < 0 ||
                v2.sub(v1).dotProduct(intersect.sub(v1)) > Math.pow(v2.sub(v1).length(),2) ||
                other.v2.sub(other.v1).dotProduct(intersect.sub(other.v1)) < 0 ||
                other.v2.sub(other.v1).dotProduct(intersect.sub(other.v1)) > Math.pow(other.v2.sub(other.v1).length(),2)) {
            return false;
        }
        return true;
    }
}
