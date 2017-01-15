package opponents;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class DataMapTest {
	
	private DataMap<Ninja> map;
	private DataMap<Ninja>.TestHook sampleMap;
	Ninja n1;
	Ninja n2;
	Ninja n3;
	Ninja n4;
	Ninja n5;
	Ninja n6;
	Ninja n7;
	Ninja n8;
	Ninja n9;
	Ninja n10;
	Ninja n11;
	
	@Before
	public void initialize() {
		map = new DataMap<Ninja>();
		sampleMap = map.new TestHook();
		
		n1 = new Ninja("Iam1");
		n2 = new Ninja("Iam2");
		n3 = new Ninja("Iam3");
		n4 = new Ninja("Iam4");
		n5 = new Ninja("Iam5");
		n6 = new Ninja("Iam6");
		n7 = new Ninja("Iam7");
	    n8 = new Ninja("Iam8");
		n9 = new Ninja("Iam9");
		n10 = new Ninja("Iam10");
		
		map.create(n1);
		map.create(n2);
		map.create(n3);
		map.create(n4);
		map.create(n5);
		map.create(n6);
		map.create(n7);
		map.create(n8);
		map.create(n9);
		
		map.oppose(n1, n2);
		map.oppose(n2, n3);
		map.oppose(n3, n4);
		map.oppose(n4, n5);
		map.oppose(n5, n6);
		map.oppose(n6, n7);
		map.oppose(n4, n7);
		map.oppose(n3, n6);
		map.oppose(n7, n8);
	}
	
	@Test
	public void testConstructor() {
		/*Structured basis: create the map*/
		DataMap<Ninja> test = new DataMap<Ninja>();
		assertEquals("The size of a new map should be 0", test.getMap().size(), 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValidate() {
		/*Structured basis: 1st is false and 2nd condition is true*/
		sampleMap.validate(map.getMap(), n1);

		/*Structured basis: 1st is false and 2nd condition is false
		 * Good data: expected values*/
		sampleMap.validate(map.getMap(), n10);
		
		/*Structured basis: 1st condition is true and 2nd is false
		/*Bad data: input not initialized*/
		sampleMap.validate(map.getMap(), n11);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreate() {
		Ninja test = new Ninja("test");
		/*Structured basis: all lines are run
		 * Good data: test is initialized and is not already in the map
		 */
		map.create(test);
		assertTrue("Create should add the object into the map if not already in there", map.getMap().containsKey(test));
		
		/*Bad Data: n11 is not initialized*/
		map.create(n11);
		assertFalse("Create should not add the object if an exception was thrown in validate", map.getMap().containsKey(n11));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddIfAbsent() {
		Ninja test = new Ninja("test");
		/*Structured basis: 1st condition is true
		 * Good data: x is a valid input
		 */
		sampleMap.addIfAbsent(map.getMap(), test);
		assertTrue("addIfAbsent should add the ninja test because it isn't in the map", map.getMap().containsKey(test));
		/*Structured basis: first condition is false
		 * Good data: x is a valid input
		 */
		sampleMap.addIfAbsent(map.getMap(), n9);
		assertTrue("addIfAbsent should not add the ninja n9 because it's in the map already", map.getMap().containsKey(n9));
		
		/*Bad data: x is not initialized*/
		sampleMap.addIfAbsent(map.getMap(), n11);
	}

	@Test
	public void testAddOpponents() {
		Set<Ninja> visited = new HashSet<Ninja>();
		Set<Ninja> ninjas = new HashSet<Ninja>();
		/*Structured basis: nominal case
		 * Good data: expected, opponents has enemies*/
		sampleMap.addOpponents(map.getMap(), map.getMap().get(n1), visited, ninjas);
		assertEquals("All of n1's opponents should be added into ninjas", ninjas, map.getMap().get(n1));
		/*Structured basis: for condition is false, opponents has no next() 
		 * Good data: minimum, no opponents*/
		ninjas = new HashSet<Ninja>();
		sampleMap.addOpponents(map.getMap(), map.getMap().get(n9), visited, ninjas);
		assertEquals("The ninjas set should still be empty because n9 has no opponents", ninjas, map.getMap().get(n9));
		/*Structured basis: for condition true, if condition is false
		 * Good data: expected, opponents has enemies */
		
		//n3's direct opponents are n2, n4, n6 so it should add n2, n4 and n6
		sampleMap.addOpponents(map.getMap(), map.getMap().get(n3), visited, ninjas);
		
		Set<Ninja> tester = new HashSet<Ninja>();
		tester.add(n2);
		tester.add(n4);
		tester.add(n6);
		assertEquals("The ninjas set should have opponents not already visited", ninjas, tester);
		
		/*Structured basis: for condition true
		 * Boundary? Error Guessing: both conditions happen in the loop
		 */
		ninjas = new HashSet<Ninja>();
		visited.add(n4);
		//n5's direct opponents are n4 and n6
		sampleMap.addOpponents(map.getMap(), map.getMap().get(n5), visited, ninjas);
		
		//n4 already visited, so only n6 should be added to ninjas
		tester = new HashSet<Ninja>();
		tester.add(n6);
		assertEquals("The ninjas set should have opponetns not already visited", ninjas, tester);
		
		/*Bad Data: private method, so would only be called after all sets created. no uninitialized inputs*/
	}
	
	@Test
	public void testSearchAllies() {
		Set<Ninja> enemies = new HashSet<Ninja>();
		Set<Ninja> allies = new HashSet<Ninja>();
		allies.add(n1);
		allies.add(n3);
		Set<Ninja> visited = new HashSet<Ninja>();
		/*Structured basis: nominal case
		 * Good data: expected, allies is not empty
		 */
		assertEquals("If an ally in allies matches the input, it should return ALLIES", 
						DataMap.Status.ALLIES, 
						sampleMap.searchAllies(map.getMap(), n3, enemies, allies, visited));
		/*Structured basis: if case is false, while case is true
		 * Good data: expected, allies is not empty
		 */
		allies.add(n1);
		allies.add(n3);
		assertEquals("If input is not in allies, it should return UNKNOWN", 
						DataMap.Status.UNKNOWN, 
						sampleMap.searchAllies(map.getMap(), n5, enemies, allies, visited));
		assertEquals("Allies should be empty at the end of the search",
					allies.size(), 0);
		assertTrue("Enemies should contain all the enemies of n3", enemies.containsAll(map.getMap().get(n3)));
		assertTrue("Enemies should contain all the enemies of n1", enemies.containsAll(map.getMap().get(n1)));
		/*Structured basis: if case is false, while case is false (allies is empty)
		 * Good data: minimal case, allies is empty
		 */
		allies = new HashSet<Ninja>();
		assertEquals("If allies is empty, it should return UNKNOWN", 
						DataMap.Status.UNKNOWN,
						sampleMap.searchAllies(map.getMap(), n1, enemies, allies, visited));
		/*Bad data: not possible, because private method, input parameters checked for */
	}
	
	@Test
	public void testOpponents() {
		/*Structured basis: while loop case is false 
		 * Good data: minimum, enemies is empty
		 */
		assertEquals("If one input has no opponents, then the relationship should be unknown", DataMap.Status.UNKNOWN, map.opponents(n9, n1));
		/*Structured basis: while loop case is true, 1st if case is true, 2nd is false
		 * Good data: expected, enemies is not empty and y is in enemies
		 */
		assertEquals("If an input is found in the enemies queue, then the relationship should be opponents", DataMap.Status.OPPONENTS, map.opponents(n1, n8));
		/*Structured basis: while loop true, 1st if case false, 2nd if case false
		 * Good data: expected, enemies and allies not empty, y is unknown
		 */
		assertEquals("If an input is not found in any queue, then the relationship should be unknown", DataMap.Status.UNKNOWN, map.opponents(n1, n9));
		/*Structured basis: while loop true, 1st if case false, 2nd if case true
		 * Good data: expected, enemies and allies not empty, y is in allies
		 */
		assertEquals("If an input is found in the allies queue, then the relationship should be allies", DataMap.Status.ALLIES, map.opponents(n2, n8));
		/*Bad data: x and y not in the map, but taken care of by addIfAbsent*/
		Ninja test1 = new Ninja("test1");
		Ninja test2 = new Ninja("test2");
		assertEquals("If an input is not already in the map, then the relationship should be unknown", DataMap.Status.UNKNOWN, map.opponents(n1,  test1));
		assertEquals("Two new inputs should have an unknown relationship", DataMap.Status.UNKNOWN, map.opponents(test1, test2));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOppose() {
		/*Structured Basis: nominal case 
		 * Bad Data: if they are allies, it should throw an exception
		 */
		map.oppose(n2,  n8);
		/*Structured Basis: if case is false
		 * Good data: expected, two ninjas exist in the map
		 * Boundary: opponents returns unknown
		 */
		map.oppose(n1, n9);
		assertEquals("oppose should add the two inputs as opponents if relationship is unknown", 
						DataMap.Status.OPPONENTS, 
						map.opponents(n1, n9));
		/*Structured Basis: if case is false
		 * Good data: expected, two ninjas exist in the map
		 * Boundary: opponents returns enemies
		 */
		map.oppose(n1, n6);
		assertEquals("oppose should add the two inputs as opponents if already enemies", 
						DataMap.Status.OPPONENTS, 
						map.opponents(n1, n6));
		/*Bad Data: x and y do not exist */
		Ninja test1 = new Ninja("test1");
		Ninja test2 = new Ninja("test2");
		map.oppose(test1, test2);
		assertEquals("oppose should set the two inputs as opponents if they aren't in the map", 
						DataMap.Status.OPPONENTS, 
						map.opponents(test1, test2));
	}
	
	@Test
	public void testStress() {
		List<Ninja> array = new ArrayList<Ninja>();
		//add a bunch of ninjas
		for(int i = 1; i < 10000; i++) {
			Ninja ninja = new Ninja("I am " + i);
			array.add(ninja);
		}
		map.oppose(n8, array.get(4000));
		//connect some nodes
		for(int i = 500; i <= 6000; i++) {
			map.oppose(array.get(i), array.get(i+1));
		}
		//connect more nodes
		for(int i = 6000; i <= 8000; i++) {
			map.oppose(array.get(i), array.get(i+2));
		}
		//connect more nodes
		for(int i = 8000; i <= 9903; i++) {
			map.oppose(array.get(i), array.get(i+5));
		}
		assertEquals("Two opponetns should return opponents", DataMap.Status.OPPONENTS, map.opponents(n8, array.get(500)));
		assertEquals("Two allies should return allies", DataMap.Status.ALLIES, map.opponents(n1, array.get(9900)));
		assertEquals("Two inputs with unknown relationship should return unknown", DataMap.Status.UNKNOWN, map.opponents(n1, array.get(8003)));
	}
}
