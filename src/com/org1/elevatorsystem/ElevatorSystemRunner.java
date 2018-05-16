package com.org1.elevatorsystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import com.org1.elevatorsystem.MoveLocation.Direction;
import com.org1.elevatorsystem.api.ElevatorAPI;

public class ElevatorSystemRunner {
	ElevatorSystem system = null;
	public static void main(String[] args) throws InterruptedException {
		if (args.length > 0 && args[0] != null) {
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(args[0]));
				ElevatorSystemRunner runner = new ElevatorSystemRunner();
				runner.system = new ElevatorSystem(properties);
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							Thread.sleep(100L);
							runner.submitFloorPanelRequests(properties, runner.system);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
					}
				});
				t.start();
				for(Entry<Integer,ElevatorAPI.ElevatorPanel> entry: runner.system.elevatorPanels.entrySet()) {
					Thread t2 = new Thread(new Runnable() {
						
						@Override
						public void run() {
							try {
								Thread.sleep(130L);
								runner.submitElevatorPanelRequests(properties, entry.getValue(),entry.getKey());
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}						
						}
						
					});
					t2.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private  void submitFloorPanelRequests(Properties properties, ElevatorSystem system)
			throws InterruptedException {
		for(int i=0;i<50;i++) {
			Thread.sleep(2000);
			boolean isDown =true;
			if(isDown) {
				system.getPanel().requestElevator(randomWithRange(Integer.parseInt((String)properties.get("minFloorNumber")),Integer.parseInt((String)properties.get("maxFloorNumber"))), Direction.DOWN);
				isDown =false;
			}else {
				system.getPanel().requestElevator(i, Direction.UP);
				isDown =true;
			}
		}
	}
	
	private  void submitElevatorPanelRequests(Properties properties, ElevatorAPI.ElevatorPanel elevatorPanel, int elevatorId)
			throws InterruptedException {
		for(int i=0;i<50;i++) {
			Thread.sleep(2000);
			boolean isDown =true;
			if(isDown) {
				int floor = randomWithRange(Integer.parseInt((String)properties.get("minFloorNumber")),Integer.parseInt((String)properties.get("maxFloorNumber")));
				System.out.println("Floor add requested for elevator " + elevatorId+ " floor no. " + floor);
				elevatorPanel.addFloor(floor);
				isDown =false;
			}else {
				system.getPanel().requestElevator(i, Direction.UP);
				isDown =true;
			}
		}
	}
	
	private static int randomWithRange(int min, int max)
	{
	   int range = (max - min) + 1;     
	   return (int)(Math.random() * range) + min;
	}

}
