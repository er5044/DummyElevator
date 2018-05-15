package com.org1.elevatorsystem.impl;

import java.util.List;

import com.org1.elevatorsystem.MoveLocation;

public class ElevatorState implements Cloneable {

	private int elevatorNumber;
	public int getElevatorNumber() {
		return elevatorNumber;
	}
	private MoveLocation location;
	private State elevatorState;
	private List<MoveLocation> pendingLocations;
	
	public ElevatorState(int elevatorNumber) {
		this.elevatorNumber = elevatorNumber;
	}
	public MoveLocation getLocation() {
		return location;
	}
	public void setLocation(MoveLocation location) {
		this.location = location;
	}
	public State getElevatorState() {
		return elevatorState;
	}
	public List<MoveLocation> getPendingLocations() {
		return pendingLocations;
	}
	public void setPendingLocations(List<MoveLocation> pendingLocations) {
		this.pendingLocations = pendingLocations;
	}
	public void setElevatorState(State elevatorState) {
		this.elevatorState = elevatorState;
	}
	public boolean isIdle() {
		return elevatorState== State.WAIT;
	}

	public boolean isBroken() {
		return elevatorState== State.BROKEN;
	}
	
	public ElevatorState clone()throws CloneNotSupportedException{  
		ElevatorState state = new ElevatorState(this.elevatorNumber);
		state.setElevatorState(this.getElevatorState());
		MoveLocation moveLocation = new MoveLocation();
		moveLocation.setDirection(this.location.getDirection());
		moveLocation.setExternal(this.location.isExternal());
		moveLocation.setFloorNumber(this.location.getFloorNumber());
		state.setLocation(moveLocation);
		return state;  
	}  

	public	enum State{
		MOVING,WAIT, BROKEN,STOPPED ,DOOROPEN
	}

	public void setBrokenState() {
		elevatorState =State.BROKEN;
		
	}
	public void setMovingState() {
		elevatorState =State.MOVING;		
	}
	public void setWaitState() {
		elevatorState =State.WAIT;		
	}	
}
