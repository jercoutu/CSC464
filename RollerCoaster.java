import java.math.*;

class RollerCoaster {
  public static int PASSENGER_NUM = 25;// Number of people totally
  public static int CAR_NUM = 1; // Number of passenger cars
  public static int SEAT_AVAIL = 4; // Number of passengers a car holds
  public static void main(String[] args) {
    Monitor rcMon = new Monitor();

    Car theCar;
    Passenger aPassenger;

    /* Create arrays of threads for initialization */
    Thread t1[] = new Thread[PASSENGER_NUM];
    Thread t2[] = new Thread[CAR_NUM];
    /* Fill the thread arrays */
    for (int i = 0; i < PASSENGER_NUM; i++) {
      aPassenger = new Passenger(i, rcMon);
      t1[i] = new Thread(aPassenger);
    }
    for (int i = 0; i < CAR_NUM; i++) {
      theCar = new Car(i, rcMon);
      t2[i] = new Thread(theCar);
    }
    for(int i = 0; i < PASSENGER_NUM; i++) {
      t1[i].start();
    }
    for(int i = 0; i < CAR_NUM; i++) {
      t2[i].start();
    }
    try {
      for (int i = 0; i < PASSENGER_NUM; i++) {
        t1[i].join();
      }
    } catch (InterruptedException e) {
      System.err.println("Passenger thread join interruption");
    }
    try {
      for (int i = 0; i < CAR_NUM; i++) {
        t2[i].join();
      }
    } catch (InterruptedException e) {
      System.err.println("Car thread join interruption");
    }
    System.out.println("Program has terminated Normally");
  }
} // end of RollerCoaster

//========================= PASSENGER CLASS ===========================
class Passenger implements Runnable {
  private int id;
  private Monitor passengerMon;
  public Passenger(int i, Monitor monitorIn) {
    id = i;
    this.passengerMon = monitorIn;
  }
  public void run() {
    for(int i=0; i<1; i++){ // every passenger only takes one ride
      try{
        Thread.sleep((int)( Math.random()*2000));
      }catch(InterruptedException e){
      }
      passengerMon.tryToGetOnCar(id);
    }
  }
} // end of Passenger class

class Car implements Runnable {
  private int id; // Car ID
  private Monitor carMon;
  public Car(int i, Monitor monitorIn) {
    id = i;
    this.carMon = monitorIn;
  }
  public void run() {
    while(true) {
      carMon.passengerGetOn(id);
      try{
        Thread.sleep((int)(Math.random()*2000));
      }catch(InterruptedException e){
      } // Car runs for a while
      carMon.passengerGetOff(id);
    }
  }
} // end of Car class

class Monitor {
  private int i, line_length; // Number of passengers waiting to board the car.
  private int seats_available = 0;
  boolean coaster_loading_passengers = false;
  boolean passengers_riding = true;

  private Object notifyPassenger = new Object(); // enter/exit protocol provides mutual exclusion.
  private Object notifyCar = new Object(); // the car waits on this.

  public void tryToGetOnCar(int i) {
    synchronized (notifyPassenger) {
      while (!seatAvailable()) {
        try {
          notifyPassenger.wait(); // Notify the passenger to wait
          } catch (InterruptedException e){}
      }
    }
    System.out.println("Passenger "+ i + " gets in car");
    synchronized (notifyCar) {notifyCar.notify();}
  }

  private synchronized boolean seatAvailable() {
    if ((seats_available > 0)
        && (seats_available <= RollerCoaster.SEAT_AVAIL)
        && (!passengers_riding)) {
      seats_available--;
      return true;
    } else return false;
  }

  public void passengerGetOn(int i) {
    synchronized (notifyCar) {
      while (!carIsRunning()) {
        try {
          notifyCar.wait();
          } catch (InterruptedException e){}
      }
    }
    System.out.println("The Car is full and starts running");
    synchronized(notifyPassenger) {notifyPassenger.notifyAll();}
  }

  private synchronized boolean carIsRunning() {
    if (seats_available == 0) {
      seats_available = RollerCoaster.SEAT_AVAIL;
      coaster_loading_passengers = true; // Indicating car is running.
      passengers_riding = true; // passengers are riding in the car.
      return true;
    } else return false;
  }

  public void passengerGetOff(int i) {
    synchronized (this) {
      // reset parameters
      passengers_riding = false;
      coaster_loading_passengers = false;
    }
   synchronized(notifyPassenger) {notifyPassenger.notifyAll();}
  }
} 
