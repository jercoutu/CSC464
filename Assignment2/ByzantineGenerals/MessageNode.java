import java.util.Vector;

public class MessageNode {
    public MessageNode parent;
    public Message message;
    //    boolean decision;
    public Vector<MessageNode> children;

    public MessageNode() {
        children = new Vector<>();
    }
}
