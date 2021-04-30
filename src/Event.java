
public class Event {
	private int type;
	private double time;
	private int etage;


	public int getEtage() {
		return etage;
	}

	public Event(int type, double time, int etage) {
		this.type = type;
		this.time = time;
		this.etage = etage;
	}

	public int getType() {
		return type;
	}

	public double getTime() {
		return time;
	}
	
	public boolean compare(Event env) {
		if (this.time>env.getTime()) return true;
		else return false;
	}

	@Override
	public String toString() {
		return "["+ type + ", " + time + "]";
	}
	
}
