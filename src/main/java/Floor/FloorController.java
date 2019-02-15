package Floor;

import core.ElevatorMessage;
import core.EventListener;
import core.EventNotifier;
import core.RequestData;

import java.util.ArrayList;

import File.ReadFile;


public class FloorController {
	
	public static final int PORT = 62442;
	
	int numFloors;
	Floor[] floors;
	
	EventListener listener;
	
	public FloorController(int numFloors) {
		this.numFloors = numFloors;
		
		// initialize <numfloors> floors
		floors = new Floor[numFloors];
		for(int i=0; i<numFloors; i++) {
			floors[i] = new Floor(i, (i == numFloors-1), (i==0));
		}
		
		// listens for notifications from the scheduler that an elevator has arrived
		listener = new EventListener(PORT, "FLOOR ELEVATOR LISTENER");
	}
	
	
	public void startListen() {
		// listens for notifications from the scheduler that an elevator has arrived
		
		System.out.println("FLOOR CONTROLLER: Starting elevator listener...");
		
		for(;;) {
			ElevatorMessage msg = listener.waitForNotification();
			
			switch(msg.getType()) {
			case ELEV_PICKUP:
				System.out.println("\nFLOOR CONTROLLER: RECEIVED ELEVATOR PICKUP NOTIFICATION" + msg);
				floors[msg.getFloor()].elevArrival(msg.getDirection());
				floors[msg.getFloor()].passengerEnter();
				break;
			case ELEV_ARRIVAL:
				System.out.println("\nFLOOR CONTROLLER: RECEIVED ELEVATOR ARRIVAL NOTIFICATION" + msg);
				floors[msg.getFloor()].elevArrival(msg.getDirection());
				break;
			default:
					break;
			}
			
		}
	}
	
	public void start() throws InterruptedException {
		FloorController s = this;
		
		// start the thread that listens for notifications from the scheduler
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				s.startListen();
			}
		});
		
		
		t1.start();
		
		/* THIS IS WHERE YOU WOULD CALL THE INPUT FILE METHOD */
		//Input file
		
		ArrayList<RequestData> inputData = ReadFile.getData("inputFile.txt");
		// everything below this is just demo runs
		Thread.sleep(5000);
		
		if(inputData.get(0).goingUp()) {
			floors[inputData.get(0).getFloorNumber()].reqUp(inputData.get(0).getfloorToGo());
		}
		else {
			floors[inputData.get(0).getFloorNumber()].reqDown(inputData.get(0).getfloorToGo());
		}
		
		
		Thread.sleep(30);
		
		if(inputData.get(1).goingUp()) {
			floors[inputData.get(1).getFloorNumber()].reqUp(inputData.get(1).getfloorToGo());
		}
		else {
			floors[inputData.get(1).getFloorNumber()].reqDown(inputData.get(1).getfloorToGo());
		}
		
		if(inputData.get(2).goingUp()) {
			floors[inputData.get(2).getFloorNumber()].reqUp(inputData.get(2).getfloorToGo());
		}
		else {
			floors[inputData.get(2).getFloorNumber()].reqDown(inputData.get(2).getfloorToGo());
		}
		
	}
	
	public static void main(String[] args) {
		FloorController c = new FloorController(5);
		try {
			c.start();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
