
public class Second {
	private String timestamp;
	private long millis;
	private int ctr;

	public Second(String timestamp, long millis) {
		this.timestamp = timestamp;
		this.millis = millis;
		this.ctr = 0;
	}

	public String getTS() { return this.timestamp; }
	public long getMillis() { return this.millis; }
	public int getCount() { return this.ctr; }

	public void increment() { this.ctr++; }
}