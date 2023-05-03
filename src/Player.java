public class Player {
    public int health;
    public int ammo;
    public int armour;
    public Weapon[] inventory;

    public Player()
    {
        health = 100;
        ammo = 50;
        armour = 0;
        inventory = new Weapon[7];
    }

}
