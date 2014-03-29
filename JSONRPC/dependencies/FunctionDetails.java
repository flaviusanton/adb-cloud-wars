package JSONRPC.dependencies;

import java.util.ArrayList;



/**
 * Class for the details of a given function, available on the server
 *
 */
public class FunctionDetails {

	public String name; //name of the function
	public String returnType;
	public ArrayList<String> parameters = new ArrayList<String>();
	
	public FunctionDetails() {
	}
	
	public FunctionDetails(String name, String comment, ArrayList<String> parameters, String returnType) {
		this.name = name;
		this.parameters = parameters;
		this.returnType = returnType;
	}
	
	public String toString() {
		return "Function Name: " + name + "\nParameteres" + parameters.toString();
	}
}
