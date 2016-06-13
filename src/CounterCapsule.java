
public class CounterCapsule {
	
	
	
	public int Index;
	
	public Attribute parentAttribute;


	public CounterCapsule(int index, String name, Attribute parentAttribute) {
		super();
		Index = index;
		this.parentAttribute = parentAttribute;
		Name = name;
	}

	public String Name;
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return  getNiceName().hashCode();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getNiceName();
	}

	public String getNiceName() {
		return Index +"," + Name;		
	}

	
	
	
}
