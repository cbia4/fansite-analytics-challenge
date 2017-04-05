import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;


public class LogWriter {

	private String filePath;
	private BufferedWriter bw;
	private FileWriter fw;

	public LogWriter(String filePath) {

		this.filePath = filePath;

		try {
			fw = new FileWriter(this.filePath);
			bw = new BufferedWriter(fw);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void write(String line) { 
		try {
			bw.write(line); 
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			if (bw != null) bw.close();
			if (fw != null) fw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}


}