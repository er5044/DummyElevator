package com.org1.elevatorsystem.impl;

import java.util.Map;

import com.org1.elevatorsystem.MoveLocation;
import com.org1.elevatorsystem.api.ElevatorAPI;
import com.org1.elevatorsystem.api.ElevatorFinder;

public class NearestElevatorFinder implements ElevatorFinder{
	
	private Map<Integer,ElevatorAPI> elevators;
	public NearestElevatorFinder(Map<Integer,ElevatorAPI> elevators) {
		this.elevators = elevators;
	}

	public ElevatorAPI findElevatorForLocation(MoveLocation command) throws Exception{
		ElevatorAPI minDistanceElevator = null;
		Integer minDistance= null;
		for(ElevatorAPI e: elevators.values()) {
			int elevatorDistance = getFloorsFromLocation(command, e.getElevatorState().getLocation());
			if(minDistance == null) {
				minDistance = elevatorDistance;
				minDistanceElevator = e;
			}else {
				if(minDistance>elevatorDistance) {
					minDistance = elevatorDistance;
					minDistanceElevator = e;
				}
			}
			System.out.println("Min distance from location " + command.toString() +"of  Elevator " + e.getId()+ " is " + minDistance);
		
		}
		System.out.println("Returning elevator" + minDistanceElevator.getId() +" for command " + command.toString());
		return minDistanceElevator;
	}
	private int getFloorsFromLocation(MoveLocation command, MoveLocation location) {
		return Math.abs(command.getFloorNumber()-location.getFloorNumber());
	}

	public ElevatorAPI findElevator(int elevatorId) {
		return elevators.get(elevatorId);
	}
}
