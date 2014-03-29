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
    
    private int ATTACK_CELL = 3;

    private Map<Plane, List<Pair>> planeMoves;
    
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
            
            planeMoves = new HashMap<Plane, List<Pair>>();
            for (Plane plane : friendlyPlanes) {
                planeMoves.put(plane, getMove(plane));
            }
			
            for (Plane plane : friendlyPlanes) {
                List<Pair> thisPlaneMoves = planeMoves.get(plane);
                Collections.sort(thisPlaneMoves);
                Pair theMove = thisPlaneMoves.get(0);

                Map<String, Object> move = new HashMap<String, Object>();
                move.put("unitID", plane.id);
                move.put("direction", pairToStr(plane, theMove));
                move.put("weapon", false);
                moves.add(move);
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
	
    public List<Pair> killRange(Plane p) {
               List<Pair> result = new ArrayList<Pair>();

               boolean north = true, east = true, south = true, west = true;
               if (p.direction.equals("north")) south = false;
               if (p.direction.equals("south")) north = false;
               if (p.direction.equals("east")) west = false;
               if (p.direction.equals("west")) east = false;

               if (north) {
                       for (int i = 2; i <= p.weaponRange; i++) {
                               result.add(new Pair(p.x, p.y + i));
                       }
               }
               if (south) {
                       for (int i = 2; i <= p.weaponRange; i++) {
                               result.add(new Pair(p.x, p.y - i));
                       }
               }
               if (west) {
                       for (int i = 2; i <= p.weaponRange; i++) {
                               result.add(new Pair(p.x + i, p.y));
                       }
               }
               if (east) {
                       for (int i = 2; i <= p.weaponRange; i++) {
                               result.add(new Pair(p.x - i, p.y));
                       }
               }
               
               return result;          
    }
	
	private List<Pair> getMove(Plane plane) {
        System.out.println(plane);
        System.out.println("xMax: " + xMax + "   yMax: " + yMax);

        List<Pair> allMoves = moveRange(plane);
        List<Pair> badMoves = new ArrayList<Pair>();

        // build badMoves
        for (Plane enemy : enemyPlanes) {
        	List<Pair> m = killRange(enemy);
            for (Pair p : allMoves) {
		        if (badMoves.contains(p)) {
        		    p.priority -=  ATTACK_CELL;
        	    } else {
                	p.priority = -ATTACK_CELL;
                }
            }
            badMoves.addAll(m);
        }

        // set priority for altitude
        for (Pair p : allMoves) {
            int prior = Math.abs(10 - plane.altitude);
            if (prior > 10) prior = 1;

            if (pairToStr(plane, p).equals("forward")) {
                p.priority += prior;
            } else {
                p.priority -= prior;
            }
	    }
 
        System.out.println("All moves " + allMoves);
        return allMoves;
	}

    private String pairToStr(Plane plane, Pair pair) {
        if (plane.direction.equals("north")) {
            if (pair.y < plane.y)
                return "forward";
            if (pair.x < plane.x)
                return "left";
            return "right";
        }

        if (plane.direction.equals("south")) {
            if (pair.y > plane.y)
                return "forward";
            if (pair.x < plane.x)
                return "right";
            return "left";
        }

        if (plane.direction.equals("west")) {
            if (pair.x < plane.x)
                return "forward";
            if (pair.y < plane.y)
                return "right";
            return "left";
        }

        if (plane.direction.equals("east")) {
            if (pair.x > plane.x)
                return "forward";
            if (pair.y < plane.y)
                return "left";
            return "right";
        }

        System.out.println("*** NASOOOOOL ***");
        return "forward";
    }

    private void initGame() {
		if(gameId == 0) {
			try {
				System.out.println(gameId);
				Map<String, Object> m = jsonRPCClient.quickmatch(userId);
				gameId = (Integer) m.get("gameID");
				color  = (String) m.get("playerColor");
				System.out.println(gameId);				
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

    private Boolean isInBounds(Pair pair) {
        if (pair.x < 0 || pair.x >= xMax)
            return false;
        if (pair.y < 0 || pair.y >= yMax)
            return false;
        return true;
    }

    private List<Pair> moveRange(Plane p) {
 		ArrayList<Pair> result = new ArrayList<Pair>();
 		
 		if(p.direction.equals("north")) {
            // up
 			if(isInBounds(new Pair(p.x, p.y - 1)))
 				result.add(new Pair(p.x, p.y - 1));

            // right
 			if(isInBounds(new Pair(p.x + 1, p.y)))
 				result.add(new Pair(p.x + 1, p.y));

            // left
 			if(isInBounds(new Pair(p.x - 1, p.y)))
 				result.add(new Pair(p.x - 1, p.y));
 		}
 		
 		if(p.direction.equals("west")) {
 			if(isInBounds(new Pair(p.x, p.y + 1)))
 				result.add(new Pair(p.x, p.y + 1));

 			if(isInBounds(new Pair(p.x, p.y - 1)))
 				result.add(new Pair(p.x, p.y - 1));
 		
 			
 			if(isInBounds(new Pair(p.x - 1, p.y)))
 				result.add(new Pair(p.x - 1, p.y));
 		}
 		
 		if(p.direction.equals("east")) {
 			if(isInBounds(new Pair(p.x, p.y + 1)))
 				result.add(new Pair(p.x, p.y + 1));

 			if(isInBounds(new Pair(p.x, p.y - 1)))
 				result.add(new Pair(p.x, p.y - 1));

 			if(isInBounds(new Pair(p.x + 1, p.y)))
 				result.add(new Pair(p.x + 1, p.y));
 		}
 		
 		if(p.direction.equals("south")) {
 			if(isInBounds(new Pair(p.x, p.y + 1)))
 				result.add(new Pair(p.x, p.y + 1));

 			if(isInBounds(new Pair(p.x + 1, p.y)))
 				result.add(new Pair(p.x + 1, p.y));

 			if(isInBounds(new Pair(p.x - 1, p.y)))
 				result.add(new Pair(p.x - 1, p.y));

 		}
 		
 		return result;
 	}
}
