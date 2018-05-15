package com.org1.elevatorsystem;

public class MoveLocation implements Cloneable ,Comparable<MoveLocation>{

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + floorNumber;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MoveLocation other = (MoveLocation) obj;
		if (direction != other.direction)
			return false;
		if (floorNumber != other.floorNumber)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MoveLocation [isExternal=" + isExternal + ", direction=" + direction + ", floorNumber=" + floorNumber
				+ "]";
	}

	private boolean isExternal;
	
	public boolean isExternal() {
		return isExternal;
	}

	public void setExternal(boolean isExternal) {
		this.isExternal = isExternal;
	}

	public enum Direction{
		UP, DOWN
	}
	
	private Direction direction;
	
	private int floorNumber;

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public int getFloorNumber() {
		return floorNumber;
	}

	public void setFloorNumber(int floorNumber) {
		this.floorNumber = floorNumber;
	}

	@Override
	public int compareTo(MoveLocation o) {
			if(o == null) {
				return 0;
			}else if(o.direction == this.direction) {
				if(o.direction == Direction.UP) {
					if(o.floorNumber <this.floorNumber) {
						return 1;
					}else {
						return -1;
					}
				}else  {
					if(o.floorNumber > this.floorNumber) {
						return -1;
					}else {
						return 1;
					}
				}
			}else {
				return -1;//Current direction is at top of priority heap.
			}
	}
	
	
	
}
