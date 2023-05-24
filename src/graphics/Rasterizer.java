package graphics;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;
import java.util.List;

public class Rasterizer {
    public static void drawTexTriangle(Graphics g, Triangle tri, int width, int height, List<Color> overlays) {
        Graphics2D g2 = (Graphics2D) g;

        Point[] ptsClone = tri.pts.clone();
        int[] sortedIndices = IntStream.range(0, ptsClone.length)
                .boxed().sorted(Comparator.comparingDouble(i -> ptsClone[i].y))
                .mapToInt(ele -> ele).toArray();
        Arrays.sort(ptsClone, Comparator.comparingDouble((p) -> p.y));
        Point[] texPtsClone = new Point[ptsClone.length];
        for (int i = 0; i < texPtsClone.length; i++) {texPtsClone[i] = tri.texPts[sortedIndices[i]];}

        int x1 = (int)ptsClone[0].x; int x2 = (int)ptsClone[1].x; int x3 = (int)ptsClone[2].x;
        int y1 = (int)ptsClone[0].y; int y2 = (int)ptsClone[1].y; int y3 = (int)ptsClone[2].y;
        int u1 = (int)texPtsClone[0].x; int u2 = (int)texPtsClone[1].x; int u3 = (int)texPtsClone[2].x;
        int v1 = (int)texPtsClone[0].y; int v2 = (int)texPtsClone[1].y; int v3 = (int)texPtsClone[2].y;

        int dx1 = x2 - x1; int dy1 = y2 - y1;
        double du1 = u2 - u1; double dv1 = v2 - v1;

        int dx2 = x3 - x1; int dy2 = y3 - y1;
        double du2 = u3 - u1; double dv2 = v3 - v1;

        double dStartXStep = 0; double dEndXStep = 0;
        double dU1Step = 0; double dV1Step = 0; double dU2Step = 0; double dV2Step = 0;
        if (dy1 != 0) {
            dStartXStep = (double)dx1/Math.abs(dy1);
            dU1Step = du1/Math.abs(dy1);
            dV1Step = dv1/Math.abs(dy1);
        }
        if (dy2 != 0) {
            dEndXStep = (double)dx2/Math.abs(dy2);
            dU2Step = du2 /Math.abs(dy2);
            dV2Step = dv2 /Math.abs(dy2);
        }

        for (int z = 0; z < 2; z++) {
            if (dy1 != 0) {
                for (int i = (z==0 ? y1 : y2); i <= (z==0 ? y2: y3); i++) {

                    int startX = z==0 ? (x1 + (int) ((i - y1) * dStartXStep)) :
                            (x2 + (int) ((i - y2) * dStartXStep));
                    int endX = x1 + (int) ((i - y1) * dEndXStep);

                    double startU = z==0 ? (u1 + ((double)(i-y1)*dU1Step)) :
                            (u2 + ((double)(i - y2) * dU1Step));
                    double startV = z==0 ? (v1 + ((double)(i-y1)*dV1Step)) :
                            (v2 + ((double)(i - y2) * dV1Step));
                    double endU = u1 + (double)(i - y1) * dU2Step;
                    double endV = v1 + (double)(i - y1) * dV2Step;

                    //need to sort along x
                    if (startX > endX) {
                        int temp1 = startX;
                        startX = endX;
                        endX = temp1;
                        double temp2 = startU;
                        startU = endU;
                        endU = temp2;
                        temp2 = startV;
                        startV = endV;
                        endV = temp2;
                    }

                    double u, v;
                    double tStep = 1.0 / (endX - startX);
                    double t = 0;

                    for (int j = startX; j < endX; j++) {
                        u = (1 - t) * startU + t * endU;
                        v = (1 - t) * startV + t * endV;

                        int finU = Math.max(Math.min((int) (u * tri.texture.getWidth()), tri.texture.getWidth() - 1), 0);
                        int finV = Math.max(Math.min((int) (v * tri.texture.getHeight()), tri.texture.getHeight() - 1), 0);
                        int color = tri.texture.getRGB(tri.texture.getWidth()-finU-1, tri.texture.getHeight()-finV-1);

                        t += tStep;

                        if (((color & 0xff000000) >>> 24) <= 0) {
                            continue;
                        }

                        //bitwise rgb color value
                        Color c = new Color((color & 0xff0000) >> 16, (color & 0xff00) >> 8, color & 0xff);

                        g.setColor(c);
                        g.fillRect(width-j, height-i, 1, 1);

                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.25f));
                        for (Color over : overlays) {
                            g2.setColor(over);
                            g2.fillRect(width-j, height-i, 1, 1);
                        }
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1f));
                    }

                }
            }

            dx1 = x3 - x2; dy1 = y3 - y2;
            du1 = u3 - u2; dv1 = v3 - v2;

            dU1Step = 0; dV1Step = 0;

            if (dy1 != 0) {
                dStartXStep = (double)dx1/Math.abs(dy1);
                dU1Step = du1/Math.abs(dy1);
                dV1Step = dv1/Math.abs(dy1);
            }
            if (dy2 != 0) {
                dEndXStep = (double)dx2/Math.abs(dy2);
            }
        }

    }
}
