import java.util.concurrent.Semaphore;

public class UnisexBathroom{
   static Semaphore bathroom;
   static Semaphore men_only;
   static Semaphore women_only;

   public static void main(String[] args){
    if (args.length != 2) {
        printUsage();
    }
    int totMen = 0;
    int totWomen = 0;

    try {
        totMen = Integer.parseInt(args[0]);
        totWomen = Integer.parseInt(args[1]);
    }
    catch (NumberFormatException e) {
        printUsage();
    }

    bathroom= new Semaphore(1);
    men_only= new Semaphore(1);
    women_only= new Semaphore(1);

    System.out.println("Working\t\tEntering\tIn Bathroom\tLeaving");
    System.out.println("----------------------------------------------------------");

    Thread[] men = new Thread[totMen];
    for (int i = 0; i < totMen; i++) {
        men[i] = new ManThread(i);
        men[i].start();
    }

    Thread[] women = new Thread[totWomen];
    for (int i = 0; i < totWomen; i++) {
        women[i] = new WomanThread(i);
        women[i].start();
    }

    for (int i = 0; i < totMen; i++) {
        try {
            men[i].join();
        }
        catch (InterruptedException e) {
        }
    }

    for (int i = 0; i < totWomen; i++) {
        try {
            women[i].join();
        }
        catch (InterruptedException e) {
        }
    }

    System.exit(0);
}

private static void printUsage() {
    System.out.println("Usage: java UnisexBathroom <totMen> <totWomen>");
    System.out.println("  <totMen>: Total number of men.");
    System.out.println("  <totWomen>: Total number of women.");
    System.exit(-1);
}

public static void randomSleep(int max) {
    try {
        Thread.sleep((int) (Math.random() * max));
    }
    catch (InterruptedException e) {
    }
}

private static class ManThread extends Thread {

    private int id;

    public ManThread(int id) {
        this.id = id;
    }

    public void run() {
        doWork();
        if(UnisexBathroom.men_only.availablePermits()==0){
           useBathroom();
        }//if
        else{
        try{
        UnisexBathroom.bathroom.acquire();
        UnisexBathroom.men_only.acquire();
        }catch(InterruptedException e){
           System.out.println(e);
           System.exit(-1);
        }
        useBathroom();
        UnisexBathroom.men_only.release();
        UnisexBathroom.bathroom.release();
        }//else
        doWork();
    }//run

    private void doWork() {
        System.out.println("Man " + id);
        UnisexBathroom.randomSleep(10000);
    }

    private void useBathroom() {
        System.out.println("\t\tMan " + id);
        UnisexBathroom.randomSleep(100);
        System.out.println("\t\t\t\tMan " + id);
        UnisexBathroom.randomSleep(500);
        System.out.println("\t\t\t\t\t\tMan " + id);
        UnisexBathroom.randomSleep(100);
    }
}

private static class WomanThread extends Thread {

    private int id;

    public WomanThread(int id) {
        this.id = id;
    }

    public void run() {
        doWork();
        if(UnisexBathroom.women_only.availablePermits()==0){
           useBathroom();
        }//if
        else{
        try{
        UnisexBathroom.bathroom.acquire();
        }catch(InterruptedException e){
           System.out.println(e);
           System.exit(-1);
        }
        useBathroom();
        UnisexBathroom.women_only.release();
        UnisexBathroom.bathroom.release();
        }//else
        doWork();
    }//run

    private void doWork() {
        System.out.println("Woman " + id);
        UnisexBathroom.randomSleep(10000);
    }

    private void useBathroom() {
        System.out.println("\t\tWoman " + id);
        UnisexBathroom.randomSleep(100);
        System.out.println("\t\t\t\tWoman " + id);
        UnisexBathroom.randomSleep(500);
        System.out.println("\t\t\t\t\t\tWoman " + id);
        UnisexBathroom.randomSleep(100);
        }
    }
}
