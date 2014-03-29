package CloudOfWar;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

import JSONRPC.Client;

import com.google.gson.JsonElement;

public class CloudOfWar extends Client {

	private static CloudOfWar instance = null;

	
	public CloudOfWar(String url) throws Exception {

		super(url);
	}

	
	public static CloudOfWar getInstance(String url) throws Exception {
		if(instance == null) {
			instance = new CloudOfWar(url);
		}
		
		return instance;
	}

	// 6 functions available on endpoint.


	public Map<String, Object> quickmatch(Object strUserID) throws Exception {

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(strUserID);

		return (Map<String, Object>) super._rpc("quickmatch", parameters);

	}


	public List<Object> terrainTiles(Object nGameID) throws Exception {

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(nGameID);

		return (List<Object>) super._rpc("terrainTiles", parameters);

	}


	public Map<String, Object> unitStates(Object strUserID, Object nGameID, Object arrUnitMoves) throws Exception {

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(strUserID);
		parameters.add(nGameID);
		parameters.add(arrUnitMoves);

		return (Map<String, Object>) super._rpc("unitStates", parameters);

	}


	public Map<String, Object> replay(Object nGameID) throws Exception {

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(nGameID);

		return (Map<String, Object>) super._rpc("replay", parameters);

	}


	public Map<String, Object> gameConfig() throws Exception {

		ArrayList<Object> parameters = new ArrayList<Object>();

		return (Map<String, Object>) super._rpc("gameConfig", parameters);

	}


	public List<Object> replays(Object strUserID) throws Exception {

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(strUserID);

		return (List<Object>) super._rpc("replays", parameters);

	}

HashMap<String, String> arrConstantDetails;
	HashMap<String, HashMap<String, String>> arrConstants;
	
	public void init_CloudOfWar_constants() 
	{
	}
	
	
	
	/**
	* Keep going forward.
	*/
	public static final String DIRECTION_FORWARD = "forward";
	
	
	/**
	* Change direction to left.
	*/
	public static final String DIRECTION_LEFT = "left";
	
	
	/**
	* Change direction to right.
	*/
	public static final String DIRECTION_RIGHT = "right";
	
	
	/**
	* Facing or moving towards east.
	*/
	public static final String EAST = "east";
	
	
	/**
	* Facing or moving towards north.
	*/
	public static final String NORTH = "north";
	
	
	/**
	* Blue player.
	*/
	public static final String PLAYER_BLUE = "blue";
	
	
	/**
	* Red player.
	*/
	public static final String PLAYER_RED = "red";
	
	
	/**
	* Facing or moving towards south.
	*/
	public static final String SOUTH = "south";
	
	
	/**
	* Empty terrain tile.
	*/
	public static final String TILE_EMPTY = "0";
	
	
	/**
	* Facing or moving towards west.
	*/
	public static final String WEST = "west";
	

}