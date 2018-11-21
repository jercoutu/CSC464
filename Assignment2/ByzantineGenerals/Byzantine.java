import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Byzantine {

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);

        System.out.println("how many generals and traitors ?");
        int NUM_GENERALS = sc.nextInt();
        int NUM_TRAITORS = sc.nextInt();

        Mission mission = new Mission(NUM_GENERALS, NUM_TRAITORS);

        ExecutorService ex = Executors.newFixedThreadPool(NUM_GENERALS); // thread with number of generals
        System.out.println("Started " + NUM_GENERALS + " generals.");

        for (int i = 0; i < NUM_GENERALS; i++) {
            General g = new General(mission);// create generals
            ex.execute(g);
        }
        System.out.println("Generals finished, exiting");
        ex.shutdown();
    }
}
