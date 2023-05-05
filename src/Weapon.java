public class Weapon {
    public static final int PISTOL = 2;
    public static final int SHOTGUN = 3;
    public static final int CHAINGUN = 4;
    public static final int ROCKETLAUNCHER = 5;
    public static final int PLASMAGUN = 6;
    public static final int BFG9000 = 7;

    public int meanDamage;
    public int deviation;
    public int fireDelay;
    public int type;



    public Weapon(int weaponType) {
        type = weaponType;
        if (type == PISTOL) {
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

    public int getfireDelay()
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


