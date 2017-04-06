import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Comparator;

import java.util.ArrayList;
import java.util.Date;

import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;



public class AnalyticsUtility {

	// Feature 1 Structures  
	private HashMap<String,Host> hostMap;
	private Host[] hosts;

	// Feature 2 Structures 
	private HashMap<String,Resource> resourceMap;
	private Resource[] resources;

	private LinkedList<Second> seconds;
	private LinkedList<Interval> intervalQueue;
	private Interval[] intervals;


	// Feature 4 - No Structures Necessary 
	SecurityUtility sec;
	// See Host.java and updateBlockedAttempts for details 

	// Constructor
	public AnalyticsUtility() {

		// Initialize data structures 
		this.hostMap = new HashMap<String,Host>();
		this.resourceMap = new HashMap<String,Resource>();
		this.seconds = new LinkedList<Second>();

		// Initialize security utility 
		this.sec = new SecurityUtility("../log_output/blocked.txt");
	}

	// adds log data and organizes it 
	public void update(LogData d) {
		Host h = updateHosts(d.getHostName()); // Feature 1
		if (d.getResource() != null) updateResources(d.getResource(), d.getBytesReturned()); // Feature 2
		updateSeconds(d);
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

		createIntervalArray();
		// Convert dynamic structures to static arrays for sorting
		hosts = hostMap.values().toArray(new Host[hostMap.size()]);
		resources = resourceMap.values().toArray(new Resource[resourceMap.size()]);

		// MergeSort - O(nlgn)
		Arrays.sort(hosts, Host.getComparator());
		Arrays.sort(resources, Resource.getComparator());
		Arrays.sort(intervals, Interval.getComparator());
	}

	// writes the data using LogWriters 
	public void write() {
		writeHosts();
		writeResources();
		writeHours();
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


	private void writeHours() {
		LogWriter w = new LogWriter("../log_output/hours.txt");
		int threshold = intervals.length < 10 ? intervals.length : 10;
		for(int i = 0; i < threshold - 1; i++) {
			Interval in = intervals[i];
			w.write(String.format("%s,%d%n",in.getStart(),in.getHits()));
		}


		Interval in = intervals[threshold-1];
		w.write(String.format("%s,%d",in.getStart(),in.getHits()));
		w.close();
	}



	private void updateSeconds(LogData d) {
		if(seconds.isEmpty()) {
			seconds.add(new Second(d.getTimeString(),d.getTime()));
			seconds.getLast().increment();
			return;
		}

		// if the time is equal to the last element in the list 
		if(d.getTime() == seconds.getLast().getMillis()) {
			seconds.getLast().increment();
			return;
		}

		// if the time is greater than the last element in the list 
		long time = seconds.getLast().getMillis() + 1000;
		while(d.getTime() >= time) {
			ZonedDateTime utc = Instant.ofEpochMilli(time).atZone(ZoneOffset.ofHoursMinutes(-4,0));
			seconds.add(new Second(DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z").format(utc),time));
			time += 1000;
		}

		seconds.getLast().increment();
	}


	private void createIntervalArray() {
		intervals = new Interval[seconds.size()];

		// if there is only one full hour interval 
		int threshold = intervals.length <= 3600 ? intervals.length : 3600;
		int sum = 0;

		for(int i = 0; i < threshold; i++) {
			sum += seconds.get(i).getCount();
		}

		int lo = 0;
		int hi;
		for(hi = threshold; hi < intervals.length; hi++) {
			Second s = seconds.get(lo);
			intervals[lo] = new Interval(s.getTS(),s.getMillis(),sum);
			sum -= s.getCount();
			sum += seconds.get(hi).getCount(); 
			lo++; 

		}

		while(lo < hi) {
			Second s = seconds.get(lo);
			intervals[lo] = new Interval(s.getTS(),s.getMillis(),sum);
			sum -= s.getCount();
			lo++;
		}

	}

	// private void newThink(LogData d) {
	// 	if(intervalQueue.isEmpty()) {
	// 		intervalQueue.add(d);
	// 		return;
	// 	}

	// 	// if the queue is not empty...

	// 	if(d.getTime() - )

	// }



}



