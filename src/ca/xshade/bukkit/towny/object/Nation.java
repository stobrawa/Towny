package ca.xshade.bukkit.towny.object;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.xshade.bukkit.towny.AlreadyRegisteredException;
import ca.xshade.bukkit.towny.EmptyNationException;
import ca.xshade.bukkit.towny.IConomyException;
import ca.xshade.bukkit.towny.NotRegisteredException;
import ca.xshade.bukkit.towny.TownyException;
import ca.xshade.bukkit.towny.TownySettings;

public class Nation extends TownyIConomyObject implements ResidentList {
	private List<Resident> assistants = new ArrayList<Resident>();
	private List<Town> towns = new ArrayList<Town>();
	private List<Nation> allies = new ArrayList<Nation>();
	private List<Nation> enemies = new ArrayList<Nation>();
	private Town capital;
	private int taxes;
	private boolean neutral = false;

	public Nation(String name) {
		setName(name);
	}

	public void addAlly(Nation nation) throws AlreadyRegisteredException {
		if (hasAlly(nation))
			throw new AlreadyRegisteredException();
		else {
			try {
				removeEnemy(nation);
			} catch (NotRegisteredException e) {
			}
			getAllies().add(nation);
		}
	}

	public boolean removeAlly(Nation nation) throws NotRegisteredException {
		if (!hasAlly(nation))
			throw new NotRegisteredException();
		else
			return getAllies().remove(nation);
	}

	public boolean removeAllAllies() {
		for (Nation ally : new ArrayList<Nation>(getAllies()))
			try {
				removeAlly(ally);
				ally.removeAlly(this);
			} catch (NotRegisteredException e) {
			}
		return getAllies().size() == 0;
	}

	public boolean hasAlly(Nation nation) {
		return getAllies().contains(nation);
	}

	public void addEnemy(Nation nation) throws AlreadyRegisteredException {
		if (hasEnemy(nation))
			throw new AlreadyRegisteredException();
		else {
			try {
				removeAlly(nation);
			} catch (NotRegisteredException e) {
			}
			getEnemies().add(nation);
		}
			
	}

	public boolean removeEnemy(Nation nation) throws NotRegisteredException {
		if (!hasEnemy(nation))
			throw new NotRegisteredException();
		else
			return getEnemies().remove(nation);
	}

	public boolean removeAllEnemies() {
		for (Nation enemy : new ArrayList<Nation>(getEnemies()))
			try {
				removeEnemy(enemy);
				enemy.removeEnemy(this);
			} catch (NotRegisteredException e) {
			}
		return getAllies().size() == 0;
	}

	public boolean hasEnemy(Nation nation) {
		return getEnemies().contains(nation);
	}

	public List<Town> getTowns() {
		return towns;
	}

	public boolean isKing(Resident resident) {
		return hasCapital() ? getCapital().isMayor(resident) : false;
	}

	public boolean hasCapital() {
		return getCapital() != null;
	}

	public boolean hasAssistant(Resident resident) {
		return getAssistants().contains(resident);
	}

	public boolean isCapital(Town town) {
		return town == getCapital();
	}

	public boolean hasTown(String name) {
		for (Town town : towns)
			if (town.getName().equalsIgnoreCase(name))
				return true;
		return false;
	}

	public boolean hasTown(Town town) {
		return towns.contains(town);
	}

	public void addTown(Town town) throws AlreadyRegisteredException {
		if (hasTown(town))
			throw new AlreadyRegisteredException();
		else if (town.hasNation())
			throw new AlreadyRegisteredException();
		else {
			towns.add(town);
			town.setNation(this);
		}
	}

	public void addAssistant(Resident resident) throws AlreadyRegisteredException {
		if (hasAssistant(resident))
			throw new AlreadyRegisteredException();
		else
			getAssistants().add(resident);
	}
	
	public void removeAssistant(Resident resident) throws NotRegisteredException {
		if (!hasAssistant(resident))
			throw new NotRegisteredException();
		else
			assistants.remove(resident);
	}

	public void setCapital(Town capital) {
		this.capital = capital;
	}

	public Town getCapital() {
		return capital;
	}

	
	//TODO: Remove
	public boolean setAliegeance(String type, Nation nation) {
		try {
			if (type.equalsIgnoreCase("ally")) {
				removeEnemy(nation);
				addAlly(nation);
				if (!hasEnemy(nation) && hasAlly(nation))
					return true;
			} else if (type.equalsIgnoreCase("neutral")) {
				removeEnemy(nation);
				removeAlly(nation);
				if (!hasEnemy(nation) && !hasAlly(nation))
					return true;
			} else if (type.equalsIgnoreCase("enemy")) {
				removeAlly(nation);
				addEnemy(nation);
				if (hasEnemy(nation) && !hasAlly(nation))
					return true;
			}
		} catch (AlreadyRegisteredException x) {
			return false;
		} catch (NotRegisteredException e) {
			return false;
		}

		return false;
	}

