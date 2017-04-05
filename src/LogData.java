
public class LogData {
	private String rawString;
	private String host;
	private String timestamp;
	private long timestampValue;
	private String resource;
	private int statusCode;
	private int bytes;

	public LogData(String rawString,
		String host, 
		String timestamp,
		long timestampValue, 
		String resource, 
		int statusCode, 
		int bytes) {

		this.rawString = String.format("%s%n",rawString);
		this.host = host;
		this.timestamp = timestamp;
		this.timestampValue = timestampValue;
		this.resource = resource;
		this.statusCode = statusCode;
		this.bytes = bytes;
	}

	// Accessors
	public String getRaw() { return this.rawString; }
	public String getHostName() { return this.host; }
	public String getTimeString() { return this.timestamp; }
	public long getTime() { return this.timestampValue; }
	public String getResource() { return this.resource; }
	public int getStatusCode() { return this.statusCode; }
	public int getBytesReturned() { return this.bytes; }
	
}