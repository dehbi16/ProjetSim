
public class Ascenceur {
	private int id;
	private double clock;
	private int etage;
	private double etat; // 0 : arret 1 : Up 2 : Down
	
	public Ascenceur(int id, double clock) {
		this.id = id;
		this.clock = clock;
		this.etage = 1;
		this.etat = 0;
	}
	
	public double getClock() {
		return clock;
	}

	public void setClock(double clock) {
		this.clock = clock;
	}

	public int getEtage() {
		return etage;
	}

	public void setEtage(int etage) {
		this.etage = etage;
	}

	public double getEtat() {
		return etat;
	}

	public void setEtat(double etat) {
		this.etat = etat;
	}

	public int getId() {
		return id;
	}

	
	
}
