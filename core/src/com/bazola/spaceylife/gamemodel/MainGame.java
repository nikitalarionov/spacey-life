package com.bazola.spaceylife.gamemodel;

import java.util.ArrayList;
import java.util.List;

import com.bazola.spaceylife.GameScreen;

public class MainGame {
	
	private final GameScreen gameScreen;
	
	public final UniverseGenerator universe;
	
	private final int worldWidth;
	private final int worldHeight;
	
	private Star playerHomeworld;
	private Star aiHomeworld;
	
	private List<Alien> playerAliens = new ArrayList<Alien>();
	
	private List<PlayerFlag> playerFlags = new ArrayList<PlayerFlag>();
	private int flagLimit = 3;
	
	//bigger clusters require a bigger number
	private int minDistanceFromFlag = 100;
	
	public MainGame(int width, int height, GameScreen gameScreen) {
		this.gameScreen = gameScreen;
		this.worldWidth = width;
		this.worldHeight = height;
		
		this.universe = new UniverseGenerator(width, height, this.gameScreen.random);
	
		this.setPlayerAndAIHomeworlds();
	}
	
	private void setPlayerAndAIHomeworlds() {
		//player in bottom corner
		int searchSize = this.worldWidth / 10;
		MapPoint playerSearchLocation = new MapPoint(searchSize, searchSize);
		this.playerHomeworld = this.findStarInRange(searchSize, playerSearchLocation);
		//increase the search size if a star is not found
		int count = 1;
		while(this.playerHomeworld == null) {
			this.playerHomeworld = this.findStarInRange(searchSize * count, playerSearchLocation);
			count++;
		}
		this.playerHomeworld.setPlayerControlled();
		
		//ai in top opposite corner
		MapPoint aiSearchLocation = new MapPoint(this.worldWidth - searchSize, this.worldHeight - searchSize);
		this.aiHomeworld = this.findStarInRange(searchSize, aiSearchLocation);
		count = 1;
		while (this.aiHomeworld == null) {
			this.aiHomeworld = this.findStarInRange(searchSize * count, aiSearchLocation);
			count++;
		}
		this.aiHomeworld.setAIControlled();
	}
	
	private Star findStarInRange(int searchSize, MapPoint playerSearchLocation) {
		for (MapPoint starPoint : this.universe.getStars().keySet()) {
			if (starPoint.x <= playerSearchLocation.x + searchSize &&
				starPoint.x >= playerSearchLocation.x - searchSize &&
				starPoint.y <= playerSearchLocation.y + searchSize &&
				starPoint.y >= playerSearchLocation.y - searchSize) {
				return this.universe.getStars().get(starPoint);
			}
		}
		return null;
	}
	
	public void setPlayerMarkedPoint(MapPoint point) {
		//create new flags up to the limit
		if (this.playerFlags.size() < this.flagLimit) {
			PlayerFlag flag = new PlayerFlag(point);
			this.playerFlags.add(flag);
			this.gameScreen.flagSpawned(flag);
		} else {
			//shuffle to last index
			PlayerFlag flag = this.playerFlags.remove(0);
			this.playerFlags.add(flag);
			flag.setPosition(point);
			this.gameScreen.spawnRadarRingsAtFlagPlace(flag);
		}
	}
	
	public void update() {
		for (Alien alien : this.playerAliens) {
			if (this.playerFlags.size() > 0) {
				PlayerFlag targetFlag = this.playerFlags.get(this.playerFlags.size() - 1);
				//move to the last flag
				if (this.calculateDistance(targetFlag.getPosition(), alien.getPosition()) > this.minDistanceFromFlag) {
					alien.setFlagDestination(targetFlag.getPosition());
				}
			}
			alien.move();
		}
	}
	
	public Star getPlayerHomeworld() {
		return this.playerHomeworld;
	}
	
	public Star getAIHomeworld() {
		return this.aiHomeworld;
	}
	
	public List<Alien> getPlayerAliens() {
		return this.playerAliens;
	}
	
	public void spawnAlien() {
		Alien alien = new Alien(this.playerHomeworld.getPosition());
		this.playerAliens.add(alien);
		this.gameScreen.alienSpawned(alien);
	}
	
	private double calculateDistance(MapPoint destination, MapPoint origin) {
		return Math.hypot(destination.x - origin.x, destination.y - origin.y);
	}
	
	/*
	private Star findClosestStar(Collection<Star> stars, Star originStar) {
		double smallestDistance = 1000000; //make sure that any distance will be less
		Star closestStar = null;
		for (Star star : stars) {
			if (!star.equals(originStar)) {
				double distance = this.calculateDistance(star.getPosition(), originStar.getPosition());
				if (distance < smallestDistance) {
					smallestDistance = distance;
					closestStar = star;
				}
			}
		}
		return closestStar;
	}
	*/
}
