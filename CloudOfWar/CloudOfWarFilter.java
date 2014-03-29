package CloudOfWar;


import JSONRPC.ClientFilterBase;
import JSONRPC.JSONRPC_Exception;


public class CloudOfWarFilter extends ClientFilterBase {
	
	public CloudOfWarFilter() {

	}

	
    public void exceptionCatch(JSONRPC_Exception exception) throws CloudOfWar_Exception, JSONRPC_Exception {

        if (exception.getCode()>= 0)
            throw new CloudOfWar_Exception(exception.getMessage(), exception.getCode());
        else 
            throw exception;
    }
}
