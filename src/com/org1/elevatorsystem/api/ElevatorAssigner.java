package com.org1.elevatorsystem.api;

import com.org1.elevatorsystem.MoveLocation.Direction;

public interface ElevatorAssigner  extends Runnable,ElevatorStateSubscriber{

	void assignElevator(int floorNumber, Direction direction) throws Exception;


}
