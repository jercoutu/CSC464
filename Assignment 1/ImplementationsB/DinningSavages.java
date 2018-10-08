import java.util.LinkedList;
import java.util.Queue;

public class DinningSavages {


	public static void main(String[] args) {
		System.out.println("Creating Pot");
		PotMonitor p = new PotMonitor();
		System.out.println("MaxServings = 6, NumberOfSavages = 5");
		Savage s[] = new Savage[5];
		for(int i = 0; i<5; i++) {
			System.out.println("Creating Savage [" + (i+1) + "] thread ...");
			s[i] = new Savage(p, i);
			s[i].start();
		}
		System.out.println("Creating Cook thread ...");
		Cook c = new Cook(p);
		c.start();

	}
}
class Savage extends Thread implements Runnable  {
    PotMonitor pot;
    int n;
    public Savage (PotMonitor pot, int n) {this.pot= pot; this.n= n;}

    public void run() {
        while(true){

            try {
				pot.getserving(n);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            try {Thread.sleep(500);
            } catch (Exception e) { return;}

        }
    }
}

class Cook extends Thread implements Runnable {
    PotMonitor pot;
    public Cook (PotMonitor pot) {this.pot= pot;}

    public void run() {
      while(true)
		try {
			pot.fillpot();

		} catch (InterruptedException e) {

			e.printStackTrace();
		}


    }
}
class PotMonitor{

	public Queue<Integer> buf;
	private boolean empty;
	public int Max = 6;
	PotMonitor(){
		buf = new LinkedList<Integer>();
		empty = true;

	}
	public synchronized void getserving(int n) throws InterruptedException{
		while(empty) wait();
		System.out.println("Savage " + (n+1) + " is eating right now [remaining servings = " + (buf.size()-1) + "]");
		Integer serving = buf.remove();
		if(buf.isEmpty()) {
			empty = true;
			notifyAll();
		}
		try {
			Thread.sleep(500);
		}
		catch(InterruptedException ex){
				Thread.currentThread().interrupt();
		}

		}
	public synchronized void fillpot() throws InterruptedException{
		while(!empty) wait();
		System.out.println("Cook filled the pot again [remaining servings are " + Max + "]");
		for(int i=0;i<Max;i++){
		buf.add(i);
		}
		try {
			Thread.sleep(500);
		}
		catch(InterruptedException ex){
				Thread.currentThread().interrupt();
		}
		empty = false;
		notifyAll();
		}

}
