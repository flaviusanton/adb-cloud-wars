package JSONRPC;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import JSONRPC.dependencies.MySSLSocketFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;


/**
* JSON-RPC 2.0 client.
* http://www.jsonrpc.org/specification
* Batch RPC calls and notifications are not implemented.
*/
@SuppressWarnings("deprecation")
public class Client {
	
	private static final String JSONRPC_VERSION = "2.0";
	
	/**
	* JSON-RPC protocol call ID.
	*/
	protected int _nCallID = 0;
	
	
	/**
	* Filter plugins which extend JSONRPC_client_filter_plugin_base.
	*/
	protected ArrayList<ClientFilterBase> _arrFilterPlugins = new ArrayList<ClientFilterBase>();
	
	
	
	/**
	* JSON-RPC server endpoint URL
	*/
	protected String _strJSONRPCRouterURL;

	
	/**
	 * This is the constructor function. It creates a new instance of Jsonrpc2client.
	 * Example: JSONRPC2Client("http://example.ro")
	 *
	 * @param string strJSONRPCRouterURL. The address of the server.
	*/
	public Client(String strJSONRPCRouterURL)
	{
		this._strJSONRPCRouterURL = strJSONRPCRouterURL.trim();
	}
	
	
	protected String _strHTTPUser;
	protected String _strHTTPPassword;
	
	
	/**
	 * This is the function used to set the HTTP credentials set.
	 * 
	 * @param strUsername
	 * @param strPassword
	 */
	public void httpCredentialsSet(String strUsername, String strPassword)
	{
		this._strHTTPUser = strUsername;
		this._strHTTPPassword = strPassword;
	}
	
	
	/**
	 * This method is used for function calling, with unnamed params (the order matters)
	 * 
	 * @param strFunctionName
	 * @param arrParams
	 * @return
	 * @throws Exception
	 */
	public Object _rpc(String strFunctionName, ArrayList<Object> arrParams) throws Exception {
		HashMap<String, Object> mapRequest = new HashMap<String, Object>();
		
		if(arrParams == null) arrParams = new ArrayList<Object>();
		
		mapRequest.put("method", strFunctionName);
		mapRequest.put("params", arrParams);
		mapRequest.put("id", ++this._nCallID);
		mapRequest.put("jsonrpc", JSONRPC_VERSION);
		
		
		return _makeCall(strFunctionName, mapRequest);
	}
	

