import java.util.Vector;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Coordination between generals. Generals report for duty (register), and
 * coordinate their attack here.
 */
public class Mission {

    public CyclicBarrier send_barrier;
    public CyclicBarrier receive_barrier;
    public Vector<General> generals;
    /* Since we process one round at a time, we collect messages here. */
    public Vector<Message> messages;
    int n, m;
    int id = 0;
    int started = 0;

    public Mission(int num_generals, int num_traitors) {
        n = num_generals;
        m = num_traitors;
        if (n <= 3 * m) { // "must"
            throw new IllegalArgumentException("Requires n > 3*m.");
        }
        generals = new Vector<>();
        messages = new Vector<>();
        // create two barrieres, one for commanders who recieve MSG from other commanders
        receive_barrier = new CyclicBarrier(n - 1, new ReceiveCleanup());
        // barrier for general who sends MSG
        send_barrier = new CyclicBarrier(n);
    }

    public int numRounds() {
        return m + 1;
    }

    /**
     * Report for duty. Registers General instance with the Mission and blocks
     * until all generals are accounted for. Since method is synchronized,
     * callers acquire intrinsic lock on this object and we can do stuff like
     * wait() and notify().
     * Could also use CyclicBarrier here.. not as much fun.
     */
    public synchronized void reportForDuty(General g) throws InterruptedException {
        g.assignId(id++);
        generals.add(g);
        if (generals.size() == n) {
            // We are last general to report, let's begin.
            started = 1;
            notifyAll();
        } else {
            /* Loop to handle spurious wakeups; e.g. from signals. */
            while (started == 0) {
                wait();
            }
        }
    }

    /**
     * Send your messages, if any for round.
     */
    public void sendRound(Vector<Message> msg, int id, int round) throws BrokenBarrierException, InterruptedException {
        /* For now, completion of round is hearing from all generals.  We could
         * add message loss and a timeout as well. */
        System.out.println("[" + id + "] send Round(" + round + " ) of messages" + msg);
        if (msg != null) {
            messages.addAll(msg);
        }
        System.out.println("\t[" + id + "] await message");
        int arrive_index = send_barrier.await(); //exception

        if (arrive_index == 0) {
            if (round == 0) {
                // Round 0 was special case, now commander drops out
                send_barrier = new CyclicBarrier(n - 1);
            } else {
                // This is not racy because threads will all reach receive barrier
                // before reentering here.
                send_barrier.reset();
            }
        }

    //    System.out.println("[" + id + "] sendRound(" + round + ") finished");
    }

    /**
     * Block until all generals are heard from, then return received messages.
     */
    public Vector<Message> receiveRound(int id, int round) throws BrokenBarrierException, InterruptedException {
        System.out.println("Process [" + id + "] receiveRound(" + round + ")");
        // grab a reference to this round's messages
        Vector<Message> round_msgs = messages;
        System.out.println("\t Process [" + id + "] await receives");
        int arrive_index = receive_barrier.await();

        // This is not racy because threads will all reach send barrier
        // before reentering here.
        if (arrive_index == 0) {
            receive_barrier.reset();
        }
        System.out.println("Process [" + id + "]  receiveRound(" + round + ") -> " + round_msgs);
        return round_msgs;
    }


    /**
     * Allocates a new list for next round's messages.
     */
    private class ReceiveCleanup implements Runnable {
        public void run() {
            System.out.println("\n\t---Clearing messages.--- \n");
            messages = new Vector<>();
        }
    }
}
