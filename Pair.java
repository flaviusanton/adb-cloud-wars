/**
 * Created by fanton on 29/03/14.
 */
public class Pair implements Comparable<Pair>{
    public Integer x;
    public Integer y;
    public Integer priority = 0;
    public Boolean fire = false;

    public Pair(Integer fst, Integer snd) {
        this.x = fst;
        this.y = snd;
    }

    @Override
    public boolean equals(Object o) {
        Pair p = (Pair)o;

        if (p.x != x || p.y != y)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + priority + " " + fire + "]";
    }

    @Override
    public int compareTo(Pair o) {
        return o.priority - this.priority;
    }
}
