package receiver;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class MyLogger {

	Logger logger;
	
	public MyLogger(String fileName, String Name) throws SecurityException, IOException {
		logger = Logger.getLogger(Name);
		FileHandler fh = new FileHandler(fileName);
		fh.setFormatter( new Formatter() {
			@Override
			public String format(LogRecord record) {
				return record.getMessage() + "\n";
			}
		}); 
		logger.addHandler(fh);
	}
	
	public void write(String msg) {
		logger.info(msg);
	}
}