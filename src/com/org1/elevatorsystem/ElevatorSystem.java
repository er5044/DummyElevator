package com.org1.elevatorsystem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.org1.elevatorsystem.MoveLocation.Direction;
import com.org1.elevatorsystem.api.ElevatorAPI;
import com.org1.elevatorsystem.api.ElevatorAPI.ElevatorPanel;
import com.org1.elevatorsystem.api.ElevatorAssigner;
import com.org1.elevatorsystem.api.ElevatorFinder;
import com.org1.elevatorsystem.api.ElevatorStatusDisplay;
import com.org1.elevatorsystem.api.LocationAddChecker;
import com.org1.elevatorsystem.impl.DefaultElevatorAssigner;
import com.org1.elevatorsystem.impl.EachFloorLocationAddChecker;
import com.org1.elevatorsystem.impl.Elevator;
import com.org1.elevatorsystem.impl.ElevatorState;
import com.org1.elevatorsystem.impl.ElevatorState.State;
import com.org1.elevatorsystem.impl.EvenFloorLocationAddChecker;
import com.org1.elevatorsystem.impl.FloorPanel;
import com.org1.elevatorsystem.impl.NearestElevatorFinder;
import com.org1.elevatorsystem.impl.OddFloorLocationAddChecker;

public class ElevatorSystem {

	private Properties configuration;
	private ExecutorService executors =null;
	FloorPanel panel;
	Map<Integer,ElevatorAPI.ElevatorPanel> elevatorPanels;

	public Map<Integer, ElevatorAPI.ElevatorPanel> getElevatorPanels() {
		return elevatorPanels;
	}

	public ElevatorSystem(Properties properties) {
		this.configuration=properties;
		init();
	}

	private void init() {
		if(this.configuration.get("elevators") != null) {
			
			Integer elevators = Integer.parseInt((String)configuration.get("elevators"));
			String  assignPolicy = (String)configuration.get("assignPolicy");
			String elevatorType = (String)configuration.get("elevatorType");
			Integer minFloorNumber = Integer.parseInt((String)configuration.get("minFloorNumber"));
			Integer maxFloorNumber = Integer.parseInt((String)configuration.get("maxFloorNumber"));
			String locationStrategy = (String)configuration.get("locationStrategy");
			Map<Integer,ElevatorAPI> elevatorAPIs = ComponentCreator.getElevators(elevators, elevatorType, minFloorNumber, minFloorNumber, maxFloorNumber, locationStrategy);
			elevatorPanels = ComponentCreator.getElevatorPanels(elevatorAPIs.values());
			ElevatorFinder finder = ComponentCreator.getElevatorFinder(assignPolicy, elevatorAPIs);
			ElevatorAssigner assigner = ComponentCreator.getAssigner(finder);
			ElevatorStatusDisplay display = ComponentCreator.getStausDisplay();
			panel = ComponentCreator.getFloorPanel(assigner);
			initElevators(elevatorAPIs, display,assigner);
			executors = Executors.newFixedThreadPool(elevators+elevators+2);
			startComponents(executors,elevatorAPIs,assigner,display,elevatorPanels);
		}
		
	}
	
	public FloorPanel getPanel() {
		return panel;
	}

	private void startComponents(ExecutorService executors2, Map<Integer, ElevatorAPI> elevatorAPIs,
			ElevatorAssigner assigner, ElevatorStatusDisplay display, Map<Integer, ElevatorPanel> elevatorPanels2) {
		executors2.submit(assigner);
		executors2.submit(display);
		for(ElevatorAPI elevator: elevatorAPIs.values()) {
			executors2.submit(elevator);	
		}
		for(ElevatorPanel panel : elevatorPanels2.values()) {
			executors2.submit(panel);
		}
	}

	private void initElevators(Map<Integer, ElevatorAPI> elevatorAPIs, ElevatorStatusDisplay display, ElevatorAssigner assigner) {
		for(ElevatorAPI elevator: elevatorAPIs.values()) {
			elevator.addSubscriber(display);
			elevator.addSubscriber(assigner);
			
		}
	}

	private static class ComponentCreator{
		public static Map<Integer,ElevatorAPI> getElevators(int numberOfElevators, String elevatorType, int floorNumber, int minFloorNumber, int maxFloorNumber, String locationStrategy){
			Map<Integer,ElevatorAPI> elevators = new HashMap<>();
			
			for(int i =1;i<=numberOfElevators;i++) {
				ElevatorState elevatorState = new ElevatorState(i);
				MoveLocation location = new MoveLocation();
				location.setFloorNumber(floorNumber);
				location.setDirection(Direction.UP);
				elevatorState.setElevatorState(State.WAIT);
				elevatorState.setLocation(location);
				LocationAddChecker checker = null;
				if(locationStrategy.equalsIgnoreCase("EachFloor")) {
					checker = new EachFloorLocationAddChecker();
				}else if(locationStrategy.equalsIgnoreCase("AlternateFloor")) {
					if(i %2==0) {
						checker = new EvenFloorLocationAddChecker();
					}else {
						checker = new OddFloorLocationAddChecker(); 
					}

				}
				ElevatorAPI api = new Elevator(i, elevatorState, maxFloorNumber, minFloorNumber, checker);
				elevators.put(i, api);
			}
			return elevators;
		}
		
		public static Map<Integer, ElevatorPanel> getElevatorPanels(Collection<ElevatorAPI> collection) {
			Map<Integer, ElevatorPanel> mapPanels = new HashMap<>();
			for(ElevatorAPI elevator: collection) {
				mapPanels.put(elevator.getId(), elevator.getElevatorPanel());
			}
			return mapPanels;
		}

		public static ElevatorFinder getElevatorFinder(String assignPolicy, Map<Integer, ElevatorAPI> elevators) {
			ElevatorFinder finder = null;
			if(assignPolicy.equals("Nearest")) {
				finder = new NearestElevatorFinder(elevators);
			}
			return finder;
		}
		public static ElevatorAssigner getAssigner(ElevatorFinder elevatorFinder) {
			ElevatorAssigner assigner = new DefaultElevatorAssigner(elevatorFinder);
			return assigner;
		}
		
		public static ElevatorStatusDisplay getStausDisplay() {
			ElevatorStatusDisplay display = new ElevatorStatusDisplay();
			return display;
		}
		public static FloorPanel getFloorPanel(ElevatorAssigner assigner) {
			FloorPanel panel = new FloorPanel(assigner);
			return panel;
		}
		
		
	}
}
