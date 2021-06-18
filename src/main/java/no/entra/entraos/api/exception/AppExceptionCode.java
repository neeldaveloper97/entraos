package no.entra.entraos.api.exception;

public class AppExceptionCode {

	
	//COMMON
	public static AppException COMMON_INTERNALEXCEPTION_500_9999 = new AppException(500, 9999, "Internal server exception: %s", "","");
	public static AppException COMMON_BADREQUESTEXCEPTION_400_9998 = new AppException(400, 9998, "Bad request: %s", "","");
	public static AppException COMMON_IDNOTFOUNDEXCEPTION_400_9999 = new AppException(400, 9998, "Id not found: %s", "","");
	
	
	
}
