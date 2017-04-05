import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Comparator;



public class AnalyticsUtility {

	// Feature 1 Structures  
	private HashMap<String,Host> hostMap;
	private Host[] hosts;

	// Feature 2 Structures 
	private HashMap<String,Resource> resourceMap;
	private Resource[] resources;

	// Feature 3 Structures 
	private LinkedList<LogData> intervalQueue;
	private HashSet<Interval> intervalSet;
	private Interval[] intervals;
	private Interval lastInterval; // the last interval added to the interval set 

	// Feature 4 - No Structures Necessary 
	SecurityUtility sec;
	// See Host.java and updateBlockedAttempts for details 

	// Constructor
	public AnalyticsUtility() {

		// Initialize data structures 
		this.hostMap = new HashMap<String,Host>();
		this.resourceMap = new HashMap<String,Resource>();
		this.intervalQueue = new LinkedList<LogData>();
		this.intervalSet = new HashSet<Interval>();

		// Initialize security utility 
		this.sec = new SecurityUtility("../log_output/blocked.txt");
	}

	// adds log data and organizes it 
	public void update(LogData d) {
		Host h = updateHosts(d.getHostName()); // Feature 1
		if (d.getResource() != null) updateResources(d.getResource(), d.getBytesReturned()); // Feature 2
		updateIntervals(d); // Feature 3
		sec.update(d,h); // Feature 4
	}

	// runs analytics over the gathered data 
	public void run() {
		prepare();
		write();
		sec.closeUtil();
	}

	// prepares data by  
	private void prepare() {
		emptyIntervalQueue();

		// Convert dynamic structures to static arrays for sorting
		hosts = hostMap.values().toArray(new Host[hostMap.size()]);
		resources = resourceMap.values().toArray(new Resource[resourceMap.size()]);
		intervals = intervalSet.toArray(new Interval[intervalSet.size()]);

		// MergeSort - O(nlgn)
		Arrays.sort(hosts, Host.getComparator());
		Arrays.sort(resources, Resource.getComparator());
		Arrays.sort(intervals, Interval.getComparator());
	}

	// writes the data using LogWriters 
	public void write() {
		writeHosts();
		writeResources();
		writeIntervals();
	}

	/**
	  * Feature 1 - Maps a hostname/IP String -> Host 
	  * Host keeps track of how many times the hostname/IP has accessed the site 
	  */
	private Host updateHosts(String hostName) {
		if(!hostMap.containsKey(hostName)) {
			hostMap.put(hostName, new Host(hostName));
		}

		hostMap.get(hostName).incrementHits();
		return hostMap.get(hostName);
	}


	/**
	  * Feature 1 - Writes to the hosts log the specified number of 
	  * top active users 
	  */
	private void writeHosts() {
		LogWriter w = new LogWriter("../log_output/hosts.txt");
		int threshold = hosts.length < 10 ? hosts.length : 10;
		for(int i = 0; i < threshold; i++) {
			Host h = hosts[i];
			w.write(String.format("%s,%d%n",h.getName(),h.getHits()));
		}

		w.close();
	}

	/**
	  * Feature 2 - Maps a resource String -> Resource 
	  * Resource keeps track of how many bytes of the resource have been returned 
	  */
	private void updateResources(String resourcePath, int bytesReturned) {
		if(!resourceMap.containsKey(resourcePath)) {
			resourceMap.put(resourcePath, new Resource(resourcePath));
		}

		resourceMap.get(resourcePath).addToBandwidth(bytesReturned);
	}

	/**
	  * Feature 2 - Writes to the resources log the specified number of
	  * resources that consume the most bandwidth
	  */
	private void writeResources() {
		LogWriter w = new LogWriter("../log_output/resources.txt");
		int threshold = resources.length < 10 ? resources.length : 10;
		for(int i = 0; i < threshold; i++) {
			Resource r = resources[i];
			w.write(String.format("%s%n",r.getName()));
		}

		w.close();
	}

	/**
	  * Feature 3 - Uses a queue that is consistently filled with 60 minutes worth of 
	  * LogData. When a new piece of data is added - the queue updates by removing 
	  * any data that was logged more than 60 minutes prior to the latest addition.
	  *
	  * When removing the data, an Interval is created that holds the timestamp that 
	  * starts a 60 minute interval and the size of the queue at the beginning of 
	  * the interval. This Interval is added to a Set of Intervals 
	  */
	private void updateIntervals(LogData d) {
		int intervalSize = intervalQueue.size(); // get the size of the queue before adding new data 
		intervalQueue.add(d);

		// if the latest log data is more than 60 minutes later than the first element 
		// in the queue, update the lastInterval variable and add it to the set 
		// keep removing until the queue only contains a 60 minute span of log data
		if(d.getTime() - intervalQueue.peek().getTime() > 3600000) {
			lastInterval = new Interval(intervalQueue.peek().getTimeString(),intervalSize);
			intervalSet.add(lastInterval);
			while(!intervalQueue.isEmpty() && (d.getTime() - intervalQueue.peek().getTime() > 3600000)) {
				intervalQueue.remove();
			}
		}
	}

	/**
	  * Feature 3 - Writes to the hours log the specified number of 
	  * the busiest 60 minute intervals 
	  */
	private void writeIntervals() {
		LogWriter w = new LogWriter("../log_output/hours.txt");
		int threshold = intervals.length < 10 ? intervals.length : 10;
		for(int i = 0; i < threshold; i++) {
			Interval in = intervals[i];
			w.write(String.format("%s,%d%n",in.getStart(),in.getHits()));
		}

		w.close();
	}

	/**
	  * Feature 3 - Helper method that adds any intervals left in the intervalQueue
	  * to the interval set
	  */
	private void emptyIntervalQueue() {
		while(!intervalQueue.isEmpty()) {
			int intervalSize = intervalQueue.size();
			LogData ld = intervalQueue.remove();
			if(lastInterval == null || !ld.getTimeString().equals(lastInterval.getStart())) {
				lastInterval = new Interval(ld.getTimeString(),intervalSize);
				intervalSet.add(lastInterval);
			}
		}
	}
}