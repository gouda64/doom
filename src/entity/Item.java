package entity;

import graphics.Point;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Item implements Sprite {

    private BufferedImage texture;

    private Point position;

    private int type;
    private double height;
    private double widthToHeight;
    private boolean visible = true;

    public static final int HEALTH = 0;
    public static final int AMMO = 1;
    public static final int ARMOR = 2;



    public Item (int type, Point initPosition) throws IOException
    {
        position = initPosition;
        this.type = type;

        height = 0.03;
        widthToHeight = 0.5;


        //TODO: init textures and dimensions (not from texture, prob wouldn't work)
        if (type == HEALTH)
        {
          texture = ImageIO.read(new File("./assets/img/DoomHealth.png"));
        }
        else if (type == AMMO)
        {
          texture = ImageIO.read(new File("./assets/img/DoomAmmo.png"));
        }
        else
        {
           texture = ImageIO.read(new File("./assets/img/DoomArmor.png"));
        }

    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public BufferedImage getTexture() {
        return texture;
    }

    @Override
    public double getWidthPropToHeight() {
        return widthToHeight;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean v) {
        visible = v;
    }

    @Override
    public double getHeightPropToCeiling() {
        return height;
    }

    public int getType(){return type;}
}
