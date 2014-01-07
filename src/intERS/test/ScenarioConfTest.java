package intERS.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import intERS.conf.scenario.ScenarioConf;
import intERS.utils.Sort;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ScenarioConfTest {

	@Test
	public void scenarioConfTest() {
		ScenarioConf scenario = ScenarioConf
				.scenarioParser("conf/scenario.xml");

		assertNotNull(scenario);

		System.out.println(scenario.toString());
	}

	@Test
	public void sortTest() {
		Map<Integer, Double> map = new HashMap<Integer, Double>();

		map.put(10, new Double(100));
		map.put(11, new Double(100.1));
		map.put(43, new Double(1));
		map.put(1999, new Double(67));

		Map<Integer, Double> newMap = Sort.ascendentSortByValue(map);

		for (Integer value : newMap.keySet()) {
			System.out.println(map.get(value));
		}
	}

	@Test
	public void listTest() {
		List<Integer> l1 = new ArrayList<Integer>();
		l1.add(1);
		l1.add(2);
		l1.add(3);

		List<Integer> l2 = new ArrayList<Integer>();
		l2.addAll(l1);

		l1 = new ArrayList<Integer>();
		l1.add(4);
		l1.add(5);
		l1.add(3);

		l2.addAll(l1);

		for (int i = 0; i < l2.size(); i++) {
			System.out.println(i + " " + l2.get(i));
		}
		System.out.println();
	}
}