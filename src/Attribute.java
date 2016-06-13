import java.util.ArrayList;

public class Attribute {
	
	private String name;
	
	private ArrayList<String> options;
	
	private int index;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Attribute(String name, ArrayList<String> options, int index) {
		super();
		this.name = name;
		this.options = options;		
		this.index = index;
	}

	public ArrayList<String> getOptions() {
		return options;
	}

	public void setOptions(ArrayList<String> options) {
		this.options = options;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public ArrayList<ArrayList<String>> Ask(int clones) {
		ArrayList<ArrayList<String>> returner = new ArrayList<ArrayList<String>>();
		for (String s: options) {
			ArrayList<String> d = new ArrayList<String>();
			for (int i = 0; i < clones ; i++) {
				d.add(s);
			}
			returner.add(d);
		}		
		return returner;
		
	}

}



