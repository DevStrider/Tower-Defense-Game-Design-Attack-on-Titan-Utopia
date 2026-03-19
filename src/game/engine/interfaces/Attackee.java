package game.engine.interfaces;

public interface Attackee {
	int getCurrentHealth();
	void setCurrentHealth(int health);
	int getResourcesValue();
	default boolean isDefeated(){
		return getCurrentHealth() <= 0;
	}
	
	default int takeDamage(int damage){
		if(getCurrentHealth() - damage <= 0)
			setCurrentHealth(0);
		else	
			setCurrentHealth(getCurrentHealth() - damage);
		if (getCurrentHealth() <= 0){
			return getResourcesValue();
		}
		return 0;
	}

}
