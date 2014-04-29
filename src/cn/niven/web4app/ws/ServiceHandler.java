package cn.niven.web4app.ws;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.reflections.Reflections;

import cn.niven.web4app.RedirectException;
import cn.niven.web4app.RequestContext;
import cn.niven.web4app.annotation.Action;
import cn.niven.web4app.annotation.Service;
import cn.niven.web4app.errors.BaseError;

public class ServiceHandler extends AbstractHandler {

	private final HashMap<String, ActionItem> serviceMap = new HashMap<>();
	private final ServiceDescriptor descriptor = new ServiceDescriptor(
			serviceMap);

	public ServiceHandler() throws Exception {

		Reflections reflect = new Reflections();

		System.out.println("Scan for services");
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
							+ action.getName());
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
		try {
			mHandle(target, baseRequest, request, response);
		} catch (RedirectException e) {
			e.invoke();
		}
	}

	public void mHandle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if ("/shutdown!".equals(target)) {
			System.exit(0);
		} else if ("/api.xml".equals(target)) {
			response.setHeader("Content-type", "text/xml;charset=utf-8");
			baseRequest.setHandled(true);
			descriptor.output(
					response.getWriter(),
					"http://" + request.getServerName() + ":"
							+ request.getServerPort());
			return;
		}
		if (serviceMap.containsKey(target)) {
			RequestContext ctx = new RequestContext(request, response);

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
			response.setContentType("text/plaintext; charset=utf-8");
			response.getWriter().print(ctx.gson.toJson(ret));
			baseRequest.setHandled(true);

			RequestContext.clearCurrentContext();
		}
	}

	public void analyze(Class<?> service) {

	}
}
