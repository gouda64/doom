package entity;

import graphics.Point;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.Math;

public class Weapon implements Sprite {
     static final int PISTOL = 2;
     static final int SHOTGUN = 3;
     static final int PLASMAGUN = 4;
     static final int BFG9000 = 5;

    private Point position;
    private BufferedImage texture;
    private double widthToHeight;
    private double height;

    private int meanDamage;
    private int deviation;
    private int fireDelay;
    private int type;

    public Weapon(int weaponType) {
        type = weaponType;
        switch (type) {
            case PISTOL -> {
                meanDamage = 10;
                deviation = 5;
                fireDelay = 2500;
            }
            case SHOTGUN -> {
                meanDamage = 70;
                deviation = 35;
                fireDelay = 967;
            }
            case PLASMAGUN -> {
                meanDamage = 250;
                deviation = 200;
                fireDelay = 2500;
            }
            case BFG9000 -> {
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
            case PISTOL -> {
                meanDamage = 10;
                deviation = 5;
                fireDelay = 2500;
            }
            case SHOTGUN -> {
                meanDamage = 70;
                deviation = 35;
                fireDelay = 967;
            }
            case PLASMAGUN -> {
                meanDamage = 250;
                deviation = 200;
                fireDelay = 2500;
            }
            case BFG9000 -> {
                meanDamage = 313;
                deviation = 107;
                fireDelay = 933;
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


