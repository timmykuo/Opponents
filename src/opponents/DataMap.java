package opponents;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DataMap<T> {

	private HashMap<T, Set<T>> map;
	public enum Status {ALLIES, OPPONENTS, UNKNOWN};
	
	public DataMap() {
		map = new HashMap<T, Set<T>>();
	}
	
	//create the T and put it into the map
	public void create(T x) {
		validate(x);
		map.put(x, new HashSet<T>());
	}
	
	//auto-generated getter for map
	public HashMap<T, Set<T>> getMap() {
		return map;
	}
	
	//if the map already contains this T, throw an exception
	private void validate(T x) {
		if(x == null) {
			throw new IllegalArgumentException("X has to be initialized");
		}
		if(map.containsKey(x)) {
			throw new IllegalArgumentException("Input T is already created in the map");
		}
	}
	
	/**
	 * oppose sets two inputs as opponents if they aren't already allies
	 * @param x
	 * @param y
	 */
	public void oppose(T x, T y) {
		//if the map doesn't contain x and y, put it into the map
		addIfAbsent(x);
		addIfAbsent(y);
		
		//return an error if they are already on the same side
		if(opponents(x, y).equals(Status.ALLIES)) {
			throw new IllegalArgumentException("X and Y are already known to be on the same side");
		}
		else {
			//add y into x's opponents
			map.get(x).add(y);
			//add x into y's opponents
			map.get(y).add(x);
		}
	}
	
	private void addIfAbsent(T x) {
		if(!map.containsKey(x)) {
			create(x);
		}
	}
	
	/**
	 * opponents returns the side that x and y are on in time O(V) where V is the number of nodes in the map
	 * complexity = 3
	 * @param x
	 * @param y
	 * @return the side the two inputs are: opponents, allies, or unknown
	 */
	public Status opponents(T x, T y) {
		addIfAbsent(x);
		addIfAbsent(y);
		//add x's opponents into enemies set
		Set<T> enemies = new HashSet<T>();
		enemies.addAll(map.get(x));
		//allies and visited should be empty 
		Set<T> allies = new HashSet<T>();
		Set<T> visited = new HashSet<T>();
		
		while(!enemies.isEmpty()) {
			//if y is in the enemies queue, return that they are opponents
			if(enemies.contains(y)) {
				return Status.OPPONENTS;
			}
			T enemy = enemies.iterator().next();
			//add the head into the set of visited nodes
			visited.add(enemy);
			//add head's opponents into allies if not already visited
			addOpponents(map.get(enemy), visited, allies);
			if(searchAllies(y, enemies, allies, visited).equals(Status.ALLIES)) {
				return Status.ALLIES;
			}
			enemies.remove(enemy);
		}
	
		//return unknown if not found in either queue
		return Status.UNKNOWN;
	}

	/**
	 * searchAllies searches through the allies queue for y, and adds it to enemies if not already visited
	 * complexity = 2
	 * @param y is the node to be searched for
	 * @param enemies is the set to add opponents to if not already visited
	 * @param allies is the set to search through
	 * @param visited is the set to add visited nodes to
	 * @return returns allies if y is found, unknown otherwise
	 */
	private Status searchAllies(T y, Set<T> enemies, Set<T> allies, Set<T> visited) {
		if(allies.contains(y)) {
			return Status.ALLIES;
		}
		
		while(!allies.isEmpty()) {
			T ally = allies.iterator().next();
			//add ally into the set of visited nodes
			visited.add(ally);
			//add ally's opponents into enemies if not already visited
			addOpponents(map.get(ally), visited, enemies);
			allies.remove(ally);
			
		}
		
		return Status.UNKNOWN;
	}
	
	//add opponent from opponents set into enemies/allies if not in visited
	private Set<T> addOpponents(Set<T> opponents, Set<T> visited, Set<T> ninjas) {
		for(T opponent: opponents) {
			//if opponent not already visited, add it in
			if(!visited.contains(opponent)) {
				ninjas.add(opponent);
			}
		}
		//return the updated set
		return ninjas;
	}
	
	//public class used for testing private methods in DataMap
	public class TestHook {
		public Set<T> addOpponents(HashMap<T, Set<T>> map,Set<T> opponents, Set<T> visited, Set<T> ninjas) {
			//set up for testing
			DataMap.this.map = map;
			
			//run the test
			Set<T> addOpponents_test = DataMap.this.addOpponents(opponents, visited, ninjas);
			return addOpponents_test;
		}
		
		public DataMap.Status searchAllies(HashMap<T, Set<T>> map, T y, Set<T> enemies, Set<T> allies, Set<T> visited) {
			//set up the test
			DataMap.this.map = map;
			
			//run the test
			return DataMap.this.searchAllies(y, enemies, allies, visited);
		}
		
		public void addIfAbsent(HashMap<T, Set<T>> map, T x) {
			//set up
			DataMap.this.map = map;
			
			//run
			DataMap.this.addIfAbsent(x);
		}
		
		public void validate(HashMap<T, Set<T>> map, T x) {
			//set up
			DataMap.this.map = map;
			
			//run
			DataMap.this.validate(x);
		}
	}
}
