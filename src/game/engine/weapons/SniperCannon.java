package game.engine.weapons;

import java.util.PriorityQueue;

import game.engine.titans.Titan;

public class SniperCannon extends Weapon {
	
	public static final int WEAPON_CODE = 2;

	public SniperCannon(int baseDamage) {
		super(baseDamage);
	}
	
	public int turnAttack(PriorityQueue<Titan> laneTitans) {
		int resources = 0;
        Titan closestTitan = findClosestTitan(laneTitans);
        if (closestTitan != null) {
            resources += super.attack(closestTitan);
            if (closestTitan.isDefeated()) 
                laneTitans.remove(closestTitan);
            return resources;
        } 
        else 
            return resources;
    }
	
    private Titan findClosestTitan(PriorityQueue<Titan> laneTitans) {  // Helper function to find the closest Titan
        if (!laneTitans.isEmpty()) 
            return laneTitans.peek();  
        else 
            return null;
    }
}
