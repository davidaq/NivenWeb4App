package cn.niven.web4app;

import javax.servlet.http.HttpServletResponse;

public class RedirectException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String location;

	public RedirectException(String location) {
		this.location = location;
	}

	public void invoke() {
		HttpServletResponse resp = RequestContext.getContext().response;
		resp.addHeader("location", location);
		resp.setStatus(HttpServletResponse.SC_FOUND);
	}
}
