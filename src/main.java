import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class main {

	public static double clock, meanArrivalTime, meanDescenteTime, LongService, lambda, mean;
	public static long NumberOfDepartures, QueueLength, NumberInFloor;

	public static final int arrival = 1;	 
	public static final int departure = 2;	
	public static final double mvtTime = 1.0/6.0;	
	//public static LinkedEvent root;
	public static List<Event> ascenceur;
	public static Random rd;
	public static int id;
	public static int etat; 
	public static int nbEtage;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		init();
		
		int index;
		while (NumberOfDepartures<1000000) {
			index = getMinFIFO( ascenceur);
			Event evn = ascenceur.get(index);
			ascenceur.remove(index);
			if (clock < evn.getTime()) clock = evn.getTime(); // l'ascenceur est en repos
			if (evn.getType() == arrival) {
				ProcessArrival(evn);
			}
			else  {

				ProcessDeparture(evn);
			}
		}

		//System.out.println(ascenceur);
		System.out.println("Nombre de personnes dans l'immeuble "+NumberInFloor);
		System.out.println("Nombre de demande en attente "+ascenceur.size());
		System.out.println("Long Service = "+LongService);
	}

	private static void ProcessDeparture(Event evn) {
		// TODO Auto-generated method stub
		//System.out.println("Départ d'un client à "+clock);

		// Appeller l'ascenceur 
		if (etat != evn.getEtage()) clock += Math.abs(etat-evn.getEtage())*mvtTime;

		//System.out.println("le temps d'attente du client "+ evn.id+" = "+evn.getTime());
		if (LongService < (clock-evn.getTime())) LongService = (clock-evn.getTime());
		// Descendre
		clock += (evn.getEtage() - 1)*mvtTime;
		etat = 1;

		NumberOfDepartures++;
		NumberInFloor--;

	}

	private static void ProcessArrival(Event evn) {
		// TODO Auto-generated method stub
		//System.out.println("Arrivée d'un client à "+clock);
		QueueLength++;
		// Appeler l'ascenceur
		clock += (etat-1)*mvtTime;


		// Programmer le temps de la descente
		ScheduleDeparture(evn);

		// Monter
		//System.out.println("le temps d'attente du client "+ evn.id+" = "+(clock-evn.getTime()) );
		if (LongService < (clock-evn.getTime())) LongService = (clock-evn.getTime());

		clock += mvtTime;
		etat = evn.getEtage();
		NumberInFloor++;

		id++;
		Event env = new Event(id, arrival, poisson(rd, lambda)+clock, 2 + (int) (rd.nextDouble()*(nbEtage-1)) );
		ascenceur.add(env);
	}

	private static void ScheduleDeparture(Event event) {
		// TODO Auto-generated method stub
		//NumberInService++;
		Event env = new Event(event.getId(), departure, exponential(rd, mean)+clock, event.getEtage());
		ascenceur.add(env);
	}

	private static void init() {
		NumberOfDepartures = QueueLength = NumberInFloor =  0;
		clock = LongService = 0.0;
		lambda = 0.5;
		mean = 60;
		id=1;
		etat = 1;
		nbEtage = 7;
		rd = new Random();
		Event env = new Event(id, arrival, poisson(rd, lambda), 2 + (int) (rd.nextDouble()*(nbEtage-1)) );

		ascenceur = new ArrayList<Event>();	
		ascenceur.add(env);
		QueueLength++;
	}

	private static int getMinFIFO(List<Event> L) {
		int index = 0;
		double min = L.get(0).getTime();
		//int min = ;
		for (int i=1; i<L.size(); i++) {
			if (L.get(i).getTime()<min) {
				min = L.get(i).getTime();
				index = i;
			}
		}
		return index;
	}

	public static double exponential(Random rng, double mean) {
		return -mean*Math.log( rng.nextDouble() );
	}

	public static double poisson(Random rng, double lambda) {
		double n=0;
		double p = 1;
		double r;
		while(true) {
			p = rng.nextDouble()*p;
			if(p<Math.exp(-lambda)) return n;
			else n+=1;
		}
	}
}
