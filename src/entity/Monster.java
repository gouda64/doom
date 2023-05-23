package entity;

import graphics.Point;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Monster implements Sprite {

    private Point position;
    private final Point startPos;
    private BufferedImage texture;
    private double widthToHeight;
    private double height;


    private int health;
    private double speed;
    private int damage;

    private int fireDelay;
    public int timeSinceFired;
    private boolean visible = true;

    public int shotTime = -1;

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
        startPos = initPosition;
        timeSinceFired = 0;
        speed = 0.01; //TODO: monster speeds
        //TODO: adjust difficulty

        height = 0.1;
        widthToHeight = 0.5;

        this.type = type;
        switch (type) {
            case ZOMBIEMAN -> {  //TODO: init textures and dimensions
                health = 20;
                damage = 2;
                fireDelay = 2500;
                try {

                        texture = ImageIO.read(new File("./assets/img/DoomZombie.png"));

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            case IMP -> {
                health = 60;
                damage = 3;
                fireDelay = 2500;
                try {

                    texture = ImageIO.read(new File("./assets/img/DoomImp.png"));

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            case DEMON -> {
                health = 140;
                damage = 5;
                fireDelay = 2500;
                try {

                    texture = ImageIO.read(new File("./assets/img/DoomDemon.png"));

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            case CACODEMON -> {
                health = 360;
                damage = 7;
                fireDelay = 1767;
                try {

                    texture = ImageIO.read(new File("./assets/img/DoomCocademon.png"));

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            case MANCUBUS -> {
                health = 600;
                damage = 8;
                fireDelay = 1767;
                try {

                    texture = ImageIO.read(new File("./assets/img/DoomMancubus.png"));

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            case SPIDER -> {
                health = 3000;
                damage = 9;
                fireDelay = 1767;
                try {

                    texture = ImageIO.read(new File("./assets/img/DoomSpider.png"));

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

    public int getFireDelay()
    {
        return fireDelay;
    }

    public int getHealth (){return health;}
    public int getDamage (){return damage;}
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) visible = false;
    }


    public double getSpeed() {
        return speed;
    }

    public Point getStartPos() {
        return startPos;
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



}