	public void setAssistants(List<Resident> assistants) {
		this.assistants = assistants;
	}

	public List<Resident> getAssistants() {
		return assistants;
	}

	public void setEnemies(List<Nation> enemies) {
		this.enemies = enemies;
	}

	public List<Nation> getEnemies() {
		return enemies;
	}

	public void setAllies(List<Nation> allies) {
		this.allies = allies;
	}

	public List<Nation> getAllies() {
		return allies;
	}

	public int getNumTowns() {
		return towns.size();
	}
	
	public int getNumResidents() {
		int numResidents = 0;
		for (Town town : getTowns())
			numResidents += town.getNumResidents();
		return numResidents;
	}

	public void removeTown(Town town) throws EmptyNationException, NotRegisteredException {
		if (!hasTown(town))
			throw new NotRegisteredException();
		else {

			remove(town);
			
			if (getNumTowns() == 0) {
				clear();
				throw new EmptyNationException(this);
			}
		}
	}
	
	private void remove(Town town) {
		removeAssistantsIn(town);
		try {
			town.setNation(null);
		} catch (AlreadyRegisteredException e) {
		}
		towns.remove(town);
	}
	
	private void removeAllTowns() {
		for (Town town : towns)
			remove(town);
	}

	public boolean hasAssistantIn(Town town) {
		for (Resident resident : town.getResidents())
			if (hasAssistant(resident))
				return true;
		return false;
	}
	
	private void removeAssistantsIn(Town town) {
		for (Resident resident : new ArrayList<Resident>(town.getResidents()))
			if (hasAssistant(resident))
				try {
					removeAssistant(resident);
				} catch (NotRegisteredException e) {
				}
	}

	public void setTaxes(int taxes) {
		this.taxes = taxes;
	}

	public int getTaxes() {
		return taxes;
	}

	public void clear() {
		//TODO: Check cleanup
		removeAllAllies();
		removeAllEnemies();
		removeAllTowns();
		capital = null;
		assistants.clear();
	}

	public void setNeutral(boolean neutral) {
		this.neutral = neutral;
	}

	public boolean isNeutral() {
		return neutral;
	}
	
	public void setKing(Resident king) throws TownyException {
		if (!hasResident(king))
			throw new TownyException("New King doesn't belong to this nation.");
		if (!king.isMayor())
			throw new TownyException("New King is not a mayor.");
		setCapital(king.getTown());
	}
	
	public boolean hasResident(Resident resident) {
		for (Town town : getTowns())
			if (town.hasResident(resident))
				return true;
		return false;
	}
	
	public void withdrawFromBank(Resident resident, int amount) throws IConomyException, TownyException {
		if (!isKing(resident) && !hasAssistant(resident))
			throw new TownyException("You don't have access to the nation's bank.");
		
		if (TownySettings.isUsingIConomy()) {
			if (!pay(amount, resident))
				throw new TownyException("There is not enough money in the bank.");
		} else
			throw new TownyException("iConomy has not been turned on.");
	}
	
	@Override
	public List<Resident> getResidents() {
		List<Resident> out = new ArrayList<Resident>();
		for (Town town : getTowns())
			out.addAll(town.getResidents());
		return out;
	}
	
	@Override
	public List<String> getTreeString(int depth) {
		List<String> out = new ArrayList<String>();
		out.add(getTreeDepth(depth) + "Nation ("+getName()+")");
		out.add(getTreeDepth(depth+1) + "Capital: " + getCapital().getName());
		if (getAssistants().size() > 0)
			out.add(getTreeDepth(depth+1) + "Assistants (" + getAssistants().size() + "): " + Arrays.toString(getAssistants().toArray(new Resident[0])));
		if (getAllies().size() > 0)
			out.add(getTreeDepth(depth+1) + "Allies (" + getAllies().size() + "): " + Arrays.toString(getAllies().toArray(new Nation[0])));
		if (getEnemies().size() > 0)
			out.add(getTreeDepth(depth+1) + "Enemies (" + getEnemies().size() + "): " + Arrays.toString(getEnemies().toArray(new Nation[0])));
		out.add(getTreeDepth(depth+1) + "Towns (" + getTowns().size() + "):");
		for (Town town : getTowns())
			out.addAll(town.getTreeString(depth+2));
		return out;
	}

	@Override
	public boolean hasResident(String name) {
		for (Town town : getTowns())
			if (town.hasResident(name))
				return true;
		return false;
	}
}
