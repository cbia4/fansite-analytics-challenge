import java.util.Comparator;

public class Host {

	// Basic Host Info
	private String id;
	private int hits;

	// Security Related 
	private boolean blocked;
	private long firstFailedLogin;
	private long blockStartTime;
	private int failedAttempts;

	public Host(String id) {
		this.id = id;
		this.hits = 0;

		this.blocked = false;
		this.firstFailedLogin = 0;
		this.blockStartTime = 0;
		this.failedAttempts = 0;
	}

	// Accessors
	public String getName() { return this.id; }
	public int getHits() { return this.hits; }
	public void incrementHits() { this.hits++; }
	public boolean isBlocked() { return this.blocked; }

	// Sets time that block began and sets blocked state to true
	private void block(long startTime) {
		this.failedAttempts = 0;
		this.blocked = true;
		this.blockStartTime = startTime;
	}

	// Used to determine whether this hosts activity should be logged 
	public boolean inBlockWindow(long accessTime) {
		return ((accessTime - this.blockStartTime) <= 300000);
	}

	// Reset security instance variables 
	public void reset() { 
		this.failedAttempts = 0;
		this.firstFailedLogin = 0;
		this.blockStartTime = 0;
		this.blocked = false;
	}

	// this is only to be called when a host is not currently blocked 
	// and the host has received a 401 http status code 
	public void escalate(long time) {

		// check for bad input or misuse of the method
		if (this.blocked || time < 0) return;

		// Reset if escalation is outside the specified time fence  
		if (time - firstFailedLogin > 20000) reset();
		
		this.failedAttempts++;

		if(this.failedAttempts < 2) {
			this.firstFailedLogin = time;
		} else if(this.failedAttempts > 2) {
			block(time);
		}
	}

	public static HostComparator getComparator() {
		return new HostComparator();
	}

	private static class HostComparator implements Comparator<Host> {
		@Override 
		public int compare(Host h1, Host h2) { return h2.hits - h1.hits; }
	}

}