import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Scanner;

public class RollerCoaster {

	protected final int CAR_CAPACITY;

	private int passengersInCar = 0; //Number of passengers in the car
	private boolean carIsWaitingPassengers = false; //True if the car is waiting for passengers

	private final ReentrantLock rollerCoasterLock;
	private final Condition carLoad; //Signaled when the car can load passengers
	private final Condition carUnload; //Signaled when the car is unloading its passengers
	private final Condition carIsFull; //Signaled when the car is full

	/**
	 * Creates a Roller Coaster implementation using monitor.
	 * @param carCapacity number of passengers per car
	 */
	public RollerCoaster(int carCapacity) {
		this.CAR_CAPACITY = carCapacity;

		rollerCoasterLock = new ReentrantLock();
		carLoad = rollerCoasterLock.newCondition();
		carUnload = rollerCoasterLock.newCondition();
		carIsFull = rollerCoasterLock.newCondition();
	}

	/**
	 * Requests to take ride in the car.
	 * @param passengerId identifier of the passenger
	 */
	public void takeRide(final int passengerId) {
		rollerCoasterLock.lock();

		//Waits the car become available:
		while (!carIsWaitingPassengers || passengersInCar == CAR_CAPACITY) {
			System.out.println("Passenger " + passengerId + ": Waiting for the car");
			carLoad.awaitUninterruptibly();
		}

		passengersInCar++;
		System.out.println("Passenger " + passengerId + ": Will take ride in the car");
		if (passengersInCar == CAR_CAPACITY) {
			//Notifies the car that it is full:
			carIsFull.signal();
		}
		//Waits until the car gets full:
		carIsFull.awaitUninterruptibly();
		//Waits the end of the ride:
		carUnload.awaitUninterruptibly();
		System.out.println("Passenger " + passengerId + ": Left the car");

		rollerCoasterLock.unlock();
	}

	/**
	 * Requests the car to carry passengers.
	 */
	public void load() {
		rollerCoasterLock.lock();

		//Waits for the car if it is riding:
		while (passengersInCar > 0) {
			carUnload.awaitUninterruptibly();
		}
		carIsWaitingPassengers = true;
		//Notifies the passengers the car is ready to load them:
		carLoad.signalAll();
		System.out.println("Car: Waiting for passengers");
		//Waits until the car gets full:
		carIsFull.awaitUninterruptibly();
		carIsWaitingPassengers = false;
		//Notifies the passengers that the car is full:
		carIsFull.signalAll();
		System.out.println("Car: The car is full. Going around...\n");

		rollerCoasterLock.unlock();
	}

	/**
	 * Unload the passengers from the car.
	 */
	public void unload() {
		rollerCoasterLock.lock();
		System.out.println("Car: Ride is done");
		//Unload the passengers:
		carUnload.signalAll();
		rollerCoasterLock.unlock();
		passengersInCar = 0;
		System.out.println("Car: Unloading passengers");
	}

	public static void main(String[] args) {
		int numCars = 1;
		int carCapacity;
		int numPassengers;
		Scanner scannerInt = new Scanner(System.in);
		do {
			System.out.print("Number of passengers per car: ");
			carCapacity = scannerInt.nextInt();
		} while (carCapacity <= 0);
		do {
			System.out.print("Number of passengers: ");
			numPassengers = scannerInt.nextInt();
		} while (numPassengers <= 0);
		System.out.println();
		testRollerCoaster(carCapacity, numPassengers);
	}

	private static void testRollerCoaster(int carCapacity, int numPassengers) {
		final RollerCoaster rollerCoaster = new RollerCoaster(carCapacity);

		Thread carThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					rollerCoaster.load();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
					}
					rollerCoaster.unload();
				}
			}
		}, "Car");
		carThread.start();

		for (int i = 0; i < numPassengers; i++) {
			final int passengerId = i;
			Thread passengerThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						rollerCoaster.takeRide(passengerId);
					}
				}
			}, "Passenger " + passengerId);
			passengerThread.start();
		}
	}
}
