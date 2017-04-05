

public class SecurityUtility {

	private LogWriter w;

	public SecurityUtility(String logFilePath) {
		this.w = new LogWriter(logFilePath);
	}

	/**Feature 4 - log all requests from a host that failed to authenticate 3 
	  *consecutive times in 20 seconds, for 5 minutes 
	  */
	public void update(LogData d, Host h) {
		if(!h.isBlocked()) {
			if (d.getStatusCode() == 401) {
				h.escalate(d.getTime()); // increment the number of failed attempts
				return;
			}

			h.reset(); // reset failed attempts to 0 if the server authorizes the request
			return;
		}

		// if the host is blocked and inside the blocking time fence - log the request 
		if(h.inBlockWindow(d.getTime())) {
			w.write(d.getRaw());
			return;
		}

		// if the host is blocked and outside the blocking time fence, 
		// reset it and call the method again
		h.reset();
		update(d,h);
	}

	public void closeUtil() {
		w.close();
	}
}