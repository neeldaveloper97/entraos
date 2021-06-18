package no.entra.entraos.api.exception;

public class AppException extends RuntimeException {

	private static final long serialVersionUID = -8999932578270387947L;
	
	
	private int status;
	
	private int code; 
		
	private String link;
	
	private String developerMessage;	
	
	/**
	 * 
	 * @param status
	 * @param code
	 * @param message
	 * @param developerMessage
	 * @param link
	 */
	public AppException(int status, int code, String message, String developerMessage, String link) {
		super(message);
		this.status = status;
		this.code = code;
		this.developerMessage = developerMessage;
		this.link = link;
	}
	
	private AppException() {}

	public int getStatus() {
		return status;
	}

	public AppException setStatus(int status) {
		this.status = status;
		return this;
	}

	public int getCode() {
		return code;
	}

	public AppException setCode(int code) {
		this.code = code;
		return this;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public AppException setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
		return this;
	}

	public String getLink() {
		return link;
	}

	public AppException setLink(String link) {
		this.link = link;
		return this;
	}
	
	public AppException addMessageParams(Object... params) {
		return new AppException(status, code, String.format(this.getMessage(), params), developerMessage, link);
	}
	
}