	/**
	 * This method is used for function calling, with named params (the order doesn't matter)
	 * 
	 * @param strFunctionName
	 * @param mapNamedParams
	 * @return
	 * @throws Exception
	 */
	public Object _rpc(String strFunctionName, HashMap<String, Object> mapNamedParams) throws Exception {
		HashMap<String, Object> mapRequest = new HashMap<String, Object>();
		
		if(mapNamedParams == null) mapNamedParams = new HashMap<String, Object>();
		
		mapRequest.put("method", strFunctionName);
		mapRequest.put("params", mapNamedParams);
		mapRequest.put("id", ++this._nCallID);
		mapRequest.put("jsonrpc", JSONRPC_VERSION);
		
		return _makeCall(strFunctionName, mapRequest);
	}
	
	
	/**
	 * This is the main function to call the RPC API.
	 * 
	 * @param strFunctionName
	 * @param mapRequest, the params sent to the function on the server
	 * @return JsonElement The response of the server in Gson format
	 * @throws Exception
	 */
	public Object _makeCall(String strFunctionName, HashMap<String, Object> mapRequest) throws Exception
	{
		for(ClientFilterBase filterPlugin : this._arrFilterPlugins) {
			HashMap<String, Object> result = filterPlugin.beforeJSONEncode(mapRequest);
			if(result != null)
				filterPlugin.beforeJSONEncode(mapRequest);
		}
		
		String strRequest = json_encode(mapRequest);
		String strJSONRPCEndpointURL = this._strJSONRPCRouterURL;
		
			
		HashMap<String, String> mapHTTPHeaders = new HashMap<String, String>();
		mapHTTPHeaders.put("Content-type", "application/json");
		
		
		if(this._strHTTPPassword != null && this._strHTTPUser != null)
			mapHTTPHeaders.put("Authorization", "Basic " + Base64.encodeBase64String((this._strHTTPUser + ":" + this._strHTTPPassword).getBytes()));
				
				
		
		for(ClientFilterBase filterPlugin : _arrFilterPlugins) {
			
			HashMap<String, Object> result = new HashMap<String, Object>();
			result.put("strJSONRequest", strRequest);
			result.put("strJSONRPCEndpointURL", strJSONRPCEndpointURL);
			result = filterPlugin.afterJSONEncode(result);
			
			if(result != null) {
				if(result.get("strJSONRequest") != null)
					strRequest = result.get("strJSONRequest").toString();
				if(result.get("strJSONRPCEndpointURL") != null)
					strJSONRPCEndpointURL = result.get("strJSONRPCEndpointURL").toString();
			}
		}

		
		boolean bErrorMode = false;
		boolean bCalled=false;
		String strResult = null;
		
		for(ClientFilterBase filterPlugin : _arrFilterPlugins) {
			HashMap<String, Object> result = new HashMap<String, Object>(); 
			result.put("strRequest", strRequest);
			result.put("strJSONRPCEndpointURL", strJSONRPCEndpointURL);
			result.put("bCalled", bCalled);
			result = filterPlugin.makeRequest(result);
			
			if(result != null) {
				if(result.get("strRequest") != null)
					strRequest = (String) result.get("strResult");
				if(result.get("strJSONRPCEndpointURL") != null)
					strJSONRPCEndpointURL = (String) result.get("strJSONRPCEndpointURL");
				if(result.get("bCalled") != null)
					bCalled = (Boolean) result.get("bCalled");
			}
				
			
			if(bCalled)
				break;
		}
		
		
		if(!bCalled)
		{
			HttpClient httpClient = getNewHttpClient();
			HttpPost request = new HttpPost(strJSONRPCEndpointURL);
			StringEntity params = new StringEntity(strRequest);
		
			
			for(Entry<String, String> header : mapHTTPHeaders.entrySet()) 
				request.addHeader(header.getKey(), header.getValue());
			
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			strResult = reader.readLine();
			

			bErrorMode = response.getStatusLine().getStatusCode() != 200;
		}
		
		return this.processRAWResponse(strResult, bErrorMode);
	}
	
	
	/**
	 * @return HttpCLient A new client that accepts all certificats
	 */
	public HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory SSLSocket = new MySSLSocketFactory(trustStore);
	        SSLSocket.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", SSLSocket, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
	
	
	/**
	 * Encodes a json
	 * 
	 * @param mapRequest Input json with all the required fields
	 * @return String The encoded json
	 * @throws Exception
	 */
	private String json_encode(HashMap<String, Object> mapRequest)  throws Exception {
		
		String strResult = new String();
		Gson gson = new GsonBuilder().serializeNulls().create();
		
		strResult = gson.toJson(mapRequest);

		return strResult;
	}


