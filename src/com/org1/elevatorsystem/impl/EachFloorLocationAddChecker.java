package com.org1.elevatorsystem.impl;

import com.org1.elevatorsystem.MoveLocation;
import com.org1.elevatorsystem.api.LocationAddChecker;

public class EachFloorLocationAddChecker implements LocationAddChecker{

	public boolean canAddLocation(MoveLocation location) {
		return true;
	}
}
