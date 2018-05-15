package com.org1.elevatorsystem.impl;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.StampedLock;

import com.org1.elevatorsystem.MoveLocation;
import com.org1.elevatorsystem.MoveLocation.Direction;
import com.org1.elevatorsystem.api.ElevatorAPI;
import com.org1.elevatorsystem.api.ElevatorStateSubscriber;
import com.org1.elevatorsystem.api.LocationAddChecker;
import com.org1.elevatorsystem.impl.ElevatorState.State;

public class Elevator implements ElevatorAPI{

	private int id;
	private ElevatorState elevatorState;
	private int maxFloorNumber;
	private int minFloorNumber;
	private List<ElevatorStateSubscriber> stateSubscribers;
	private PriorityBlockingQueue<MoveLocation> locationQueue;
	private LocationAddChecker locationAddChecker;
	private StampedLock lock;
	private ElevatorPanel elevatorPanel;
	public Elevator(int i, ElevatorState elevatorState, int maxFloorNumber, int minFloorNumber,LocationAddChecker locationAddChecker) {
		this.id = i;
		this.elevatorState = elevatorState;
		this.maxFloorNumber = maxFloorNumber;
		this.minFloorNumber = minFloorNumber;
		this.locationAddChecker = locationAddChecker;
		locationQueue= new PriorityBlockingQueue<>();
		elevatorPanel = new ElevatorPanelImpl();
	}

	@Override
	public void run() {
		while(!elevatorState.isBroken()) {
			try {
				MoveLocation location = locationQueue.take();
				move(location);
			} catch (InterruptedException e) {
				System.out.println("Interrupted will continue if not broken");
				e.printStackTrace();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void move(MoveLocation location) throws InterruptedException, CloneNotSupportedException {
		changeDirectionIfRequired(location);
		elevatorState.setElevatorState(State.MOVING);
		MoveLocation currentLocation = elevatorState.getLocation(); 
		while(currentLocation.getFloorNumber() != location.getFloorNumber()) {
			Thread.sleep(5000);
			incrementDecrementFloorNumber();
			publishState();
		}
		handleFloorReached();
		
	}

	private void handleFloorReached() throws InterruptedException {
		openDoor();
		Thread.sleep(10000);
		closeDoor();
	}

	private void publishState() throws CloneNotSupportedException {
		for(ElevatorStateSubscriber subscriber: stateSubscribers) {
			subscriber.subscribe(getElevatorState());
		}
		
	}

	private void incrementDecrementFloorNumber() {
		if(elevatorState.getLocation().getDirection() == Direction.UP) {
			elevatorState.getLocation().setFloorNumber(elevatorState.getLocation().getFloorNumber()+1);
		}else if(elevatorState.getLocation().getDirection() == Direction.DOWN) {
			elevatorState.getLocation().setFloorNumber(elevatorState.getLocation().getFloorNumber()-1);
		}

	}

	private void changeDirectionIfRequired(MoveLocation location) {
		Direction d =null;
		if(elevatorState.getLocation().getFloorNumber()>location.getFloorNumber()) {
			d = Direction.UP;
		}else if(elevatorState.getLocation().getFloorNumber()>location.getFloorNumber()) {
			d = Direction.DOWN;
		}
		Lock wLock = lock.asWriteLock();
		wLock.lock();
			elevatorState.getLocation().setDirection(d);
		wLock.unlock();
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public ElevatorState getElevatorState() throws CloneNotSupportedException {
		return elevatorState.clone();
	}

	@Override
	public int getMaxFloorNumber() {
		return maxFloorNumber;
	}

	@Override
	public int getMinFloorNumber() {
		return minFloorNumber;
	}

	@Override
	public void openDoor() {
		System.out.println("Door Opened");
		elevatorState.setElevatorState(State.DOOROPEN);
	}

	@Override
	public void closeDoor() {
		System.out.println("Door Opened");
		elevatorState.setElevatorState(State.WAIT);		
	}

	@Override
	public void addSubscriber(ElevatorStateSubscriber subscriber) {
		stateSubscribers.add(subscriber);
	}


	@Override
	public boolean addLocation(MoveLocation loc) {
		boolean added = false;
		if(locationAddChecker.canAddLocation(loc)) {
			added =locationQueue.add(loc);
			
		}else {
			System.out.println("Location not permitted for elevator");
		}
		
		return added;
	}

	@Override
	public boolean addLocation(int floorNumber) {
		MoveLocation loc = new MoveLocation();
		loc.setFloorNumber(floorNumber);
		loc.setDirection(elevatorState.getLocation().getDirection());
		return addLocation(loc);
	}

	@Override
	public ElevatorPanel getElevatorPanel() {
		return elevatorPanel;
	}
	

	class ElevatorPanelImpl implements ElevatorPanel{
		private LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>(); 
		@Override
		public void run() {
			while(true) {
				try {
					int floorNumber = queue.take();
					boolean b = addLocation(floorNumber);
					if(!b) {
						System.out.println("Floor number "+floorNumber + " not added by elevator " + toString());
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public boolean addFloor(int floorNumber) {
			return queue.add(floorNumber);
		}
		
	}


	@Override
	public boolean removeLocation(MoveLocation loc) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "Elevator [id=" + id + ", elevatorState=" + elevatorState + ", maxFloorNumber=" + maxFloorNumber
				+ ", minFloorNumber=" + minFloorNumber + "]";
	}
	
}
