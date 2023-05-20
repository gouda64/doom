package entity;

import graphics.Point;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Math;
import javax.imageio.ImageIO;

public class Weapon implements Sprite {
     static final int SLINGSHOT = 2;
     static final int PENCIL = 3;
     static final int FIREFLOWER = 4;
     static final int WAND = 5;

    private Point position;
    private BufferedImage texture;
    private double widthToHeight;
    private double height;

    private int meanDamage;
    private int deviation;
    private int fireDelay;
    private int type;
    private boolean visible = true;

    public Weapon(int weaponType)  {
        type = weaponType;
        switch (type) {
            case SLINGSHOT -> {
                meanDamage = 10;
                deviation = 5;
                fireDelay = 2500;

            }
            case PENCIL -> {
                meanDamage = 70;
                deviation = 35;
                fireDelay = 967;

            }
            case FIREFLOWER -> {
                meanDamage = 250;
                deviation = 200;
                fireDelay = 2500;

            }
            case WAND -> {
                meanDamage = 313;
                deviation = 107;
                fireDelay = 933;

            }
        }
    }
    public Weapon(int weaponType, Point position) throws IOException {
        this.position = position;
        height = 0.05;
        widthToHeight = 2;

        //TODO: init textures and dimensions
        type = weaponType;
        switch (type) {
            case SLINGSHOT -> {
                meanDamage = 10;
                deviation = 5;
                fireDelay = 2500;
                height = 0.03;
                widthToHeight = 0.7;
                try {
                    texture = ImageIO.read(new File("./assets/img/DoomSlingshot.png"));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            case PENCIL -> {
                meanDamage = 70;
                deviation = 35;
                fireDelay = 967;
                height = 0.03;
                widthToHeight = 0.3;
                try {
                    texture = ImageIO.read(new File("./assets/img/DoomPencil.png"));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
            case FIREFLOWER -> {
                meanDamage = 250;
                deviation = 200;
                fireDelay = 2500;
                height = 0.03;
                widthToHeight = 1;
                try {
                    texture = ImageIO.read(new File("./assets/img/DoomFireflower.png"));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            case WAND -> {
                meanDamage = 313;
                deviation = 107;
                fireDelay = 933;
                height = 0.03;
                widthToHeight = 0.7;
                try {
                    texture = ImageIO.read(new File("./assets/img/DoomWand.png"));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getType()
        {
            return type;
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

    public int getFireDelay()
    {
        return fireDelay;
    }

    public int shoot()
    {
        boolean add = false;
        if (Math.random()>= 0.5)
            add = true;
        int value = (int)(Math.random()*deviation);
        if (add)
            return meanDamage + value;
        else
            return meanDamage-value;

    }

}


