package com.org1.elevatorsystem.api;

import com.org1.elevatorsystem.MoveLocation;
import com.org1.elevatorsystem.impl.ElevatorState;

public interface ElevatorAPI extends Runnable{

	Integer getId();

	ElevatorState getElevatorState() throws CloneNotSupportedException;

	int getMaxFloorNumber();

	int getMinFloorNumber();

	void openDoor();

	void closeDoor();

	void addSubscriber(ElevatorStateSubscriber subscriber);
	boolean addLocation(MoveLocation loc);
	boolean addLocation(int floorNumber);
	boolean removeLocation(MoveLocation loc);

	ElevatorPanel getElevatorPanel();
	
	interface ElevatorPanel extends Runnable{

		boolean addFloor(int floorNumber);
		
	}
	
}

