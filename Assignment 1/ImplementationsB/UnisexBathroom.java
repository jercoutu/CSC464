import java.util.Random;
import java.util.concurrent.Semaphore;

public class UnisexBathroom extends Thread {

    public static Semaphore USERS = new Semaphore(0);
    public static Semaphore PUBLIC_CR = new Semaphore(0);
    public static Semaphore USAGE = new Semaphore(1);

    public static final int LIST = 3;
    public static int a = 0;
    public static int c = 0;

    public static int VACANCY = LIST;

    class MEN extends Thread {

        int iD;
        boolean notUsed = true;

        public MEN(int i) {
            iD = i;
        }

        public void run() {
            while (notUsed) {
                try {
                    USAGE.acquire();
                    if (VACANCY > 0) {
                        System.out.println("MALE " + this.iD + " Is USING");
                        VACANCY--;
                        USERS.release();
                        USAGE.release();
                        try {
                            PUBLIC_CR.acquire();
                            notUsed = false;
                            this.USING_THE_CR();
                        } catch (InterruptedException ex) {
                        }
                    } else {
                        USAGE.release();
                        notUsed = false;
                    }
                } catch (InterruptedException ex) {
                }
            }
        }

        public void USING_THE_CR() {
            System.out.println("MALE " + this.iD + " is done USING the Comfort Room");
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
            }
        }

    }

    class WOMEN extends Thread {

        int iD;
        boolean notUsed = true;

        public WOMEN(int i) {
            iD = i;
        }

        public void run() {
            while (notUsed) {
                try {
                    USAGE.acquire();
                    if (VACANCY > 0) {
                        System.out.println("FEMALE " + this.iD + " Is USING");
                        VACANCY--;
                        USERS.release();
                        USAGE.release();
                        try {
                            PUBLIC_CR.acquire();
                            notUsed = false;
                            this.USING_THE_CR();
                        } catch (InterruptedException ex) {
                        }
                    } else {

                        USAGE.release();
                        notUsed = false;
                    }
                } catch (InterruptedException ex) {
                }
            }
        }

        public void USING_THE_CR() {
            System.out.println("FEMALE " + this.iD + " is DONE USING the Comfort Room");
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
            }
        }

    }

    class BATHROOM extends Thread {

        public BATHROOM() {
        }

        public void run() {
            while (true) {  // runs in an infinite loop
                try {
                    USERS.acquire();
                    USAGE.release();
                    VACANCY++;
                    PUBLIC_CR.release();
                    USAGE.release();
                    this.USING();
                } catch (InterruptedException ex) {
                }
            }
        }

        public void USING() {
            System.out.println("The Comfort Room is occupied");
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
    }

    public static void main(String args[]) {

        UnisexBathroom BR = new UnisexBathroom();
        BR.start();  // Let the simulation begin
    }

    public void run() {
        BATHROOM b = new BATHROOM();  //WORKING THREADS ON BATHROOM
        b.start();

        /* THIS WILL GENERATE THE USERS*/
        for (int i = 1; i < 16; i++) {

            Random random = new Random();
            int temp = random.nextInt(2);
            if (temp == 0) {
                a++;

                MEN m = new MEN(a);
                m.start();

            } else {
                c++;
                WOMEN w = new WOMEN(c);
                w.start();

            }

            try {
                sleep(2000);
            } catch (InterruptedException ex) {
            };
        }
    }
}
