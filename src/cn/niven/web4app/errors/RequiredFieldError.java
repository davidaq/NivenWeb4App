package cn.niven.web4app.errors;

public class RequiredFieldError extends BaseError {

	private static final long serialVersionUID = 1L;

	public RequiredFieldError(String fieldName) {
		super(502, fieldName + " is not optional");
	}

}
