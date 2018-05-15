package com.org1.elevatorsystem.api;

import com.org1.elevatorsystem.impl.ElevatorState;

public interface ElevatorStateSubscriber {

	void subscribe(ElevatorState elevatorState); 
}
