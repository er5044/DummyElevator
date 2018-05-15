package com.org1.elevatorsystem.api;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.org1.elevatorsystem.impl.ElevatorState;

public class ElevatorStatusDisplay implements ElevatorStateSubscriber, Runnable{
	
	private BlockingQueue<ElevatorState> stateQueue= new LinkedBlockingQueue<>();

	@Override
	public void subscribe(ElevatorState elevatorState) {
		stateQueue.add(elevatorState);
	
	}

	@Override
	public void run() {
		while(true) {
			ElevatorState elevatorState;
			try {
				elevatorState = stateQueue.take();
				System.out.println("Elevator "+ elevatorState.getElevatorNumber() +" reached floor " + elevatorState.getLocation().getFloorNumber()
						+" Direction " + elevatorState.getLocation().getDirection());
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			} 
			
		}
		
	}

	

}
