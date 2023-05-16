package entity;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Item implements Sprite {



    private BufferedImage img;

    private int type;
    private int height;
    private int width;

    private static final int HEALTH = 0;
    private static final int AMMO = 1;
    private static final int ARMOR = 2;

    public Item (int type) throws IOException
    {
        this.type = type;

        if (type == HEALTH)
        {
            img = ImageIO.read(new File("DoomHealthBonus.jpeg"));
            width = img.getWidth();
            height = img.getHeight();
        }
        else if (type == AMMO)
        {
            img = ImageIO.read(new File("DoomHealthBonus.jpeg"));
            width = img.getWidth();
            height = img.getHeight();
        }
        else
        {
            img = ImageIO.read(new File("DoomHealthBonus.jpeg"));
            width = img.getWidth();
            height = img.getHeight();
        }




    }
}
