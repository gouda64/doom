package entity;

import graphics.Point;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.Math;

public class Weapon implements Sprite {
    private static final int PISTOL = 2;
    private static final int SHOTGUN = 3;
    private static final int CHAINGUN = 4;
    private static final int ROCKETLAUNCHER = 5;
    private static final int PLASMAGUN = 6;
    private static final int BFG9000 = 7;

    private Point position;
    private BufferedImage texture;

    private int meanDamage;
    private int deviation;
    private int fireDelay;
    private int type;




    public Weapon(int weaponType, Point initPosition) throws IOException {
        position = initPosition;
        type = weaponType;
        if (type == PISTOL) { //TODO: assign texture based on imgs
            meanDamage = 10;
            deviation = 5;
            fireDelay = 2500;
        } else if (type == SHOTGUN) {
            meanDamage = 70;
            deviation = 35;
            fireDelay = 967;
        } else if (type == CHAINGUN) {
            meanDamage = 10;
            deviation = 5;
            fireDelay = 8833;
        } else if (type == ROCKETLAUNCHER) {
            meanDamage = 218;
            deviation = 80;
            fireDelay = 1767;
        } else if (type == PLASMAGUN) {
            meanDamage = 25;
            deviation = 20;
            fireDelay = 11667;
        } else if (type == BFG9000) {
            meanDamage = 3130;
            deviation = 1070;
            fireDelay = 933;
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


