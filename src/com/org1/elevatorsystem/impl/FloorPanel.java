package com.org1.elevatorsystem.impl;

import com.org1.elevatorsystem.MoveLocation.Direction;
import com.org1.elevatorsystem.api.ElevatorAssigner;

public class FloorPanel {

	private ElevatorAssigner assigner;
	public FloorPanel(ElevatorAssigner assigner) {
		this.assigner = assigner;
	}
	
	public void requestElevator(int floor, Direction direction) {
		try {
			assigner.assignElevator(floor, direction);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
