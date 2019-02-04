package Floor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import Elevator.ElevatorMessage;
import Scheduler.ElevatorListener;

// TODO: PUT ALL THESE IN ONE CLASS

public class NotificationListener{
	
	
	public static final int PORT = 42424;
	protected DatagramPacket recvPacket;
	protected DatagramSocket recvSocket;
	
	public NotificationListener() {
		try {
			recvSocket = new DatagramSocket(PORT);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public ElevatorMessage waitForNotification() {
		ElevatorMessage msg;
		byte[] data = new byte[ElevatorMessage.SIZE];
		
		recvPacket = new DatagramPacket(data, data.length);
		System.out.println("SCHEDULER LISTENER: Waiting for a notification...");
		try {
			recvSocket.receive(recvPacket);
			System.out.println("SCHEDULER LISTENER: RECEIVED NOTIFICATION");
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		msg = new ElevatorMessage(recvPacket.getData());
		return msg;
	}

}