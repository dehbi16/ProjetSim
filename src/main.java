import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class main {

	
	public static void main(String argv[]) {
		
		double lambda = 0.5;
		double mean = 60;
		int nbEtage = 7;
		int nbAscenceur = 3;
		int typeOrdo = 1;
		int typeRal = 1;

		Sim s = new Sim(lambda, mean, nbEtage, nbAscenceur, typeOrdo, typeRal);

		s.writeReport();
	}
	
	
}
