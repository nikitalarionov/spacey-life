package com.bazola.spaceylife.gamemodel;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.math.Rectangle;

public class Alien {

	private final Random random;
	
	private MapPoint position;
	
	private MapPointPair pointPair;
	
	private double angle;
	
	private int speed;
	
	public MoveState state;
	
	private Rectangle rectangle;
	
	private int overlapMoveDistance = 30;
	
	//bigger clusters require a bigger number
	private int minDistanceFromFlag = 100;
	
	public Alien(MapPoint position, Random random) {
		this.position = position;
		this.random = random;
		
		this.angle = 0;
		
		this.speed = 10;
		
		this.state = MoveState.RESTING;
	}
	
	public void setRectangle(Rectangle rectangle) {
		this.rectangle = rectangle;
	}
	
	public Rectangle getRectangle() {
		return this.rectangle;
	}
	
	public MapPoint getPosition() {
		return this.position;
	}
	
	public double getAngle() {
		return this.angle;
	}
	
	public void update(List<PlayerFlag> playerFlags, Map<MapPoint, Star>stars, List<Alien> playerAliens) {
		
		//update rectangle position
		this.rectangle.setPosition(this.position.x, this.position.y);
		
		//move to player flag first
		if (playerFlags.size() > 0) {
			PlayerFlag targetFlag = playerFlags.get(playerFlags.size() - 1);
			//move to the last flag
			if (this.calculateDistance(targetFlag.getPosition(), this.position) > this.minDistanceFromFlag) {
				this.setFlagDestination(targetFlag.getPosition());
			}
		}
		
		//move away if overlapping other alien
		for (Alien alien : playerAliens) {
			if (this.equals(alien)) {
				continue;
			}
			
			if (this.rectangle.overlaps(alien.getRectangle())) {

				int randomX = this.random.nextInt(this.overlapMoveDistance);
				int randomY = this.random.nextInt(this.overlapMoveDistance);
				//50% chance to move negative instead of positive
				if (this.random.nextBoolean()) {
					randomX *= -1;
				}
				if (this.random.nextBoolean()) {
					randomY *= -1;
				}
				
				this.setOverlapDestination(new MapPoint(this.position.x + randomX,
														this.position.y + randomY));
			}
		}
		
		//this should always be the last part of the update phase
		this.move();
	}
	
	private double calculateDistance(MapPoint destination, MapPoint origin) {
		return Math.hypot(destination.x - origin.x, destination.y - origin.y);
	}
	
	public void setFlagDestination(MapPoint destination) {
		this.pointPair = new MapPointPair(this.position, destination);
		this.state = MoveState.MOVING_TO_FLAG;
	}
	
	public void setOverlapDestination(MapPoint destination) {
		if (this.state != MoveState.RESTING) {
			return;
		}
		this.pointPair = new MapPointPair(this.position, destination);
		this.state = MoveState.MOVING_FOR_OVERLAP;
	}
	
	public void move() {
		
		if (this.pointPair == null) {
			this.state = MoveState.RESTING;
		
			//System.out.println("point pair null");
			
			return;
		}
		
		if (this.position.equals(this.pointPair.secondPoint)) {
			this.state = MoveState.RESTING;
			
			//System.out.println("point equals second point");
			
			return;
		}
		
		int deltaX = this.pointPair.secondPoint.x - this.position.x;
		int deltaY = this.pointPair.secondPoint.y - this.position.y;
		
		double goalDistance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
		if (goalDistance > this.speed) {
			
			//System.out.println("goal distance greater");
			
			double ratio = this.speed / goalDistance;
			double xMove = ratio * deltaX;
			double yMove = ratio * deltaY;
			
			MapPoint previousPosition = this.position;
			this.position = new MapPoint((int)(xMove + this.position.x), (int)(yMove + this.position.y));
			this.angle = this.getAngle(previousPosition, this.position);
		} else {
			
			//System.out.println("goal distance not greater");
			
			this.position = this.pointPair.secondPoint;
			this.state = MoveState.RESTING;
		}
	}
	
	private double getAngle(MapPoint origin, MapPoint destination) {
		double degree = Math.toDegrees(Math.atan2(destination.y - origin.y, 
                								  destination.x - origin.x));
		if (degree < 0) {
			degree += 360;
		}
		return degree;
	}
}
