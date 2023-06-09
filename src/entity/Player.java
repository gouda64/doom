package entity;

public class Player {
    private int health;
    private int ammo;
    private int armor;
    private Weapon[] inventory;
    private Weapon equipped;

    public int shotTime = -1;
    public int timeSinceFired;

    public Player()
    {
        health = 100;
        ammo = 30;
        armor = 0;

        inventory = new Weapon[6];
        inventory[0] = new Weapon(Weapon.SLINGSHOT);
        equipped = inventory[0];

        timeSinceFired = equipped.getFireDelay();
    }

    public boolean equipt(int a)
    {
        if (inventory[a-1] !=null){
            equipped = inventory[a-1];
            return true;
        }
        return false;
    }

    public void pickUpWeapon(int type)
    {
        int a = 0;
        while(a<=5)
        {
            if (inventory[a] == null) {
                inventory[a] = new Weapon(type);
                return;
            }
            else if (inventory[a].getType() == type) {

                ammo += 15;
                return;
            }
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
                health+=2;
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

    public Weapon[] getInventory() {
        return inventory;
    }

    public int getHealth(){return health;}
    public int getAmmo(){return ammo;}
    public int getArmor(){return armor;}
    public Weapon getEquipped(){return equipped;}

    public int getFireDelay() {
        return equipped.getFireDelay();
    }

    public void shot(){
        timeSinceFired = 0;
        ammo--;
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
