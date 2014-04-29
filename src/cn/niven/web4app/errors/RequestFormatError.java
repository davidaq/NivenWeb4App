package cn.niven.web4app.errors;

public class RequestFormatError extends BaseError {

	private static final long serialVersionUID = 1L;

	public RequestFormatError() {
		super(503, "Request malformed!");
	}

}
