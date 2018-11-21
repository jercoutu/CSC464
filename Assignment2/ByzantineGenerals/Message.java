import java.util.Vector;

public class Message {

    public boolean value;
    public Vector<Integer> path;

    public Message(boolean value) {
        path = new Vector<>();
    }

    public Message(Message m2) {
        value = m2.value;
        path = (Vector<Integer>) m2.path.clone();
    }

    /**
     * @see method assignID
     * @return Returns the last component of the vector.
     */
    public int senderId() {
        return path.lastElement();
    }

    /**
     * Return true if elements of path match beginning of p
     */
    public boolean prefixMatch(Vector<Integer> p) {
        String s = path + ".prefixMatch(" + p + ")";
        for (int i : path) {
            if (i != p.elementAt(path.indexOf(i))) {
                System.out.println("\t" + s + "-> false");
                return false;
            }
        }
        System.out.println("\t" + s + "-> true");
        return true;
    }

    @Override public String toString() {
        return "M:" + value + "," + path;
    }
}
