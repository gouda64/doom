package entity;

public class Player {
    private int health;
    private int ammo;
    private int armor;
    private Weapon[] inventory;

    private static final int PISTOL = 2;
    private static final int SHOTGUN = 3;
    private static final int CHAINGUN = 4;
    private static final int ROCKETLAUNCHER = 5;
    private static final int PLASMAGUN = 6;
    private static final int BFG9000 = 7;

    public Player()
    {
        health = 100;
        ammo = 50;
        armor = 0;
        inventory = new Weapon[6];
        inventory[0] = new Weapon(2);
    }

    public void pickUpWeapon(int type)
    {
        int a = 0;
        while(inventory[a]!=null || inventory[a].getType() != type || a<=5)
        {
            a++;
        }
        if (inventory[a] == null)
            inventory[a] = new Weapon(type);
        else if (inventory[a].getType() == type)
            ammo += 15;

    }

    public void pickUpHealth()
    {
        health++;
    }

    public int getInventorySize()
    {
        int a = 0;
        while (inventory[a]!=null && a!=inventory.length)
        {
            a++;
        }
        return a;
    }

    public int getHealth(){return health;}
    public int getAmmo(){return ammo;}
    public int getArmor(){return armor;}


    public void pickUpAmmo()
    {
        ammo++;
    }

    public void pickUpArmor()
    {
        armor++;
    }

    public void damage(int HP)
    {
        HP /= 2;
        armor -=HP;
        if (armor <0)
        {
            health += armor;
            armor = 0;
        }
        health-= HP;
    }



}
