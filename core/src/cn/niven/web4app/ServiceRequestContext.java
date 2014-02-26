package cn.niven.web4app;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

public class ServiceRequestContext {

	static HashMap<Long, ServiceRequestContext> contextMap = new HashMap<>();

	public static ServiceRequestContext getContext() {
		return contextMap.get(Thread.currentThread().getId());
	}

	HttpServletRequest servletRequest;
	Gson gson = new Gson();

	private HashMap<String, String> extraHeader;

	public void putExtraHeader(String key, String value) {
		if (extraHeader == null)
			extraHeader = new HashMap<>();
		extraHeader.put(key, value);
	}

	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}

	public Gson getGson() {
		return gson;
	}

	public String getHeader(String key) {
		String ret = servletRequest.getHeader(key);
		if (extraHeader != null)
			ret = extraHeader.get(key);
		return ret;
	}
}
