import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class CigaretteSmokers {
    Smoker paperSmoker = new Smoker();
    Smoker tobaccoSmoker = new Smoker();
    Smoker matchSmoker = new Smoker();
    Agent agent = new Agent();

    // create mutex
    static Semaphore mutex = new Semaphore(1);
    static Semaphore paperAndTobacco = new Semaphore(0);
    static Semaphore paperAndMatches = new Semaphore(0);
    static Semaphore tobaccoAndMatches = new Semaphore(0);

    private class Agent {
        boolean readyForNextItem = true;
        Random random = new Random();

        public void placeItems() throws InterruptedException {
            readyForNextItem = false;
            int randomIngredients = random.nextInt(3); // randomly generate items to place
            switch (randomIngredients) {
                case 0:
                    paperAndTobacco.release();
                    break;
                case 1:
                    paperAndMatches.release();
                    break;
                case 2:
                    tobaccoAndMatches.release();
                    break;
            }
        }
        public void signalCompletion() {
            readyForNextItem = true;
        }

        public boolean getReadyForNextItem() {
            return readyForNextItem;
        }
    }

    private class Smoker {
        // lock mutex and acquire ingredients
        public void getIngredients(Semaphore ingredient) throws InterruptedException {
            mutex.acquire();
            ingredient.acquire();
        }
        public void smoke(String name) {
            System.out.println(name + " is smoking their cigarette.");
            try {
                Thread.sleep(500);
            }
            catch(InterruptedException ex){
                    Thread.currentThread().interrupt();
            }
        }

        public void signalCompletion() {
            agent.signalCompletion();
            mutex.release();
        }
    }

    public void smokeAway() throws InterruptedException {
        while (true) {
            // if the mutex is available and the agent is ready to place an item
            if (mutex.availablePermits() > 0 && agent.getReadyForNextItem()) {
                agent.placeItems();
                if (paperAndTobacco.availablePermits() > 0 ) {
                    matchSmoker.getIngredients(paperAndTobacco);
                    matchSmoker.smoke("Smoker with matches");
                    matchSmoker.signalCompletion();
                } else if (paperAndMatches.availablePermits() > 0 ) {
                    tobaccoSmoker.getIngredients(paperAndMatches);
                    tobaccoSmoker.smoke("Smoker with tobacco");
                    tobaccoSmoker.signalCompletion();
                } else {
                    paperSmoker.getIngredients(tobaccoAndMatches);
                    paperSmoker.smoke("Smoker with paper");
                    paperSmoker.signalCompletion();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CigaretteSmokers cs = new CigaretteSmokers();
        cs.smokeAway();
    }
}
