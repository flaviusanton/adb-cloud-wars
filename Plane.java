/**
 * Created by fanton on 29/03/14.
 */
public class Plane {

    public int tick;
    public int id;
    public int x;
    public int y;
    public int ammo;
    public int altitude;
    public String direction;
    public int weaponRange;
    public Boolean weapon;
    public String color;

    public Plane() {

    }

    @Override
    public String toString() {
        return "ID: " + id + "\n" +
                "X: " + x + "\n" +
                "Y: " + y + "\n" +
                "Dir:" + direction + "\n" +
                "Ammo: " + ammo + "\n" +
                "Alt: "  + altitude + "\n";
    }
}
