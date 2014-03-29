package JSONRPC;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import com.google.gson.JsonElement;

public class ClientFilterBase {

	/**
	 * Should be used to 
	 * - add extra request object keys;
	 * - translate or encode output params into the expected server request object format.
	 * @param array arrRequest.
	 * @return HashMap. Key is the name of the received parameter, and the value are their value. Null if no change
	*/
	public HashMap<String, Object> beforeJSONEncode(HashMap<String, Object> request) throws Exception {
		return null;
	}
	
	
	/**
	 * Should be used to 
	 * - encrypt, encode or otherwise prepare the JSON request string into the expected server input format;
	 * - log raw output.
	 * @param string strJSONRequest.
	 * @param string strEndpointURL.
	 * @param string arrHTTPHeaders.
	 * @return String json string
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws MalformedURLException 
	 * @return HashMap. Key is the name of the received parameter, and the value are their value. Null if no change
	*/
	public HashMap<String, Object> afterJSONEncode(HashMap<String, Object> mapParams) throws Exception {
		return null;
	}


	/**
	 * Should be used to 
	 * - decrypt, decode or otherwise prepare the JSON response into the expected JSON-RPC client format;
	 * - log raw input.
	 * @param string strJSONResponse.
	 * @return String. Returns a Json string, null if no change
	*/
	public String beforeJSONDecode(String strJSONResponse) throws Exception {
		return null;
	}
	
	
	/**
	 * Should be used to 
	 * - add extra response object keys;
	 * - translate or decode response params into the expected JSON-RPC client response object format.
	 * @param array arrResponse.
	 * @return JsonElement The new Json element, null if no change
	*/
	public JsonElement afterJSONDecode(JsonElement response) throws Exception {
		return null;
	}


	/**
	* Should be used to rethrow exceptions as different types.
	* The first plugin to throw an exception will be the last one.
	* If there are no filter plugins registered or none of the plugins have thrown an exception,
	* then JSONRPC_client will throw the original JSONRPC2SessionException.
	* @param JSONRPC2SessionException exception.
	*/
	public void exceptionCatch(JSONRPC_Exception exception) throws Exception {
	}

	
	/**
	 * First plugin to make a request will be the last one. The respective plugin MUST set &$bCalled to true.
	 * @return HashMap. Key is the name of the received parameter, and the value are their value. Null if no change
	*/
	public HashMap<String, Object> makeRequest(HashMap<String, Object> mapParams) {
		return null;
	}
}
