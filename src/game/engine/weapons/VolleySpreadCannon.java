package game.engine.weapons;

import java.util.PriorityQueue;

import game.engine.titans.Titan;

public class VolleySpreadCannon extends Weapon {
	
	public static final int WEAPON_CODE = 3;

	private final int minRange;
	private final int maxRange;

	public VolleySpreadCannon(int baseDamage, int minRange, int maxRange) {
		super(baseDamage);
		this.minRange = minRange;
		this.maxRange = maxRange;
	}

	public int getMinRange() {
		return minRange;
	}

	public int getMaxRange() {
		return maxRange;
	}
	
	public int turnAttack(PriorityQueue<Titan> laneTitans) {
        int totalResources = 0; 
        PriorityQueue<Titan> temp = new PriorityQueue<Titan>();
        while (!laneTitans.isEmpty()) {
            Titan eldian = laneTitans.poll();
            if (eldian.getDistance() >= minRange && eldian.getDistance() <= maxRange) {
                totalResources += super.attack(eldian);
                if (!eldian.isDefeated())  
                	temp.add(eldian);
            } 
            else
                temp.add(eldian);
        }
        laneTitans.addAll(temp);
        temp.clear();
        return totalResources;
    }


}
