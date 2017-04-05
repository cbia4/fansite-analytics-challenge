import java.util.Comparator;

public class Resource {

	private String resourcePath;
	private int bandwidth;

	public Resource(String resourcePath) {
		this.resourcePath = resourcePath;
		this.bandwidth = 0;
	}

	public String getName() { return this.resourcePath; }
	public void addToBandwidth(int b) { this.bandwidth += b; }
	public int getBandwidth() { return this.bandwidth; }

	public static ResourceComparator getComparator() {
		return new ResourceComparator();
	}

	private static class ResourceComparator implements Comparator<Resource> {
		@Override 
		public int compare(Resource r1, Resource r2) { return r2.bandwidth - r1.bandwidth; }
	}

}