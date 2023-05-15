package entity;

public class Monster implements Sprite {

    private int health;

    private int damage;

    private int fireDelay;

    private int type;
    private static final int ZOMBIEMAN = 0;
    private static final int IMP = 1;
    private static final int DEMON = 2;
    private static final int CACODEMON = 3;
    private static final int MANCUBUS = 4;
    private static final int SPIDER = 5;

    public Monster(int type)
    {
        this.type = type;
        if (type == ZOMBIEMAN) {
            health = 20;
            damage = 9;
            fireDelay = 2500;
        }
        else if (type == IMP) {
            health = 60;
            damage = 14;
            fireDelay = 2500;
        }
        else if (type == DEMON){
            health = 140;
            damage = 22;
            fireDelay = 2500;
        }
        else if (type == CACODEMON){
            health = 360;
            damage = 35;
            fireDelay = 1767;
        }
        else if (type == MANCUBUS){
            health = 600;
            damage = 32;
            fireDelay = 1767;
        }
        else if (type == SPIDER){
            health = 3000;
            damage = 18;
            fireDelay = 1767;
        }
    }
    public int getType()
    {
        return type;
    }

    public int getfireDelay()
    {
        return fireDelay;
    }

    public int getHealth (){return health;}
    public int getDamage (){return damage;}



}
