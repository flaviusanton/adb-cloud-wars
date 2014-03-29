/**
 * Created by fanton on 29/03/14.
 */
public class Main {
	
    public static void main(String[] args) {
    	String key = "5:smTjjCH2nHxzV2BHvqPLI139KT8wvTnXgghzSZCgG9QCcrkHS9tBbwLVpUrVNPS";
    	ProClient bot = new ProClient(5, key, 391);
    	bot.runGame();
    }
}
