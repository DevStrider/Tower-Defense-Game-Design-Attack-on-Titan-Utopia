package game.engine.weapons;

import java.util.PriorityQueue;

import game.engine.titans.Titan;

public class WallTrap extends Weapon {
	
	public static final int WEAPON_CODE = 4;

	public WallTrap(int baseDamage) {
		super(baseDamage);
	}
	public int turnAttack(PriorityQueue<Titan> laneTitans) {
		int resources = 0;
		if(!laneTitans.isEmpty()&&laneTitans.peek().hasReachedTarget()) {
			resources+=super.attack(laneTitans.peek());
			if(laneTitans.peek().isDefeated())
				laneTitans.poll();
		}
		return resources;
	}	

}
