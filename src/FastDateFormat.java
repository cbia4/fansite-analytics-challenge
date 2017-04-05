import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.ParseException;

public class FastDateFormat {

	private static final String TIMESTAMP_REGEX = "(?<day>[0-9]{2})/(?<month>[A-Za-z]{3})/(?<year>[0-9]{4}):(?<hour>[0-9]{2}):(?<min>[0-9]{2}):(?<sec>[0-9]{2}) (?<tz>[+|-][0-9]{4})";
	private static final Pattern timestampPattern = Pattern.compile(TIMESTAMP_REGEX);
	private static int currLineNumber = 0;

	// FastDateFormat is static and should not be instantiated 
	private FastDateFormat() {}

	// Parses Date/Time and prepares its conversion to milliseconds 
	public static long parse(String timestamp, int lineNumber) {
		currLineNumber = lineNumber;
		Matcher m = timestampPattern.matcher(timestamp);
		int[] time = new int[7];
		long timi = -1;
		try {
			if(m.find()) {
				time[0] = Integer.parseInt(m.group("day"));
				time[1] = convertMonths(m.group("month"));
				time[2] = Integer.parseInt(m.group("year"));
				time[3] = Integer.parseInt(m.group("hour"));
				time[4] = Integer.parseInt(m.group("min"));
				time[5] = Integer.parseInt(m.group("sec"));
				time[6] = Integer.parseInt(m.group("tz"));
				timi = convertToMillis(time);
			}
		} catch(ParseException e) {
			e.printStackTrace();
		}

		return timi;
	}

	// Converts a Date/Time to milliseconds 
	private static long convertToMillis(int[] time) {
		long yearMillis = ( time[2] - 1970 ) * (long) Double.parseDouble("3.1536E+10");
		long monthMillis = (time[1]) * (long) Double.parseDouble("2.6784E+9");
		long dayMillis = (time[0]) * (long) Double.parseDouble("8.64E+7");
		long hourMillis = (time[3]) * (long) Double.parseDouble("3.6E+6");
		long minMillis = (time[4]) * 60000;
		long secMillis = (time[5]) * 1000;
		long tzMillis = (time[6]) * (long) Double.parseDouble("3.6E+4") * -1;
		return yearMillis + monthMillis + dayMillis + hourMillis + minMillis + secMillis + tzMillis;
	}

	// Converts a Month String to an integer value 
	private static int convertMonths(String month) throws ParseException {
		switch (month) {
			case "Jan": return 0;
			case "Feb": return 1;
			case "Mar": return 2;
			case "Apr": return 3;
			case "May": return 4;
			case "Jun": return 5;
			case "Jul": return 6;
			case "Aug": return 7;
			case "Sep": return 8;
			case "Oct": return 9;
			case "Nov": return 10;
			case "Dec": return 11;
			default: throw new ParseException("Invalid Month: " + month, currLineNumber);
		}
	}
}