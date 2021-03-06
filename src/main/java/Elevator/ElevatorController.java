package Elevator;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import core.Constants;
import core.Constants.DIR;
import core.ElevatorMessage;
import core.EventListener;

public class ElevatorController {
	
	Elevator[] elevators = null;
	EventListener m_messageListener = null;
	
	
	//gui variables 
	JRadioButton openDoors [];
	JRadioButton closedDoors [];
	JRadioButton activeLamps [];
	JRadioButton motors [];
	JRadioButton UPLamps [];
	JRadioButton DOWNLamps [];
	JLabel currentFloorLabels [];
	
	public ElevatorController() {
		elevators = new Elevator[Constants.NUM_CARS];
	    for(int i=0; i<Constants.NUM_CARS; i++) {
	    	elevators[i] =  new Elevator(i);
	    }

	    m_messageListener = new EventListener(Constants.ELEV_PORT, "ELEVATOR CONTROLLER");
	}
	
	
	public ElevatorController(JRadioButton openDoors [], JRadioButton closedDoors [], JRadioButton activeLamps [], JRadioButton motors [], JRadioButton UPLamps [],
			JRadioButton DOWNLamps [], JLabel currentFloorLabels []) {
		
		
		this.openDoors = openDoors;
		this.closedDoors = closedDoors;
		this.activeLamps = activeLamps;
		this.motors = motors;
		this.UPLamps = UPLamps;
		this.DOWNLamps = DOWNLamps;
		this.currentFloorLabels = currentFloorLabels;
		
		elevators = new Elevator[Constants.NUM_CARS];
	    for(int i=0; i<Constants.NUM_CARS; i++) {
	    	elevators[i] =  new Elevator(i, this.activeLamps[i], this.motors[i], this.UPLamps[i], this.DOWNLamps[i], this.openDoors[i], this.closedDoors[i], this.currentFloorLabels[i]);
	    }

	    m_messageListener = new EventListener(Constants.ELEV_PORT, "ELEVATOR CONTROLLER");
	}
	
	public void startListen() {
			
			// starts a new thread/daemon that has a BLOCKING WAIT call, waits for a floor request from the scheduler
			System.out.println("ELEVATOR CONTROLLER: Starting message listener...");
			for(;;) {
				ElevatorMessage msg = m_messageListener.waitForNotification();
				switch(msg.getType()) {
				case REQ:
					System.out.println("\nELEVATOR CONTROLLER: RECEIVED ELEVATOR REQUEST NOTIFICATION" + msg);
					for(Elevator elev : elevators) {
						if (elev.dir == DIR.NONE) {
							elev.receiveRequest(msg.getId(), Constants.DIR.fromCode(msg.getData().get(0)));
							break;
						}
					}
					break;
				case FAULT:
					System.out.println("\nELEVATOR CONTROLLER: RECEIVED ELEVATOR FAULT" + msg);
					for(int i=0; i<Constants.NUM_CARS; i++) {
						if (elevators[i].dir == DIR.NONE) {
							elevators[i].receiveFault(msg.getId());
							break;
						}
					}
					break;
				default:
					break;
				}
			}
	}
	
	public void start() {
		ElevatorController s = this;
		
		// start listening for floor requests
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				s.startListen();
			}
		});
		
		t1.start();
	}
	
	public static void runElevator() {
		ElevatorController c = new ElevatorController();
		c.start();
	}
	
	public static void main(String[] args) {
		ElevatorController c = new ElevatorController();
		c.start();
	}
}
		
