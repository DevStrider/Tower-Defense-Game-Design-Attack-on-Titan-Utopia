package game.engine.weapons.factory;

import java.io.IOException;
import java.util.HashMap;

import game.engine.dataloader.DataLoader;
import game.engine.exceptions.InsufficientResourcesException;
import game.engine.weapons.Weapon;
import game.engine.weapons.WeaponRegistry;

public class WeaponFactory {
	
	private final HashMap<Integer, WeaponRegistry> weaponShop;

	public WeaponFactory() throws IOException {
		super();
		weaponShop = DataLoader.readWeaponRegistry();
	}

	public HashMap<Integer, WeaponRegistry> getWeaponShop() {
		return weaponShop;
	}
	
	public FactoryResponse buyWeapon(int resources, int weaponCode) throws InsufficientResourcesException {
		WeaponRegistry weapon = weaponShop.get(weaponCode);
		if(weapon.getPrice() > resources) 
			 throw new InsufficientResourcesException(resources);
		int remainingResources = resources - weapon.getPrice();
		Weapon purchase = weapon.buildWeapon();
		return new FactoryResponse(purchase , remainingResources);
	}

	public void addWeaponToShop(int code, int price) {
		WeaponRegistry newWeapon = new WeaponRegistry(code, price);
		weaponShop.put(code, newWeapon);
	}
	
	public void addWeaponToShop(int code, int price, int damage, String name) {
		WeaponRegistry newWeapon = new WeaponRegistry(code, price, damage, name);
		weaponShop.put(code, newWeapon);
	}
	
	public void addWeaponToShop(int code, int price, int damage, String name, int minRange, int maxRange) {
		WeaponRegistry newWeapon = new WeaponRegistry(code, price, damage, name, minRange, maxRange);
		weaponShop.put(code, newWeapon);
	}
}
