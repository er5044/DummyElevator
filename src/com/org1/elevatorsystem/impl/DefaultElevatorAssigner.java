package com.org1.elevatorsystem.impl;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.org1.elevatorsystem.MoveLocation;
import com.org1.elevatorsystem.MoveLocation.Direction;
import com.org1.elevatorsystem.api.ElevatorAPI;
import com.org1.elevatorsystem.api.ElevatorAssigner;
import com.org1.elevatorsystem.api.ElevatorFinder;

public class DefaultElevatorAssigner implements ElevatorAssigner{

	private ElevatorFinder elevatorFinder;
	private BlockingQueue<MoveLocation> locationQueue;
	private boolean interupted;
	
	public DefaultElevatorAssigner(ElevatorFinder elevatorFinder) {
		this.elevatorFinder = elevatorFinder;
		locationQueue = new LinkedBlockingDeque<>();
		interupted = false;
	}

	private void assignElevator(MoveLocation command) {
		try {
		ElevatorAPI elevator = elevatorFinder.findElevatorForLocation(command);
		elevator.addLocation(command);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	


	@Override
	public void subscribe(ElevatorState elevatorState) {

		if(elevatorState.isBroken()) {
			List<MoveLocation> locations = elevatorState.getPendingLocations();
			for(MoveLocation location: locations) {
				assignElevator(location);
			}
		}else {
			//System.out.println("Elevator " + elevatorState);
		}
	}

	@Override
	public void assignElevator(int floorNumber, Direction direction) throws Exception {
		MoveLocation loc = createLocationFromFloor(floorNumber);
		loc.setDirection(direction);
		loc.setExternal(true);
		addLocation(loc);
		return ;
	}

	
	private void addLocation(MoveLocation location) {
		locationQueue.add(location);
	}

	private MoveLocation createLocationFromFloor(int floorNumber) {
		MoveLocation loc = new MoveLocation();
		loc.setFloorNumber(floorNumber);
		return loc;
	}


	@Override
	public void run() {
		while(!interupted) {
			try {
				MoveLocation location =  locationQueue.take();
				assignElevator(location);
			} catch (InterruptedException e) {
				interupted = true;
			}
			
		}
		
	}

}
