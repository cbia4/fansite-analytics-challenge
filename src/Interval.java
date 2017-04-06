import java.util.Comparator;

public class Interval {
	
	private String start;
	private long startMillis;
	private int hits;

	public Interval(String start, long millis, int hits) {
		this.start = start;
		this.startMillis = millis;
		this.hits = hits;
	}

	public String getStart() { return this.start; }
	public long getMillis() { return this.startMillis; }
	public int getHits() { return this.hits; }

	public static IntervalComparator getComparator() {
		return new IntervalComparator();
	}

	private static class IntervalComparator implements Comparator<Interval> {
		@Override 
		public int compare(Interval i1, Interval i2) { 
			if(i2.hits != i1.hits) return i2.hits - i1.hits; 

			return i1.start.compareTo(i2.start);
		}
	}

}