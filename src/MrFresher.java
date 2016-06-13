import java.awt.ItemSelectable;
import java.awt.geom.GeneralPath;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class MrFresher {

	public static void main(String[] args) {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();

		attributes.add(new Attribute("Outlook", new ArrayList<String>(), 0));
		attributes.get(0).getOptions().add("sunny");
		attributes.get(0).getOptions().add("overcast");
		attributes.get(0).getOptions().add("rainy");

		attributes
				.add(new Attribute("Temperature", new ArrayList<String>(), 1));
		attributes.get(1).getOptions().add("hot");
		attributes.get(1).getOptions().add("mild");
		attributes.get(1).getOptions().add("cool");

		attributes.add(new Attribute("Humidity", new ArrayList<String>(), 2));
		attributes.get(2).getOptions().add("high");
		attributes.get(2).getOptions().add("normal");

		attributes.add(new Attribute("Windy", new ArrayList<String>(), 3));
		attributes.get(3).getOptions().add("true");
		attributes.get(3).getOptions().add("false");

		attributes.add(new Attribute("Play", new ArrayList<String>(), 4));
		attributes.get(4).getOptions().add("yes");
		attributes.get(4).getOptions().add("no");

		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		String[] source = { "sunny,hot,high,FALSE,no",
				"sunny,hot,high,TRUE,no", "overcast,hot,high,FALSE,yes",
				"rainy,mild,high,FALSE,yes", "rainy,cool,normal,FALSE,yes",
				"rainy,cool,normal,TRUE,no", "overcast,cool,normal,TRUE,yes",
				"sunny,mild,high,FALSE,no", "sunny,cool,normal,FALSE,yes",
				"rainy,mild,normal,FALSE,yes", "sunny,mild,normal,TRUE,yes",
				"overcast,mild,high,TRUE,yes", "overcast,hot,normal,FALSE,yes",
				"rainy,mild,high,TRUE,no" };

		for (String s : source) {
			String[] p = s.toLowerCase().split(",");
			ArrayList<String> x = new ArrayList<String>();
			x.add(p[0]);
			x.add(p[1]);
			x.add(p[2]);
			x.add(p[3]);
			x.add(p[4]);
			data.add(x);
		}

		// reading done
		ArrayList<String> itemset = new ArrayList<String>();
		for (Attribute attribute : attributes) {
			for (String a : attribute.getOptions()) {
				itemset.add(a);
			}
		}

		Combiner c = new Combiner();
		ArrayList<ArrayList<Integer>> x = c.combine(itemset.size(), 4);

		int iteration = 1;
		Hashtable<ArrayList<CounterCapsule>, Integer> checkSet; // This is what
																// we will run
																// to get
																// frequency
		// List of (How many combo of ( Element ) )

		Hashtable<String, CounterCapsule> elements = new Hashtable<String, CounterCapsule>();
		for (Attribute attribute : attributes) {
			for (String o : attribute.getOptions()) {
				elements.put(attribute.getIndex() + "," + o,
						new CounterCapsule(attribute.getIndex(), o, attribute)); // {name,index},{counter}
			}
		}

		boolean askInput = false;
		Integer minFrequency = 0;
		boolean useProbs = false;
		Double prob = 0d;
		if (askInput) {
			String userResponse = readInput("Use Probability? (Y/N) : ");
			if (userResponse.compareTo("Y") == 0
					|| userResponse.compareTo("y") == 0) {
				useProbs = true;
				userResponse = readInput("Probability (0 - 1) : ");
				prob = Double.parseDouble(userResponse);
			} else {
				useProbs = false;
				userResponse = readInput("Minimum Frequency : ");
				minFrequency = Integer.parseInt(userResponse);
			}
		} else {
			// no wasting time
			useProbs = false;
			prob = 0.3;
			minFrequency = 6;
		}

		if (useProbs) {
			minFrequency = (int) Math.floor(data.size() * prob);
		}
		System.out.println("Minimum Frequency is " + minFrequency);

		double start = (new Date()).getTime();
		// Work starts here
		checkSet = Generate(iteration, elements); // takes in Hashtable<String,
													// CounterCapsule>
		checkSet = CountFrequency(checkSet, data); // the updated checkset with
													// frequency counts
		ArrayList<CounterCapsule> singleList = new ArrayList<CounterCapsule>();
		ArrayList<ArrayList<CounterCapsule>> filteredSet = EliminateBadOnes(
				checkSet, minFrequency, singleList, iteration);
		
		do {
			System.out.println("L" + iteration + " = " + filteredSet.size());
			checkSet = Generate(++iteration, filteredSet, singleList, false);
			if (checkSet.size()==0 ) {System.out.println("No generation"); break; }
			checkSet = CountFrequency(checkSet, data); // the updated checkset with
			// frequency counts
			singleList = new ArrayList<CounterCapsule>();
			filteredSet = EliminateBadOnes(
					checkSet, minFrequency, singleList, iteration);
			if (filteredSet.size()==0 ) {System.out.println("No filtered"); break;}
			
		} while(true);
		// Done
		double stop = (new Date()).getTime();
		System.out.println("Execution Time was " + (stop - start));

	}

	private static Hashtable<ArrayList<CounterCapsule>, Integer> Generate(
			int itemPerSet, ArrayList<ArrayList<CounterCapsule>> source,
			ArrayList<CounterCapsule> singleList, boolean debug) {

		Hashtable<ArrayList<CounterCapsule>, Integer> bruteList = Generate(
				itemPerSet, Capsulate(singleList));
		if (debug)  PrintHastable(bruteList);

		ArrayList<ArrayList<CounterCapsule>> removables = new ArrayList<ArrayList<CounterCapsule>>();
		// Eliminate sets with CC from same column
		for (ArrayList<CounterCapsule> lcc : bruteList.keySet()) {
			boolean isValid = true;
			int lastColumn = -1;
			if (hasDuplicate(lcc)) {
				removables.add(lcc);
				continue;
			}
		}
		// remove the duplicate column ones
		for (ArrayList<CounterCapsule> arrayList : removables) {
			bruteList.remove(arrayList);
		}
		if (debug)  PrintHastable(bruteList);

		// go through and remove impossible n-1 combinations
		int oneLessItem = itemPerSet - 1;
		if (debug) System.out.println(oneLessItem);
		Hashtable<ArrayList<CounterCapsule>, Integer> o = new Hashtable<ArrayList<CounterCapsule>, Integer>();
		for (ArrayList<CounterCapsule> a0 : bruteList.keySet()) {
			Hashtable<ArrayList<CounterCapsule>, Integer> splitList = Generate(
					oneLessItem, Capsulate(a0));
			
			boolean isValid = true;
			if (debug) System.out.println(a0);
			if (debug) System.out.println("\t" +source);
						
			for (ArrayList<CounterCapsule> cc : splitList.keySet()) {
				boolean isValid1 = false;
				for (ArrayList<CounterCapsule> scc : source) {
					isValid1 = scc.containsAll(cc) || isValid1; // atleast one of them must contain
				}
				isValid = isValid1 && isValid;
			}			
			if (isValid) {
				o.put(a0, 0);
			}
		}

		return o;
	}

	public static Hashtable<String, CounterCapsule> Capsulate(
			ArrayList<CounterCapsule> source) {
		Hashtable<String, CounterCapsule> o = new Hashtable<String, CounterCapsule>();
		for (CounterCapsule cc : source) {
			o.put(cc.getNiceName(), cc);
		}
		return o;

	}

	public static boolean hasDuplicate(ArrayList<CounterCapsule> all) {
		Set<Integer> set = new HashSet<Integer>();
		// Set#add returns false if the set does not change, which
		// indicates that a duplicate element has been added.
		for (CounterCapsule each : all)
			if (!set.add(each.Index))
				return true;
		return false;
	}

	private static void PrintHastable(
			Hashtable<ArrayList<CounterCapsule>, Integer> source) {
		System.out
				.println("-----------------------------------------------------");
		for (ArrayList<CounterCapsule> cc : source.keySet()) {
			String keyName = "";
			// boolean hasDuplicates = hasDuplicate(cc);
			for (CounterCapsule c : cc) {
				keyName = keyName + " + " + c.Name;
			}
			keyName = keyName.substring(3);
			System.out.println(keyName);
			// System.out.println(keyName + "\t " + (hasDuplicates ?
			// "Duplicates" : "X"));
		}
		System.out.println("------------------------------------\t "
				+ source.size());

	}

	private static String readInput(String message) {
		String input = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		try {
			System.out.print(message);
			input = reader.readLine();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
		return input;
	}

	private static ArrayList<ArrayList<CounterCapsule>> EliminateBadOnes(
			Hashtable<ArrayList<CounterCapsule>, Integer> frequencySet,
			int minFrequency, ArrayList<CounterCapsule> singleList,
			int currentIteration) {
		System.out
				.println("----------------------------------------------------------------");
		System.out.println("\t\t Iteration " + currentIteration);
		System.out
				.println("----------------------------------------------------------------");
		ArrayList<ArrayList<CounterCapsule>> o = new ArrayList<ArrayList<CounterCapsule>>();

		for (ArrayList<CounterCapsule> a0 : frequencySet.keySet()) {
			String keyName = "";
			for (CounterCapsule cc :  a0) {
				keyName = keyName + " + " + cc.parentAttribute.getName() + " is " + cc.Name;
				if (!singleList.contains(cc))
					singleList.add(cc);
			}
			boolean isPass = (frequencySet.get(a0) >= minFrequency);
			keyName = keyName.substring(3);
			
			if (isPass){
				System.out.printf("\t%-25s -> %2d -> %s\n", keyName,
						frequencySet.get(a0), (isPass ? "Pass" : "Fail"));

				o.add(a0);}
			
		}
		return o;
	}

	private static Hashtable<ArrayList<CounterCapsule>, Integer> CountFrequency(
			Hashtable<ArrayList<CounterCapsule>, Integer> checkset,
			ArrayList<ArrayList<String>> data) {
		for (ArrayList<String> a0 : data) {
			for (ArrayList<CounterCapsule> s0 : checkset.keySet()) {
				boolean didPass = true;
				for (CounterCapsule cc : s0) {
					// System.out.println(a0.get(cc.Index) + " and " + cc.Name +
					// " is " + (a0.get(cc.Index).compareTo(cc.Name) == 0));
					didPass = (a0.get(cc.Index).compareTo(cc.Name) == 0)
							&& didPass;
				}
				// System.out.println("?" + current + ", " + target);
				if (didPass) {
					checkset.put(s0, checkset.get(s0) + 1);
				}
			}
		}

		/*
		 * for (ArrayList<CounterCapsule> a0 : checkset.keySet()) { String
		 * keyName = ""; for (CounterCapsule cc : a0) { keyName = keyName +
		 * " + " + cc.Name; } String k = keyName + " = " + checkset.get(a0);
		 * System.out.println(k.substring(3)); }
		 */
		return checkset;
	}

	private static Hashtable<ArrayList<CounterCapsule>, Integer> Generate(
			int itemPerSet, Hashtable<String, CounterCapsule> source) {

		Hashtable<Integer, String> trans = new Hashtable<Integer, String>();
		int i = 0;
		for (String k : source.keySet()) {
			i++;
			trans.put(i, k);
		}

		ArrayList<ArrayList<Integer>> combos = new Combiner().combine(i,
				itemPerSet);
		// PrintArray(combos);

		// number -> Hashtable key -> string value -> CounterCaps
		Hashtable<ArrayList<CounterCapsule>, Integer> checkSet = new Hashtable<ArrayList<CounterCapsule>, Integer>();
		for (ArrayList<Integer> c : combos) {
			ArrayList<CounterCapsule> c0 = new ArrayList<CounterCapsule>();
			for (Integer i0 : c) {
				String key = trans.get(i0);
				c0.add(source.get(key));
			}
			checkSet.put(c0, 0);
		}
		return checkSet;
	}

	private static void PrintArray(ArrayList s) {
		for (Object o : s) {
			System.out.println("> " + o.toString());
		}
	}

}
