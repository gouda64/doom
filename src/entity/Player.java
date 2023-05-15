package entity;

public class Player {
    private int health;
    private int ammo;
    private int armour;
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
        armour = 0;
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

    public void pickUpAmmo()
    {
        ammo++;
    }

    public void pickUpArmour()
    {
        armour++;
    }

    public void damage(int HP)
    {
        HP /= 2;
        armour-=HP;
        if (armour<0)
        {
            health += armour;
            armour = 0;
        }
        health-= HP;
    }



}
