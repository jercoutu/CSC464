import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CigaretteSmokers {

 boolean isTobacco = false;
 boolean isPaper = false;
 boolean isMatch = false;

 public static Semaphore tobaccoSem = new Semaphore(0);
 public static Semaphore paperSem = new Semaphore(0);
 public static Semaphore matchSem = new Semaphore(0);


 public static Semaphore tobacco = new Semaphore(0);
 public static Semaphore paper = new Semaphore(0);
 public static Semaphore match = new Semaphore(0);

 public static Semaphore agentSem = new Semaphore(1);

 public static Lock mutex = new ReentrantLock();


 public void initPushers() {
  Thread pusherA = new Thread() {
   public void run() {
    while(true) {
     try {
      tobacco.acquire();
      System.out.println("Pusher A for tobacco is active");
      mutex.lock();
      try {
       if(isPaper) {
        isPaper = false;
        matchSem.release();
       } else if(isMatch) {
        isMatch = false;
        paperSem.release();
       } else {
        isTobacco = true;
       }
      } finally {
       mutex.unlock();
      }
     } catch (InterruptedException e) {
      e.printStackTrace();
     }
    }
   };
  };
  Thread pusherB = new Thread() {
   public void run() {
    while(true) {
     try {
      paper.acquire();
      System.out.println("Pusher B for Paper is active");
      mutex.lock();
      try {
       if(isTobacco) {
        isTobacco = false;
        matchSem.release();
       } else if(isMatch) {
        isMatch = false;
        tobaccoSem.release();
       } else {
        isPaper = true;
       }
      } finally {
       mutex.unlock();
      }
     } catch (InterruptedException e) {
      e.printStackTrace();
     }
    }
   };
  };
  Thread pusherC = new Thread() {
   public void run() {
    while(true) {
     try {
      match.acquire();
      System.out.println("Pusher C for Match is active");
      mutex.lock();
      try {
       if(isPaper) {
        isPaper = false;
        tobaccoSem.release();
       } else if(isTobacco) {
        isTobacco = false;
        paperSem.release();
       } else {
        isMatch = true;
       }
      } finally {
       mutex.unlock();
      }
     } catch (InterruptedException e) {
      e.printStackTrace();
     }
    }
   };

  };
  pusherA.start();
  pusherB.start();
  pusherC.start();
 }

 public void initSmokers() {
  Thread tobaccoSmoker = new Thread() {
   @Override
   public void run() {
    while(true) {
     try {
      tobaccoSem.acquire();
      makeCigarette();
      agentSem.release();
      smoke();
     } catch (InterruptedException e) {
      e.printStackTrace();
     }
    }
   }

   public void makeCigarette() {
    System.out.println("tobaccoSmoker is making cigratte");
    try {
     sleep(5000);
    } catch (InterruptedException ex) {
    }
    System.out.println("tobaccoSmoker is cigratte making completed");
   }

   public void smoke() {
    System.out.println("tobaccoSmoker is smoking");
    try {
     sleep(5000);
    } catch (InterruptedException ex) {
    }
   }
  };

  Thread matchSmoker = new Thread() {
   @Override
   public void run() {
    while(true) {
     try {
      matchSem.acquire();
      makeCigarette();
      agentSem.release();
      smoke();
     } catch (InterruptedException e) {
      e.printStackTrace();
     }
    }
   }

   public void makeCigarette() {
    System.out.println("matchSmoker is making cigratte");
    try {
     sleep(5000);
    } catch (InterruptedException ex) {
    }
    System.out.println("matchSmoker is cigratte making completed");
   }

   public void smoke() {
    System.out.println("matchSmoker is smoking");
    try {
     sleep(5000);
    } catch (InterruptedException ex) {
    }
   }
  };

  Thread paperSmoker = new Thread() {
   @Override
   public void run() {
    while(true) {
     try {
      paperSem.acquire();
      makeCigarette();
      agentSem.release();
      smoke();
     } catch (InterruptedException e) {
      e.printStackTrace();
     }
    }
   }

   public void makeCigarette() {
    System.out.println("paperSmoker is making cigratte");
    try {
     sleep(5000);
    } catch (InterruptedException ex) {
    }
    System.out.println("paperSmoker is cigratte making completed");
   }

   public void smoke() {
    System.out.println("paperSmoker is smoking");
    try {
     sleep(5000);
    } catch (InterruptedException ex) {
    }
   }
  };

  tobaccoSmoker.start();
  matchSmoker.start();
  paperSmoker.start();
 }

 public void initAgents() {
  Thread agentA = new Thread() {
   @Override
   public void run() {
    while(true) {
     try {
      agentSem.acquire();
      System.out.println("Agent A is active and will release provide Tobacco & Paper ingredients.");
      tobacco.release();
      paper.release();
     } catch (InterruptedException e) {
      e.printStackTrace();
     }
    }
   }
  };
  Thread agentB = new Thread() {
   @Override
   public void run() {
    while(true) {
     try {
      agentSem.acquire();
      System.out.println("Agent B is active and will release provide Match & Paper ingredients.");
      match.release();
      paper.release();
     } catch (InterruptedException e) {
      e.printStackTrace();
     }
    }
   }
  };
  Thread agentC = new Thread() {
   @Override
   public void run() {
    while(true) {
     try {
      agentSem.acquire();
      System.out.println("Agent C is active and will release provide Tobacco & Match ingredients.");
      tobacco.release();
      match.release();
     } catch (InterruptedException e) {
      e.printStackTrace();
     }
    }
   }
  };
  agentA.start();
  agentB.start();
  agentC.start();
 }

 public static void main(String[] args) {
  CigaretteSmokers cs = new CigaretteSmokers();
  cs.initAgents();
  cs.initPushers();
  cs.initSmokers();
 }
}
