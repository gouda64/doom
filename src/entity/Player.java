package entity;

public class Player {
    private int health;
    private int ammo;
    private int armor;
    private Weapon[] inventory;
    private Weapon equipped;

    private static final int PISTOL = 2;
    private static final int SHOTGUN = 3;
    private static final int PLASMAGUN = 4;
    private static final int BFG9000 = 5;

    public Player()
    {
        health = 100;
        ammo = 50;
        armor = 0;
        inventory = new Weapon[6];
        inventory[0] = new Weapon(Weapon.PISTOL);
        equipped = inventory[0];
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

    public void pickUpItem(int type) {
        switch (type) {
            case Item.HEALTH -> {
                health++;
            }
            case Item.AMMO -> {
                ammo+=10;
            }
            case Item.ARMOR -> {
                armor+=10;
            }
        }
    }

    public int getInventorySize()
    {
        int a = 0;
        while (a!=inventory.length && inventory[a]!=null)
        {
            a++;
        }
        return a;
    }

    public int getHealth(){return health;}
    public int getAmmo(){return ammo;}
    public int getArmor(){return armor;}
    public Weapon getEquipped(){return equipped;}

    public void shot(){ammo--;}


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
