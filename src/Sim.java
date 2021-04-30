import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sim {
	public double lambda, mean;
	public static double LongService, SumResponseTime;
	public static long NumberOfServed, MaxQueueLength;
	public static int [] QueueLength;

	public static final int arrival = 1;	 
	public static final int departure = 2;	
	public static final double mvtTime = 1.0/30.0;	
	public int typeOrdo;
	public int typeRal;

	public static List<Event> evenemets;
	public static Alea rd;
	public int nbEtage;
	public int nbAscenceur;
	public static List<Ascenceur> ascenceurs;
	
	public Sim(double lambda, double mean, int nbEtage, int nbAscenceur, int typeOrdo, int typeRal) {
		this.lambda = lambda;
		this.mean = mean;
		this.nbEtage = nbEtage;
		this.nbAscenceur = nbAscenceur;
		this.typeOrdo = typeOrdo;
		this.typeRal = typeRal;
		
		init();
		start();
		
	}
	
	private void start() {
		int index = 0, indexa;
		while (NumberOfServed<1000000) {
			
			// choisir l'ascenceur
			indexa = choixAsc();
			
			// Choix de l'événement à traiter
			switch(this.typeOrdo) {
			case 0: index = getMinFCFS(evenemets,ascenceurs.get(indexa)); break;
			case 1: index = getMinSSTF(evenemets,ascenceurs.get(indexa)); break;
			}
			
			
			Event evn = evenemets.get(index);
			evenemets.remove(index);
			
			// l'ascenceur est en repos
			if (ascenceurs.get(indexa).getClock() < evn.getTime()) {
				if (this.typeRal==1) ascenceurs.get(indexa).setEtage(1);
				ascenceurs.get(indexa).setClock(evn.getTime()); 
			}
			
			// Traiter l'évenement
			if (evn.getType() == arrival) {
				ProcessArrival(evn, ascenceurs.get(indexa));
			}
			else  {

				ProcessDeparture(evn, ascenceurs.get(indexa));
			}
			NumberOfServed++;
			for(int i=0; i<nbEtage; i++) {
				if (MaxQueueLength < QueueLength[i]) MaxQueueLength = QueueLength[i];
			}
		}
	}

	private void init() {
		NumberOfServed = MaxQueueLength = 0;
		LongService = SumResponseTime = 0.0;
		QueueLength = new int[this.nbEtage];
		ascenceurs = new ArrayList<Ascenceur>();
		for (int i=0;i<this.nbAscenceur; i++) ascenceurs.add(new Ascenceur(i, 0));
		
		rd = new Alea();
		Event env = new Event(arrival, poisson(rd, this.lambda), 2 + (int) (rd.gener()*(this.nbEtage-1)) );
		evenemets = new ArrayList<Event>();	
		evenemets.add(env);
		QueueLength[0]++;
	}
	
	private void ProcessArrival(Event evn, Ascenceur ascenceur) {

		// Appeler l'ascenceur
		ascenceur.setClock(ascenceur.getClock()+(ascenceur.getEtage()-1)*mvtTime);
		QueueLength[0]--;
		// Programmer le temps de la descente
		ScheduleDeparture(evn, ascenceur);

		// Monter
		//System.out.println("le temps d'attente du client "+ evn.id+" = "+(clock-evn.getTime()) );
		SumResponseTime += (ascenceur.getClock()-evn.getTime());
		if (LongService < (ascenceur.getClock()-evn.getTime())) LongService = (ascenceur.getClock()-evn.getTime());

		//clock += mvtTime;
		ascenceur.setClock(ascenceur.getClock()+ mvtTime) ;
		ascenceur.setEtage(evn.getEtage()); 

		Event env = new Event(arrival, poisson(rd, lambda)+ascenceur.getClock(), 2 + (int) (rd.gener()*(nbEtage-1)) );
		evenemets.add(env);
		QueueLength[0]++;
	}

	private void ScheduleDeparture(Event event, Ascenceur ascenceur) {
		//NumberInService++;
		Event env = new Event(departure, exponential(rd, mean)+ascenceur.getClock(), event.getEtage());
		evenemets.add(env);
		QueueLength[event.getEtage()-1]++;
	}

	private void ProcessDeparture(Event evn, Ascenceur ascenceur) {
		//System.out.println("Départ d'un client à "+clock);

		// Appeller l'ascenceur On peut supprimer la condition
		ascenceur.setClock(ascenceur.getClock()+ Math.abs(ascenceur.getEtage()-evn.getEtage())*mvtTime) ;

		//System.out.println("le temps d'attente du client "+ evn.id+" = "+evn.getTime());
		SumResponseTime += (ascenceur.getClock()-evn.getTime());
		if (LongService < (ascenceur.getClock()-evn.getTime())) LongService = (ascenceur.getClock()-evn.getTime());
		
		// Descendre
		ascenceur.setClock(ascenceur.getClock()+ (evn.getEtage() - 1)*mvtTime) ;
		QueueLength[evn.getEtage()-1]--;
		ascenceur.setEtage(1);
	}
	
	private static int getMinFCFS(List<Event> L, Ascenceur ascenceur) {
		int index = 0;
		double min = L.get(0).getTime();

		for (int i=1; i<L.size(); i++) {
			if (L.get(i).getTime()<min) {
				min = L.get(i).getTime();
				index = i;
			}
		}
		return index;
	}

	private static int getMinSSTF(List<Event> L, Ascenceur ascenceur) {
		int index = 0;
		int diff = Math.abs(L.get(0).getEtage()-ascenceur.getEtage());
		
		for (int i=1; i<L.size(); i++) {
			if (L.get(i).getTime()< L.get(index).getTime()-2*mvtTime*diff) {
				diff = Math.abs(L.get(i).getEtage()-ascenceur.getEtage());
				index = i;
			}
			
		}
		
		return index;
	}
	
	private int choixAsc() {
		int index = 0;
		double min = ascenceurs.get(index).getClock();
		
		for (Ascenceur a : ascenceurs) {
			if(a.getClock()<min) {
				min = a.getClock();
				index = a.getId();
			}
		}
		return index;
	}
	
	private static double exponential(Alea rng, double mean) {
		return -mean*Math.log( rng.gener() );
	}

	private static double poisson(Alea rd2, double lambda) {
		double n=0;
		double p = 1;
		while(true) {
			p = rd2.gener()*p;
			if(p<Math.exp(-lambda)) return n;
			else n+=1;
		}
	}
	
	public void writeReport() {
		System.out.println("Taux moyen d'arrivée global de lambda\t = "+this.lambda);
		System.out.println("Temps moyen dans un étage \t\t = "+this.mean);
		System.out.println("Nombre d'étage \t\t\t\t = "+this.nbEtage);
		System.out.println("Nombre d'ascenceurs \t\t\t = "+this.nbAscenceur);
		System.out.print("Type d'ordonnancement \t\t\t");
		if(this.typeOrdo==0) System.out.println(" = « First-Come-First-Serve » (FCFS).");
		if(this.typeOrdo==1) System.out.println(" = « Shortest-Seek-Time-First » (SSTF).");
		System.out.print("Politique de marche au ralenti \t\t");
		if(this.typeRal==0) System.out.println(" = Rester où il est.");
		if(this.typeRal==1) System.out.println(" = Aller à l'étage inférieure.");
		
		System.out.println("\n--------------------------------------------------------------------------------\n");
		System.out.println("Nombre de personnes servie  \t\t = "+NumberOfServed);
		System.out.println("Temps moyen de réponse  \t\t = "+SumResponseTime/NumberOfServed+" min");
		System.out.println("LONGUEUR DE LIGNE MAXIMALE \t\t = "+MaxQueueLength);
		System.out.println("Long Service \t\t\t\t = "+LongService+" min");
	}
}
