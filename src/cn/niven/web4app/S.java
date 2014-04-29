package cn.niven.web4app;

import java.io.IOException;
import java.io.Serializable;

public final class S {
	public static void redirect(String url) {
		throw new RedirectException(url);
	}

	public static void output(String str) {
		try {
			RequestContext.getContext().response.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sessionPut(String key, Serializable value) {
		Session.set(key, value);
	}

	public static Serializable sessionGet(String key) {
		return Session.valueOf(key);
	}

	public static String sessionID() {
		return Session.sessionID();
	}
}
