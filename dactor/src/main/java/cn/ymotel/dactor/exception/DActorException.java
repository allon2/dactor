package cn.ymotel.dactor.exception;

public class DActorException extends Exception {

	
	public DActorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
 	}

	public DActorException(String message, Throwable cause) {
		super(message, cause);
 	}

	public DActorException(String message) {
		super(message);
 	}
	
	private String errorCode="";
	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public DActorException(String errorCode, String message) {
		super(message);
		this.errorCode=errorCode;
 	}
	

	public DActorException(Throwable cause) {
		super(cause);
 	}
	
}
