package JSONRPC.dependencies;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;


public class CommunicationLog {

	protected String path;
	
	public CommunicationLog() {
		this.path = new String();
	}
	
	public CommunicationLog(String strLogPath) {
		this.path = strLogPath;
	}

	/**
	 * Method for writing to the log file
	 * @param String msg. The message describing the Json
	 * @param String json. The json string
	 */
	public void write(String msg, String json) {
		
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		JsonParser parser = new JsonParser();
		String jsonOutput = gson.toJson(parser.parse(json.toString()));

		Date today = new Date();
		
		try {
			FileWriter output = new FileWriter(path + "Log.txt", true);
			output.write(today.toString() + "\r\n" + msg + "\r\n" + jsonOutput + "\r\n");
			output.close();
		} catch (IOException e) {
			System.err.println("logging IO error");
		}
	}
}
