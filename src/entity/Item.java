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

    private static final int HEALTH = 0;
    private static final int AMMO = 1;
    private static final int ARMOR = 2;



    public Item (int type, Point initPosition) throws IOException
    {
        position = initPosition;
        this.type = type;

        height = 0.1;
        widthToHeight = 0.5;

        //TODO: init textures and dimensions (not from texture, prob wouldn't work)
        if (type == HEALTH)
        {
//            texture = ImageIO.read(new File("DoomHealthBonus.jpeg"));
//            width = texture.getWidth();
//            height = texture.getHeight();
        }
        else if (type == AMMO)
        {
//            texture = ImageIO.read(new File("DoomHealthBonus.jpeg"));
//            width = texture.getWidth();
//            height = texture.getHeight();
        }
        else
        {
//            texture = ImageIO.read(new File("DoomHealthBonus.jpeg"));
//            width = texture.getWidth();
//            height = texture.getHeight();
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
    public double getHeightPropToCeiling() {
        return height;
    }

    public int getType(){return type;}
}
