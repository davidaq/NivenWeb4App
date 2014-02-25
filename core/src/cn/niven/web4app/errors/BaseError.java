package cn.niven.web4app.errors;

public class BaseError extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private int code;
	private String message;

	public BaseError(int code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
