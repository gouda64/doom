package entity;

import graphics.Point;

import java.awt.image.BufferedImage;

public class Monster implements Sprite {

    private Point position;
    private BufferedImage texture;
    private double widthToHeight;
    private double height;


    private int health;
    private double speed;
    private int damage;

    private int fireDelay;
    public int timeSinceFired;

    private int type;
    private static final int ZOMBIEMAN = 0;
    private static final int IMP = 1;
    private static final int DEMON = 2;
    private static final int CACODEMON = 3;
    private static final int MANCUBUS = 4;
    private static final int SPIDER = 5;

    public Monster(int type, Point initPosition)
    {
        position = initPosition;
        timeSinceFired = 0;
        speed = 0.5;

        this.type = type;
        switch (type) {
            case ZOMBIEMAN -> {  //TODO: init textures and dimensions
                health = 20;
                damage = 9;
                fireDelay = 2500;
            }
            case IMP -> {
                health = 60;
                damage = 14;
                fireDelay = 2500;
            }
            case DEMON -> {
                health = 140;
                damage = 22;
                fireDelay = 2500;
            }
            case CACODEMON -> {
                health = 360;
                damage = 35;
                fireDelay = 1767;
            }
            case MANCUBUS -> {
                health = 600;
                damage = 32;
                fireDelay = 1767;
            }
            case SPIDER -> {
                health = 3000;
                damage = 18;
                fireDelay = 1767;
            }
        }
    }
    public int getType()
    {
        return type;
    }

    public int getFireDelay()
    {
        return fireDelay;
    }

    public int getHealth (){return health;}
    public int getDamage (){return damage;}
    public void takeDamage(int damage) {
        health -= damage;
    }


    public double getSpeed() {
        return speed;
    }



    @Override
    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
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



}
