package game.engine.lanes;

import java.util.ArrayList;
import java.util.PriorityQueue;

import game.engine.base.Wall;
import game.engine.titans.Titan;
import game.engine.weapons.Weapon;

public class Lane implements Comparable<Lane> {
	private final Wall laneWall;
	private int dangerLevel;
	private final PriorityQueue<Titan> titans;
	private final ArrayList<Weapon> weapons;

	public Lane(Wall laneWall) {
		super();
		this.laneWall = laneWall;
		this.dangerLevel = 0;
		this.titans = new PriorityQueue<>();
		this.weapons = new ArrayList<>();
	}

	public Wall getLaneWall() {
		return this.laneWall;
	}

	public int getDangerLevel() {
		return this.dangerLevel;
	}

	public void setDangerLevel(int dangerLevel) {
		this.dangerLevel = dangerLevel;
	}

	public PriorityQueue<Titan> getTitans() {
		return this.titans;
	}

	public ArrayList<Weapon> getWeapons() {
		return this.weapons;
	}

	@Override
	public int compareTo(Lane o) {
		return this.dangerLevel - o.dangerLevel;
	}
	
	public void addTitan(Titan titan)  {
		getTitans().add( titan);
	}
	
	public void addWeapon(Weapon weapon){
		weapons.add(weapon);
	}
	
	public void moveLaneTitans(){
		PriorityQueue<Titan> temp = new PriorityQueue<Titan>();
		while(!getTitans().isEmpty()){
			Titan eldian = getTitans().poll();
			if(!eldian.hasReachedTarget())
				eldian.move();
			temp.add(eldian);
		}
		while(!temp.isEmpty()){
			getTitans().add(temp.poll());
		}
	}
	
	public int performLaneTitansAttacks() {
		int totalresources = 0 ;
		PriorityQueue <Titan> temp = new PriorityQueue<Titan>();
		if(!laneWall.isDefeated()) {
			while(!titans.isEmpty()) {
				Titan eldian = titans.poll();
				if (eldian.hasReachedTarget())
					totalresources +=eldian.attack(laneWall);
				temp.add(eldian);
			}
			titans.addAll(temp);
			temp.clear();
		}
		return totalresources;
	}
	
	public int performLaneWeaponsAttacks() {
		int totalResources = 0;
		for(int i = 0 ; i < weapons.size() ; i++ ){
			Weapon weapon = weapons.get(i);
			totalResources += weapon.turnAttack(titans);
		}
		return totalResources;
	}
	
	public boolean isLaneLost(){
		return laneWall.getCurrentHealth()<=0;
	}
	
	public void updateLaneDangerLevel() {
		int newdangerlevel = 0;
		PriorityQueue<Titan> temp=new PriorityQueue<Titan>();
		while(!titans.isEmpty()) {
			Titan t=titans.poll();
			newdangerlevel += t.getDangerLevel();
			temp.add(t);
		}
		while(!temp.isEmpty())
			titans.add(temp.poll());
		this.dangerLevel = newdangerlevel;
	}

}
