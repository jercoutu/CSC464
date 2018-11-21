import java.util.Vector;

/**
 * A tree of messages. Could be nested inside General. I'm sure this could be
 * simplified, but it is late.
 */
public class MessageTree {
    public MessageNode root;
    int owner_id;

    public MessageTree(int id) {
        this.owner_id = id;
    }

    /**
     * Given a received message, where should it live in the tree?
     */
    private MessageNode findParent(Message m, int round) {
        int i = 1;
        MessageNode node = root;

        /* Descend tree via prefix matches until we hit our rank. */
        while (i < round) {
            for (MessageNode n : node.children) {
                if (n.message.prefixMatch(m.path)) {
                    node = n;
                    break;
                }
            }
            i++;
        }
        System.out.println("[" + owner_id + "] findParent(" + m + ", " + round + ") -> n" + node);
        return node;
    }

    /**
     * Add received list of messages to ADT for later decision making.
     */
    public void insert(Vector<Message> messages, int round) {
        if (round == 0) {
            root = new MessageNode();
            root.parent = null;
            root.message = messages.firstElement();
            System.out.println("\t[" + owner_id + "] insert(..round 0..) -> root " + root);
        } else {
            for (Message m : messages) {
                MessageNode parent = findParent(m, round);
                MessageNode node = new MessageNode();
                node.message = m;
                parent.children.add(node);
                System.out.println("\t[" + owner_id + "]insert(" + m + "," + round + " ) -> parent " + parent.message);
            }
        }
    }
}
