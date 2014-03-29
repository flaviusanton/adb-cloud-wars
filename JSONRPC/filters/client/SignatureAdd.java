package JSONRPC.filters.client;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import JSONRPC.ClientFilterBase;


public class SignatureAdd extends ClientFilterBase {

	/**
	 * Private key used for hashed messages sent to the server
	 */
	protected String APIKey;
	
	protected HashMap<String, Object> mapExtraURLParams;
	
	/**
	* This is the constructor function. It creates a new instance of JSONRPC_filter_signature_add.
	* Example: JSONRPC_filter_signature_add("secretKey")
	 * @param string strKEY. The private key used for hashed messages sent to the server.
	*/
	public SignatureAdd(String APIKey) {
		this.APIKey = APIKey;
	}
	
	/**
	 * This is the constructor function. It creates a new instance of JSONRPC_filter_signature_add.
	 * Example: JSONRPC_filter_signature_add("secretKey")
	 * @param string strKEY. The private key used for hashed messages sent to the server.
	 * @param HashMap mapExtraURLParams. 
	 */
	public SignatureAdd(String APIKey, HashMap<String, Object> mapExtraURLParams) {
		this.APIKey = APIKey;
		this.mapExtraURLParams = mapExtraURLParams;
	}


	/**
	 * This function sets an uptime for the request.
	 */
	@Override
	public HashMap<String, Object> beforeJSONEncode(HashMap<String, Object> request)
			throws Exception {

		request.put("expires", System.currentTimeMillis() / 1000L + 86400);
	
		return request;
	}


	/**
	 * This function is used for authentication. It alters the Endpoint URL such that it contains
	 * a specific signature.
	 * 
	 * @param string strJSONRequest
	 * @param string strJSONRPCEndpointURL
	 * @param array arrHTTPHeaders
	 *
	 * @return array arrResult
	*/
	@Override
	public HashMap<String, Object> afterJSONEncode(HashMap<String, Object> mapParams) throws Exception {
		
		String strVerifyHash = hmacDigest(mapParams.get("strJSONRequest").toString(), APIKey);
		
		URL url = new URL(mapParams.get("strJSONRPCEndpointURL").toString());

		if (url.getQuery() == null) 
			url = new URL(url.toString() + "?verify=" + strVerifyHash);
		else
			url = new URL(url.toString() + "&verify=" + strVerifyHash);
		
		
		for (Entry<String, Object> objURLParam : mapExtraURLParams.entrySet()) 
			url = new URL(url.toString() + "&" + objURLParam.getKey() + "=" + objURLParam.getValue());
		
		mapParams.put("strJSONRPCEndpointURL", url.toString());
		
		return mapParams;
	}

	
	
	/**
	 * Method for the calculation of the hey
	 * @param String msg. Message to encode
	 * @param String keyString. The authentification salt
	 * @return String.
	 */
	protected static String hmacDigest(String msg, String keyString) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
		
		String algo = "HmacSHA256";
		Mac mac = null;
		
		try {
			mac = Mac.getInstance(algo);
		} catch (NoSuchAlgorithmException e1) {
			algo = "HmacMD5";
			mac = Mac.getInstance(algo);
		}

		
		String digest = null;

		SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), algo);

		mac.init(key);
		
		byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            	
		StringBuffer hash = new StringBuffer();

		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			
			if (hex.length() == 1) {	
				hash.append('0');
			}
			
			hash.append(hex);
		}

		digest = hash.toString();

	
		return digest;
	}

}
