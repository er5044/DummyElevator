package com.org1.elevatorsystem.api;

import com.org1.elevatorsystem.MoveLocation;

public interface ElevatorFinder {

	public ElevatorAPI findElevatorForLocation(MoveLocation location) throws Exception;
	public ElevatorAPI findElevator(int elevatorId);
}
