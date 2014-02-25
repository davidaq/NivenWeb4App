package cn.niven.web4app;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.reflections.Reflections;

import cn.niven.web4app.annotation.Action;
import cn.niven.web4app.annotation.Component;
import cn.niven.web4app.annotation.Service;
import cn.niven.web4app.errors.BaseError;

import com.google.gson.Gson;

public class ServiceHandler extends AbstractHandler {

	private final HashMap<String, ActionItem> serviceMap = new HashMap<>();
	private final HashMap<String, Object> componentMap = new HashMap<>();

	public ServiceHandler() throws Exception {
		Reflections reflect = new Reflections();

		System.out.println("Scan for components");
		for (Class<?> component : reflect
				.getTypesAnnotatedWith(Component.class)) {
			Component c = component.getAnnotation(Component.class);
			if (!c.value().isAssignableFrom(component)) {
				throw new RuntimeException(
						"Component can't be assigned to target class");
			}
			String key;
			if (c.value().equals(Object.class)) {
				key = component.getName();
			} else {
				key = c.value().getName();
			}
			if (componentMap.containsKey(key)) {
				System.err.println("Warning: Multiple components assigned to "
						+ key);
			} else {
				componentMap.put(key, component.newInstance());
			}
		}

		System.out.println("Scan for services");
		HashMap<String, ActionItem> serviceMap = new HashMap<>();
		for (Class<?> service : reflect.getTypesAnnotatedWith(Service.class)) {
			if (service.isInterface() || service.isAnnotation()) {
				continue;
			}
			String path = service.getAnnotation(Service.class).value();
			path = ("/" + path + "/").replaceAll("/+", "/");
			Object sObj = service.newInstance();
			for (Method action : service.getMethods()) {
				if (action.isAnnotationPresent(Action.class)) {
					ActionItem item = new ActionItem(sObj, action, path
							+ action.getName(), componentMap);
					serviceMap.put(item.path, item);
					System.out.println(item.path);
				}
			}
		}
		Thread.sleep(100);
	}

	@SuppressWarnings("unused")
	private static final class ReturnVO {
		public int error = 0;
		public String message;
		public Object result;
	}

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if (serviceMap.containsKey(target)) {
			long threadId = Thread.currentThread().getId();
			ServiceRequestContext ctx = new ServiceRequestContext();
			ServiceRequestContext.contextMap.put(threadId, ctx);

			ReturnVO ret = new ReturnVO();
			ActionItem item = serviceMap.get(target);
			try {
				ret.result = item.invoke(ctx);
			} catch (BaseError e) {
				ret.error = e.getCode();
				ret.message = e.getMessage();
			} catch (Exception e) {
				e.printStackTrace();
				ret.error = 500;
				ret.message = e.getMessage();
			}
			response.setContentType("text/plaintext; charset:utf-8");
			response.getWriter().print(new Gson().toJson(ret));
			baseRequest.setHandled(true);

			ServiceRequestContext.contextMap.remove(threadId);
		}
	}

	public void analyze(Class<?> service) {

	}
}
