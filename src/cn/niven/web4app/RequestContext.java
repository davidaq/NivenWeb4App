package cn.niven.web4app;

import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RequestContext {

	private static HashMap<Long, RequestContext> contextMap = new HashMap<>();

	public static RequestContext getContext() {
		return contextMap.get(Thread.currentThread().getId());
	}

	public final HttpServletRequest request;
	public final HttpServletResponse response;
	public final Gson gson;
	public Session session;

	public RequestContext(HttpServletRequest request,
			HttpServletResponse response) {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Date.class, new DateGsonAdapter());
		gson = builder.create();
		contextMap.put(Thread.currentThread().getId(), this);
		this.request = request;
		this.response = response;
		session = Session.getSession();
	}

	private HashMap<String, String> extraHeader;

	public void putExtraHeader(String key, String value) {
		if (extraHeader == null)
			extraHeader = new HashMap<>();
		extraHeader.put(key, value);
	}

	public String getHeader(String key) {
		String ret = request.getHeader(key);
		if (extraHeader != null)
			ret = extraHeader.get(key);
		return ret;
	}

	public static void clearCurrentContext() {
		contextMap.remove(Thread.currentThread().getId());
	}
}
