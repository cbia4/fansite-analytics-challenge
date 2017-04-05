import java.util.Comparator;

public class Interval {
	
	private String start;
	private int hits;

	public Interval(String start,int hits) {
		this.start = start;
		this.hits = hits;
	}

	public String getStart() { return this.start; }
	public int getHits() { return this.hits; }

	public static IntervalComparator getComparator() {
		return new IntervalComparator();
	}

	private static class IntervalComparator implements Comparator<Interval> {
		@Override 
		public int compare(Interval i1, Interval i2) { return i2.hits - i1.hits; }
	}

}