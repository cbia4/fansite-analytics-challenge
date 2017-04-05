import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

// 
public class DataParser {

	private static final String LOG_DATA_REGEX = "(?<h>[^\\s]+)[^\\w]+\\[(?<ts>.+?)\\][ ]+[\\S](?<r>.+?)[\\S][ ]+(?<s>[0-9]{3})[^\\w]+(?<b>[0-9]+|\\-)";
	private static final String REQUEST_REGEX = "(?<requestURL>/[\\S]*)";
	private static final Pattern inputPattern = Pattern.compile(LOG_DATA_REGEX);
	private static final Pattern requestURLPattern = Pattern.compile(REQUEST_REGEX);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyy:HH:mm:ss Z");

	// DataParser should not be instantiated 
	private DataParser() {}

	// Parses a line of log input - prints error if input does not match the specified regex
	public static LogData parse(String line, int lineNumber) {

		// Parse input
		Matcher m = inputPattern.matcher(line);
		if(!m.find()) {
			System.err.format("Could not parse line %d: %s%n",lineNumber, line);
			return null;
		}

		// Assign captured regex groups 
		String host = m.group("h");
		String timestamp = m.group("ts");
		String request = m.group("r");
		String statusCode = m.group("s");
		String bytes = m.group("b");

		int sc = Integer.parseInt(statusCode); // parse status code 
		long timestampValue = FastDateFormat.parse(timestamp,lineNumber); // parse timestamp (using FastDateFormat)
		String resource = parseResource(request); // parse resource
		int b = bytes.equals("-") ? 0 : Integer.parseInt(bytes); // parse bytes

		// Return a new LogData object 
		return new LogData(line,host,timestamp,timestampValue,resource,sc,b);
	}

	// Parses for resources ending in an extension 
	private static String parseResource(String request) {
		Matcher m = requestURLPattern.matcher(request);
		if (m.find()) return m.group("requestURL");
		return null;
	}

	// SimpleDateFormat (VERY Slow)
	private static long parseTimeStamp(String timestamp) {
		try {
			Date d = sdf.parse(timestamp);
			return d.getTime();
		} catch(ParseException e) {
			System.err.println("ParseException ERROR");
			e.printStackTrace();
		}

		return -1;
	}

}