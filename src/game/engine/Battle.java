package game.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import game.engine.base.Wall;
import game.engine.dataloader.DataLoader;
import game.engine.exceptions.InsufficientResourcesException;
import game.engine.exceptions.InvalidLaneException;
import game.engine.lanes.Lane;
import game.engine.titans.Titan;
import game.engine.titans.TitanRegistry;
import game.engine.weapons.Weapon;
import game.engine.weapons.WeaponRegistry;
import game.engine.weapons.factory.WeaponFactory;

public class Battle {
	private static final int[][] PHASES_APPROACHING_TITANS =
	{
		{ 1, 1, 1, 2, 1, 3, 4 },
		{ 2, 2, 2, 1, 3, 3, 4 },
		{ 4, 4, 4, 4, 4, 4, 4 } 
	}; // order of the types of titans (codes) during each phase
	private static final int WALL_BASE_HEALTH = 10000;

	private int numberOfTurns;
	private int resourcesGathered;
	private BattlePhase battlePhase;
	private int numberOfTitansPerTurn; // initially equals to 1
	private int score; // Number of Enemies Killed
	private int titanSpawnDistance;
	private final WeaponFactory weaponFactory;
	private final HashMap<Integer, TitanRegistry> titansArchives;
	private final ArrayList<Titan> approachingTitans; // treated as a Queue
	private final PriorityQueue<Lane> lanes;
	private final ArrayList<Lane> originalLanes;

	public Battle(int numberOfTurns, int score, int titanSpawnDistance, int initialNumOfLanes,int initialResourcesPerLane) throws IOException {
		super();
		this.numberOfTurns = numberOfTurns;
		this.battlePhase = BattlePhase.EARLY;
		this.numberOfTitansPerTurn = 1;
		this.score = score;
		this.titanSpawnDistance = titanSpawnDistance;
		this.resourcesGathered = initialResourcesPerLane * initialNumOfLanes;
		this.weaponFactory = new WeaponFactory();
		this.titansArchives = DataLoader.readTitanRegistry();
		this.approachingTitans = new ArrayList<Titan>();
		this.lanes = new PriorityQueue<>();
		this.originalLanes = new ArrayList<>();
		this.initializeLanes(initialNumOfLanes);
	}

	public int getNumberOfTurns() {
		return numberOfTurns;
	}

	public void setNumberOfTurns(int numberOfTurns) {
		this.numberOfTurns = numberOfTurns;
	}

	public int getResourcesGathered() {
		return resourcesGathered;
	}

	public void setResourcesGathered(int resourcesGathered) {
		this.resourcesGathered = resourcesGathered;
	}

	public BattlePhase getBattlePhase() {
		return battlePhase;
	}

	public void setBattlePhase(BattlePhase battlePhase) {
		this.battlePhase = battlePhase;
	}

	public int getNumberOfTitansPerTurn() {
		return numberOfTitansPerTurn;
	}

