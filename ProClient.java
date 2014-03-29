import java.util.*;

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

    private List<Plane> redPlanes;
    private List<Plane> bluePlanes;
    private List<Plane> planes;
    private List<Plane> friendlyPlanes;
    private List<Plane> enemyPlanes;

    private int xMax;
    private int yMax;

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

            buildPlanesVectors();

            for (Plane plane : friendlyPlanes) {
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

    private void buildPlanesVectors() {
        yMax = tiles.size();
        xMax = ((List<Object>)tiles.get(0)).size();

        redPlanes       = new ArrayList<Plane>();
        bluePlanes      = new ArrayList<Plane>();
        friendlyPlanes  = new ArrayList<Plane>();
        planes          = new ArrayList<Plane>();
        enemyPlanes     = new ArrayList<Plane>();

        for(Map<String, Object> plane : (List<Map<String, Object>>) unitStates.get("red")) {
            Plane p = new Plane();
            p.id = (Integer) plane.get("ID");
            p.tick = (Integer) plane.get("tick");
            p.x = (Integer) plane.get("tileX");
            p.y = (Integer) plane.get("tileY");
            p.ammo = (Integer) plane.get("ammunition");
            p.altitude = (Integer) plane.get("altitude");
            p.direction = (String) plane.get("direction");
            p.weaponRange = (Integer) plane.get("weaponRange");
            p.weapon = (Boolean) plane.get("weapon");
            p.color = "red";

            redPlanes.add(p);
            planes.add(p);

            if (color.equals("red"))
                friendlyPlanes.add(p);
            else
                enemyPlanes.add(p);
        }

        for(Map<String, Object> plane : (List<Map<String, Object>>) unitStates.get("blue")) {
            Plane p = new Plane();
            p.id = (Integer) plane.get("ID");
            p.tick = (Integer) plane.get("tick");
            p.x = (Integer) plane.get("tileX");
            p.y = (Integer) plane.get("tileY");
            p.ammo = (Integer) plane.get("ammunition");
            p.altitude = (Integer) plane.get("altitude");
            p.direction = (String) plane.get("direction");
            p.weaponRange = (Integer) plane.get("weaponRange");
            p.weapon = (Boolean) plane.get("weapon");
            p.color = "blue";

            bluePlanes.add(p);
            planes.add(p);

            if (color.equals("blue"))
                friendlyPlanes.add(p);
            else
                enemyPlanes.add(p);
        }
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
		if(enemyPlanes.size() > 0)
			return false;
		
		flagWon = true;
		
		return true;
	}
	
	private Map<String, Object> getMove(Plane plane) {
		List<String> dirs = Arrays.asList(directions);
		Collections.shuffle(dirs);

		Map<String, Object> move = new HashMap<String, Object>();

		move.put("unitID", plane.id);
		move.put("direction", dirs.get(0));
		move.put("weapon", false);
		
		return move;
	}
	
 	private void initGame() {
		if(userId == 0) {
			try {
				Map<String, Object> m = jsonRPCClient.quickmatch(userId);
				userId = (Integer) m.get("userID");
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
