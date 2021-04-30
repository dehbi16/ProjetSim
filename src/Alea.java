import java.time.Instant;

public class Alea {
	private double a;
	private double b;
	private double m;
	private long x0;
	private long x1;
	
	public Alea() {
		this.a = 73.0;
		this.b = 70.0;
		this.m = Math.pow(2, 20);
		this.x0 = System.nanoTime();
		this.x1 = System.currentTimeMillis(); 
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public double gener() {
		double x = (this.a*this.x1 + this.b*this.x0 )%this.m;
		this.x0 = this.x1;
		this.x1 = (long) x;
		return this.x1/this.m; 
	}
			

	
}
