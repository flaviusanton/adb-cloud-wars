package JSONRPC.filters.client;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import JSONRPC.ClientFilterBase;


public class DebugLogger extends ClientFilterBase {

	public static final boolean LOG_TO_CONSOLE = true;
	public static final boolean LOG_TO_FILE = false;
	
	public String strLogPath = new String("");
	
	public PrintStream writer;
	public Date date = new Date();
	public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzzz");
	public Gson prettyPrinting = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create(); //set up pretty printing of the result
	JsonParser parser = new JsonParser();
	
	
	public DebugLogger(boolean bLogType) throws Exception {	
		
		if(!bLogType) 
			throw new Exception("Missing second argument, strLogPath.");
	}
	
	
	public DebugLogger(boolean bLogType, String strLogPath) throws Exception {
		
		if(!bLogType) {
			if(strLogPath != "")
				this.strLogPath = strLogPath;
			else
				throw new Exception("No log path specified.");
			
			writer = new PrintStream(new FileOutputStream(strLogPath));
			System.setOut(writer);
		}
	}
	
	
	public HashMap<String, Object> afterJSONEncode(HashMap<String, Object> mapParams) throws Exception {
		
		System.out.println("Sent request at: " + dateFormat.format(date));
		System.out.println(prettyPrinting.toJson(parser.parse(mapParams.get("strJSONRequest").toString())));
		System.out.println();
		System.out.println();
		
		return null;
	}

	
	public String beforeJSONDecode(String strJSONResponse) throws Exception {
		
		System.out.println("Received response at: " + dateFormat.format(date));
		System.out.println(prettyPrinting.toJson(parser.parse(strJSONResponse)));
		System.out.println();
		System.out.println();
		
		return null;
	}
}
