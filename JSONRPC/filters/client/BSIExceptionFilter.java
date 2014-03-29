package JSONRPC.filters.client;


import BSI.BSI_Exception;
import JSONRPC.ClientFilterBase;
import JSONRPC.JSONRPC_Exception;


public class BSIExceptionFilter extends ClientFilterBase {
	
	public BSIExceptionFilter() {

	}

	
    public void exceptionCatch(JSONRPC_Exception exception) throws BSI_Exception, JSONRPC_Exception {

        if (exception.getCode()>= 0)
            throw new BSI.BSI_Exception(exception.getMessage(), exception.getCode());
        else 
            throw exception;
    }
}
