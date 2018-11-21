
/*
 * Fun thread-based solution to Byzantine Generals problem.  For algorithm, see
 * "The Byzantine Generals Problem", Lamport, Shostak, Pease 1982.
 */
 import java.util.Vector;
 import java.util.concurrent.BrokenBarrierException;

public class General implements Runnable {

    public final boolean commander_should_attack = true;
    public Mission mission;
    public MessageTree m_tree; //for messages
    int id;

    public General(Mission m) {
        this.mission = m;
    }

    /**
     *
     * @return true/false depending on if id is 0
     */
    public boolean amICommander() {
        return (id == 0);
    }

    public boolean majority(Vector<Message> messages) {
        int truth_sum = 0;
        for (Message m : messages) {
            truth_sum += (m.value ? 1 : -1);
        }
        return (truth_sum > 0);  // More trues than falses?
    }

    public void assignId(int id) {
        this.id = id;
        m_tree = new MessageTree(id);
    }

    public void run() {
        try {
            communicationPhase();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    /**
     * Communication phase: Iterative approach.
     */
    public void communicationPhase() throws InterruptedException, BrokenBarrierException {
        boolean decision_this_round = false;
        Vector<Message> messages = null;

        mission.reportForDuty(this);
        System.out.println("Reporting for duty... ... assigned id " + id);

        for (int round = 0; round < mission.numRounds(); round++) {

            // Sending phase
            if (amICommander() && round == 0) {
                Message m = new Message(commander_should_attack);
                m.path.add(id);
                Vector<Message> v = new Vector<>();
                v.add(m);
                mission.sendRound(v, id, round);
                break;
            }


            // Send out copies of received messages, adding self to path,
            // and including our decision.
            Vector<Message> newMessages = new Vector<>();
            if (round != 0) {
                for (Message m : messages) {
                    if (m.senderId() != id) {
                        Message m_new = new Message(m); // a copy
                        m_new.path.add(id);
                        m_new.value = decision_this_round;
                        newMessages.add(m_new);
                    }
                }
            }
            mission.sendRound(newMessages, id, round);

            // Receiving phase
            messages = mission.receiveRound(id, round);
            m_tree.insert(messages, round);

            // Deciding phase
            decision_this_round = majority(messages);
            System.out.println("\n\n We are attacking this round T or F \t" +decision_this_round);
        }
    }
}
