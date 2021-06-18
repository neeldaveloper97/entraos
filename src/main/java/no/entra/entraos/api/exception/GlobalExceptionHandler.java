package no.entra.entraos.api.exception;

import io.helidon.webserver.ErrorHandler;

public class GlobalExceptionHandler {
	public static ErrorHandler<Throwable> handleErrors(String errorlevel) {
        return (req, res, t) -> {
            Throwable root = t;

            while (!(root instanceof AppException) && root.getCause() != null) {
                root = root.getCause();
            }

            if (root instanceof AppException) {
            	String error_res = ExceptionConfig.handleSecurity(new ErrorMessage((AppException) root), errorlevel).toString();	
                res.status(((AppException) root).getStatus()).send(error_res);
            } else {
                req.next(t);
            }
        };
    }


}
