package cn.niven.web4app.errors;

public class ArgumentMismatchError extends BaseError {

	private static final long serialVersionUID = 1L;

	public ArgumentMismatchError() {
		super(501, "Argument mismatch");
	}

}