	public void setNumberOfTitansPerTurn(int numberOfTitansPerTurn) {
		this.numberOfTitansPerTurn = numberOfTitansPerTurn;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getTitanSpawnDistance() {
		return titanSpawnDistance;
	}

	public void setTitanSpawnDistance(int titanSpawnDistance) {
		this.titanSpawnDistance = titanSpawnDistance;
	}

	public WeaponFactory getWeaponFactory() {
		return weaponFactory;
	}

	public HashMap<Integer, TitanRegistry> getTitansArchives() {
		return titansArchives;
	}

	public ArrayList<Titan> getApproachingTitans() {
		return approachingTitans;
	}

	public PriorityQueue<Lane> getLanes() {
		return lanes;
	}

	public ArrayList<Lane> getOriginalLanes() {
		return originalLanes;
	}

	private void initializeLanes(int numOfLanes) {
		for (int i = 0; i < numOfLanes; i++) {
			Wall w = new Wall(WALL_BASE_HEALTH);
			Lane l = new Lane(w);

			this.getOriginalLanes().add(l);
			this.getLanes().add(l);
		}
	}
	public void refillApproachingTitans() {
		int[] titansToSpawn = PHASES_APPROACHING_TITANS[battlePhase.ordinal()];
	    for (int titanCode : titansToSpawn) {
	        TitanRegistry registry = titansArchives.get(titanCode);
	        if (registry != null) {
	            Titan newTitan = registry.spawnTitan(titanSpawnDistance);
	            approachingTitans.add(newTitan);
	        } 
	    }
	}
	
	public void purchaseWeapon(int weaponCode, Lane lane) throws InsufficientResourcesException, InvalidLaneException {
	    if (!lanes.contains(lane))
	        throw new InvalidLaneException();
	        
	    if (lane.isLaneLost()) 
	        throw new InvalidLaneException();
	        
	    WeaponRegistry weaponRegistry = weaponFactory.getWeaponShop().get(weaponCode);
	    
	    if (weaponRegistry == null) 
	        throw new IllegalArgumentException("Invalid weapon code");

	    if (weaponRegistry.getPrice() > resourcesGathered) 
	        throw new InsufficientResourcesException(resourcesGathered);
	    
	    Weapon weapon = weaponRegistry.buildWeapon();
	    lane.addWeapon(weapon); 
	    resourcesGathered -= weaponRegistry.getPrice(); 
	    performTurn();
	}

	
	public void passTurn (){ 
		performTurn();
	}
	
	private void addTurnTitansToLane() {
		Lane lane = lanes.poll();
		PriorityQueue<Lane> temp = new PriorityQueue<>();
		while(!lanes.isEmpty()) {
			if(lanes.peek().getDangerLevel()<lane.getDangerLevel()) {
				lane=lanes.poll();
				temp.add(lane);
			}
			else 
				temp.add(lanes.poll());	
		}
		while (!temp.isEmpty()) {
			if(lane!=temp.peek())
				lanes.add(temp.poll());
		}
		for(int i = 0; i < numberOfTitansPerTurn; i++) {
			if(approachingTitans.isEmpty())
				refillApproachingTitans();
			lane.addTitan(approachingTitans.remove(0));
		}
		lanes.add(lane);
	}

	private void moveTitans() {
		PriorityQueue <Lane> temp = new PriorityQueue <Lane>();
		while (!lanes.isEmpty()) {
			Lane l = lanes.poll();
			l.moveLaneTitans();
			temp.add(l);
		}
		while(!temp.isEmpty()) 
			lanes.add(temp.poll());
	}
	
	private int performWeaponsAttacks() {
		int resourcesCollected = 0;
		if(!isGameOver()) {
			PriorityQueue<Lane>temp=new PriorityQueue<Lane>();
			while(!lanes.isEmpty()) {
				Lane lane=lanes.poll();
				resourcesCollected += lane.performLaneWeaponsAttacks();
				temp.add(lane);
			}
			while(!temp.isEmpty()){
				lanes.add(temp.poll());
			}
		}
		score += resourcesCollected;
		resourcesGathered+=resourcesCollected;
		return resourcesCollected;
	}
	
	private int performTitansAttacks() { 
		int resourcesLost = 0;
		PriorityQueue<Lane> temp=new PriorityQueue<Lane>();
		while(!lanes.isEmpty()) {
			Lane lane=lanes.poll();
			resourcesLost += lane.performLaneTitansAttacks();
			if(!lane.isLaneLost()) {
				temp.add(lane);
			}
		}
		while(!temp.isEmpty()) {
			lanes.add(temp.poll());
		}
		return resourcesLost;
	}
	
	private void updateLanesDangerLevels() {
		PriorityQueue <Lane> l = new PriorityQueue<Lane>();
		while(!lanes.isEmpty()) {
			Lane lane=lanes.poll();
			lane.updateLaneDangerLevel();
			if (!lane.isLaneLost())
				l.add(lane);
		}
		while(!l.isEmpty())
			lanes.add(l.poll());
	}
	
	private void finalizeTurns() {
		this.numberOfTurns++;
		if(this.numberOfTurns < 15) {
			this.battlePhase = BattlePhase.EARLY;
		}
		else if(this.numberOfTurns < 30) {
			this.battlePhase = BattlePhase.INTENSE;
		}	
		else if(this.numberOfTurns >= 30) {
			if(this.numberOfTurns > 30 && this.numberOfTurns % 5 == 0) {
				this.numberOfTitansPerTurn = this.numberOfTitansPerTurn*2;
			}		
			this.battlePhase = BattlePhase.GRUMBLING;
		}
	}
	
	private void performTurn() {
			moveTitans();
			performWeaponsAttacks();
		    performTitansAttacks();
		    addTurnTitansToLane();
		    finalizeTurns();
		    updateLanesDangerLevels();
	}
	
	public boolean isGameOver() {
		return lanes.isEmpty();
	}
}