	/**
	 * This is the function used to decode the received JSON and return its result.
	 * It is automatically called by _rpc.
	 * 
	 * @param strResult The json string returned by the server
	 * @param bErrorMode Whether or not the received JSON contains errors.
	 * @return JsonElement The json response in Gson format
	 * @throws Exception
	 */
	public Object processRAWResponse(String strResult, boolean bErrorMode) throws Exception
	{
		JsonElement jsonResponse = null;

		try {
			for(ClientFilterBase filterPlugin : _arrFilterPlugins) {
				String strJson = filterPlugin.beforeJSONDecode(strResult);
			
				if(strJson != null)
					strResult =strJson;
			}
			
			
			try {
				jsonResponse = new JsonParser().parse(strResult.toString());
			} catch (Exception exc) {
				throw new JSONRPC_Exception(exc.getMessage() + " . RAW response from server: " + strResult, JSONRPC_Exception.PARSE_ERROR);
			}
	
			
			for(ClientFilterBase filterPlugin : _arrFilterPlugins) {
				JsonElement result = filterPlugin.afterJSONDecode(jsonResponse);
				
				if(result != null)
					jsonResponse = result;
			}
			
			
			if(jsonResponse == null || jsonResponse.isJsonObject() == false || (bErrorMode == true && jsonResponse.getAsJsonObject().get("error") == null))
				throw new JSONRPC_Exception("Invalid response structure. RAW response: " + strResult, JSONRPC_Exception.INTERNAL_ERROR);
			else if(jsonResponse.getAsJsonObject().get("error") == null && jsonResponse.getAsJsonObject().get("result") != null)
			{
				if(jsonResponse.getAsJsonObject().get("result").isJsonPrimitive())
					return jsonResponse.getAsJsonObject().get("result");
				else
					return createMapFromJsonObject(jsonResponse.getAsJsonObject().get("result").getAsJsonObject());
			}
			
			throw new JSONRPC_Exception(jsonResponse.getAsJsonObject().get("error").getAsJsonObject().get("message").toString(), jsonResponse.getAsJsonObject().get("error").getAsJsonObject().get("code").getAsInt());
		
			
		} catch(JSONRPC_Exception exc) {
			for(ClientFilterBase filterPlugin : _arrFilterPlugins) 
				filterPlugin.exceptionCatch(exc);
			
			throw exc;
		}
	}
	
	
	/**
	 * This function is used to add filter plugins to an instance of JSONRPC_client.
	 * If there is an attempt to add multiple instances of the same filter,
	 * an exception is thrown.
	 *
	 * @param filter A new instance of a given plugin
	 * @throws Exception
	 */
	public void addFilterPlugins(ClientFilterBase filter) throws Exception {
		
		for(ClientFilterBase filterPlugin : _arrFilterPlugins) 
			if(filterPlugin.getClass() == filter.getClass())
				throw new Exception("Multiple instances of the same filter are not allowed.");
			
		_arrFilterPlugins.add(filter);
	}
	
	
	/**
	 * This function is used to remove client filter plugins.
	 * If there is an attempt to remove an unregistered filter plugin,
	 * an exception is thrown.
	 * @param filter The instance of the plugin that will be removed
	 * @throws Exception
	 */
	public void removeFilterPlugin(ClientFilterBase filter) throws Exception {
		
		int nIndex = 0;
		
		for(ClientFilterBase filterPlugin : _arrFilterPlugins) { 
			if(filterPlugin.getClass() == filter.getClass())
				break;
			
			nIndex++;
		}
				
		if(nIndex == _arrFilterPlugins.size())
			throw new Exception("Failed to remove filter plugin object, maybe plugin is not registered.");
		
		_arrFilterPlugins.remove(filter);
	}
	
	
	static Map<String, Object> createMapFromJsonObject(JsonObject jo) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		for (Entry<String, JsonElement> entry : jo.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			map.put(key, getValueFromJsonElement(value));
		}
		
		return map;
	}


	static Object getValueFromJsonElement(JsonElement je) {
		
		if (je.isJsonObject()) {
			
			return createMapFromJsonObject(je.getAsJsonObject());
		
		} else if (je.isJsonArray()) {
	    
			JsonArray array = je.getAsJsonArray();
			List<Object> list = new ArrayList<Object>(array.size());
	    
			for (JsonElement element : array) {
				list.add(getValueFromJsonElement(element));
			}
			return list;
			
		} else if (je.isJsonNull()) {
			
			return null;
		
		} else {// must be primitive
			
			JsonPrimitive p = je.getAsJsonPrimitive();
	    
			if (p.isBoolean()) 
				return p.getAsBoolean();
	    
			if (p.isString()) 
				return p.getAsString();
			// else p is number, but don't know what kind
	    
			String s = p.getAsString();
		    try {
		    	return new Integer(s);
		    } catch (NumberFormatException e) {
		    	// must be a decimal
		    	return new Double(s);
		    }
	    }
	}
}
