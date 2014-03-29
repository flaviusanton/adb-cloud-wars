package CloudOfWar;
/**
* Cloud of War, API v0.1 
*/
public class CloudOfWar_Exception extends Exception
{ 
	int code;
	
	public CloudOfWar_Exception(String message, int code) 
	{
		super(message + "; Exception code: " + code);

		this.code = code;
	}


	public int getCode()
	{
		return this.code;
	}



	/**
	* Game in progress.
	* 
	* Some resources are unavailable and some actions not allowed for in progress games.
	*/
	public static final int GAME_IN_PROGRESS=1;


	/**
	 * SerialVersionUID is calculated from the API version but with all the '.' removed.
	 */
	private static final long serialVersionUID = 0;
}