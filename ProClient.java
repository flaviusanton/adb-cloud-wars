import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import CloudOfWar.CloudOfWar;
import JSONRPC.Client;
import JSONRPC.filters.client.SignatureAdd;


public class ProClient {
	private int userId;
	private String userApiKey;
	private CloudOfWar jsonRPCClient;
	
	private int gameId = 0;
	private String color;
	private boolean flagWon;
	
	private List<Object> tiles;
	private Map<String, Object> unitStates;
	

	private static String[] directions = {"left", "forward", "right"}; 
	
	public ProClient(int userId, String userApiKey, int gameId) {
		this.userId     = userId;
		this.userApiKey = userApiKey;
		this.gameId     = gameId;
		
		try {
			jsonRPCClient = new CloudOfWar(
					"https://bsiintegration.hostway.ro/api/games/cloud-of-war/jsonrpc-2.0"
			);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
			
		try {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("user_id", userId);
			jsonRPCClient.addFilterPlugins(new SignatureAdd(this.userApiKey, params));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runGame() {
		initGame();
		
		while(true) {
			ArrayList<Map<String, Object>> moves = new ArrayList<Map<String, Object>>();
			for(Map<String, Object> plane : (List<Map<String, Object>>) unitStates.get(color)) {
				moves.add(getMove(plane));
			}
			
			try {
				unitStates = jsonRPCClient.unitStates(userId, gameId, moves);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(gameEnded())
				break;
		}
		
		endGame();
	}
	
	private void endGame() {
		if(!flagWon)
			System.out.println("You lost!\n");
		else
			System.out.println("You won!\n");
	}
	
	private boolean gameEnded() {
		List<Object> planes = (List<Object>) unitStates.get(color);
		if(planes.size() == 0)
			return true;
		
		//TODO check this
		String enemyColor = "blue";
		if(enemyColor.equals(color)) {
			enemyColor = "red";
		}
		
		List<Object> enemyPlanes = (List<Object>) unitStates.get(enemyColor);
		if(planes.size() > 0)
			return false;
		
		flagWon = true;
		
		return true;
	}
	
	private Map<String, Object> getMove(Map<String, Object> state) {
		List<String> dirs = Arrays.asList(directions);
		Collections.shuffle(dirs);
		
		Map<String, Object> move = new HashMap<String, Object>();
		move.put("unitID", state.get("ID"));
		move.put("direction", dirs.get(0));
		move.put("weapon", false);
		
		return move;
	}
	
 	private void initGame() {
		if(userId == 0) {
			try {
				Map<String, Object> m = jsonRPCClient.quickmatch(userId);
				userId = (int) m.get("userID");
				color  = (String) m.get("playerColor");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			color = "blue";
		}
		
		System.out.println("GameID: " + gameId);
		
		int noOfPlanes = 0;
		flagWon = false;
		try {
			tiles = jsonRPCClient.terrainTiles(gameId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(Object row : tiles) {
			List<Object> line = (List<Object>) row;
			noOfPlanes += Collections.frequency(line, color);
		}
		
		ArrayList<Map<String, Object>> moves = new ArrayList<Map<String, Object>>();
		for(int i = 0; i < noOfPlanes; i++) {
			Map<String, Object> move = new HashMap<String, Object>();
			move.put("unitID", i);
			moves.add(move);
		}
		
		try {
			unitStates = jsonRPCClient.unitStates(userId, gameId, moves);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
