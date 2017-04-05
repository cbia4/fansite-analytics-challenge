import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class ProcessLog {

	// Prints a message and the time in mm min ss sec format
	public static void printTime(String msg, long millis) {
		System.out.format("%s: %s%n",msg,
			String.format("%02d min, %02d sec",
				TimeUnit.MILLISECONDS.toMinutes(millis),
				TimeUnit.MILLISECONDS.toSeconds(millis) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
	}


	// Driver - parse configuration file and pass log data to the analytics utility 
	public static void main(String[] args) {

		AnalyticsUtility analytics = new AnalyticsUtility();
		BufferedReader br = null;
		FileReader fr = null;

		String path = "../log_input/log.txt";
		//String path = "../log_input/sample.txt";

		long start, total;
		start = System.currentTimeMillis();

		try {
			fr = new FileReader(path);
			br = new BufferedReader(fr);
			String line;
			int lineNumber = 1;

			while((line = br.readLine()) != null) {
				LogData d = DataParser.parse(line,lineNumber++);
				if(d != null) {
					analytics.update(d);
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if(br != null) br.close();
				if(fr != null) fr.close();
			} catch(IOException ex) {
				ex.printStackTrace();
			}

		}

		System.out.println("Running Analytics...");
		analytics.run();

		total = System.currentTimeMillis() - start;
		printTime("Program Run Time",total);
	}
}