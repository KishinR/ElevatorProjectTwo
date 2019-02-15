package Floor;

import core.Button;
import core.ElevatorMessage;
import core.EventListener;
import core.EventNotifier;
import core.Lamp;
import Scheduler.Scheduler;

public class Floor{
	
	
	int floorNum = -1;	
	boolean highestFloor = false;  // highest floors have no up requests
	boolean lowestFloor = false;  // lowest floors have no down requests
	
	EventNotifier notifier = null; // notifies the scheduler that we requested an elevator
	
	Button reqBtnUp = null; 
	Button reqBtnDown = null;
	
	Lamp reqLampUp = null;
	Lamp reqLampDown = null;
	
	Lamp directionLampUp = null;
	Lamp directionLampDown = null;
	
	int requestFloor = -1;
	
	public Floor(int num, boolean highestFloor, boolean lowestFloor) {
		this.floorNum = num;
		this.highestFloor = highestFloor;
		this.lowestFloor = lowestFloor;
		
		// buttons, button lamps, and direction lamps
		if(!highestFloor) {
			reqBtnUp = new Button("Floor " + floorNum + " Request UP btn", true);
			reqLampUp = new Lamp("Floor " + floorNum + " Request UP lamp",false);
		}
		if(!lowestFloor) {
			reqBtnDown = new Button("Floor " + floorNum + " Request DOWN btn", true);
			reqLampDown = new Lamp("Floor " + floorNum + " Request DOWN lamp",false);
		}
		this.directionLampUp = new Lamp("Floor " + floorNum + " direction UP lamp", false);
		this.directionLampDown = new Lamp("Floor " + floorNum + " direction DOWN lamp", false);
		
		
		this.notifier = new EventNotifier(Scheduler.PORT, "FLOOR NOTIFIER");
	}
	
	public void reqUp(int num) {
		// num -> floor we want to go to
		// send request from floor (this.floorNum) to floor num
		
		if (highestFloor) {
			// highest floors can't go up
			throw new IllegalStateException();
		}
		
		requestFloor = num;
		// turn on button lamp and press the button
		reqLampUp.setOn(true);
		reqBtnUp.setPressed(true);
		
		// send notification to scheduler
		this.notifier.sendMessage(new ElevatorMessage(ElevatorMessage.MessageType.ELEV_REQUEST, this.floorNum, 1));
	}
	
	public void reqDown(int num) {
		// num -> floor we want to go to
		// send request from floor (this.floorNum) to floor num
		if (lowestFloor) {
			// lowest floors can't go down
			throw new IllegalStateException();
		}
		
		requestFloor = num;
		// turn on button lamp and press the button
		reqLampDown.setOn(true);
		reqBtnDown.setPressed(true);
		
		
		this.notifier.sendMessage(new ElevatorMessage(ElevatorMessage.MessageType.ELEV_REQUEST, this.floorNum, 2));
	}
	
	public void resetDownBtn() {
		// unpresses button and turns off button lamp
		if(!lowestFloor) {
			reqLampDown.setOn(false);
			reqBtnDown.setPressed(false);
		}
	}
	
	public void resetUpBtn() {
		// unpresses button and turns off button lamp
		if(!highestFloor) {
			reqLampUp.setOn(false);
			reqBtnUp.setPressed(false);
		}
	}
	
	public void elevArrival(int direction) {
		System.out.println("FLOOR " + floorNum + " ELEVATOR ARRIVAL.");
		if (direction == 1) {
			resetUpBtn();
			directionLampUp.setOn(true);
			directionLampDown.setOn(false);
		}
		else {
			resetDownBtn();
			directionLampUp.setOn(false);
			directionLampDown.setOn(true);
		}
	}
	
	public void passengerEnter() {
		if(requestFloor == -1) {
			return;
		}
		this.notifier.sendMessage(new ElevatorMessage(ElevatorMessage.MessageType.PASSENGER_ENTER, this.floorNum, this.requestFloor));
		this.requestFloor = -1;
	}
}
