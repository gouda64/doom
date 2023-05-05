public class Player {
    public int health;
    public int ammo;
    public int armour;
    public Weapon[] inventory;

    public static final int PISTOL = 2;
    public static final int SHOTGUN = 3;
    public static final int CHAINGUN = 4;
    public static final int ROCKETLAUNCHER = 5;
    public static final int PLASMAGUN = 6;
    public static final int BFG9000 = 7;

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
