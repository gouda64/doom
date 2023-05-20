package entity;


import graphics.Point;

import java.awt.image.BufferedImage;

public interface Sprite {
    public Point getPosition();
    public BufferedImage getTexture();
    public double getHeightPropToCeiling();
    public double getWidthPropToHeight();
    public boolean isVisible();
    public void setVisible(boolean v);
}
