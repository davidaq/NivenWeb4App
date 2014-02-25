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

	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}

	public Gson getGson() {
		return gson;
	}
	
	public void getHeader(String key) {
		servletRequest.getHeader(key);
	}
}
