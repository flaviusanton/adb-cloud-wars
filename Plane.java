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

    public Plane clone(Plane p) {
        Plane plane = new Plane();
        plane.id = p.id;
        plane.tick = p.tick;
        plane.x = p.x;
        plane.y = p.y;
        plane.altitude = p.altitude;
        plane.direction = p.direction;
        plane.ammo = p.ammo;
        plane.color = p.color;
        plane.weapon = p.weapon;
        plane.weaponRange = p.weaponRange;

        return plane;
    }
}
