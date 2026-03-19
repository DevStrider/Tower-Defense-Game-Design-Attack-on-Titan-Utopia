package game.engine.weapons;

import java.util.PriorityQueue;

import game.engine.titans.Titan;

public class PiercingCannon extends Weapon {
	
	public static final int WEAPON_CODE = 1;

	public PiercingCannon(int baseDamage) {
		super(baseDamage);
	}
	
	public int turnAttack(PriorityQueue<Titan> laneTitans) {
		PriorityQueue <Titan> temp = new PriorityQueue <Titan> ();
		int resources = 0;
		for (int i = 0; i < 5 && !laneTitans.isEmpty(); i++) {
			Titan target = laneTitans.poll();
			resources += super.attack(target);
		    if(!target.isDefeated()) {
		    	temp.add(target);
		    }
		}
		while(!temp.isEmpty())
			laneTitans.add(temp.poll());
		return resources;
	}
}
