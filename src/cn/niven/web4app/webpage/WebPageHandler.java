package cn.niven.web4app.webpage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.reflections.Reflections;

import cn.niven.web4app.Components;
import cn.niven.web4app.Invoker;
import cn.niven.web4app.RedirectException;
import cn.niven.web4app.RequestContext;
import cn.niven.web4app.S;
import cn.niven.web4app.Util;
import cn.niven.web4app.annotation.Component;
import cn.niven.web4app.annotation.WebPage;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class WebPageHandler extends AbstractHandler {

	private HashMap<String, Object> pageHandlers = new HashMap<>();
	private HashMap<String, String> pageCode = new HashMap<>();

	public WebPageHandler() throws Exception {
		Reflections reflect = new Reflections();

		System.out.println("Scan for web pages");
		for (Class<?> pageHandler : reflect
				.getTypesAnnotatedWith(WebPage.class)) {
			WebPage c = pageHandler.getAnnotation(WebPage.class);
			String key = c.value();
			if (key.charAt(0) != '/') {
				key = "/" + key;
			}
			if (pageHandlers.containsKey(key)) {
				System.err.println("Warning: Multiple page assigned to " + key);
			} else {
				Object obj = pageHandler.newInstance();
				for (Field f : pageHandler.getDeclaredFields()) {
					if (f.isAnnotationPresent(Component.class)) {
						Component component = f.getAnnotation(Component.class);
						f.setAccessible(true);
						f.set(obj, Components.getComponent(component.value()));
					}
				}
				pageHandlers.put(key, obj);
				pageCode.put(key, JSCodeMaker.makeInvoker(pageHandler, key));
			}
		}
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
		if ("/jquery.js".equals(target)) {
			baseRequest.setHandled(true);
			response.setContentType("text/javascript");
			Util.readInputStreamToWriter(new FileInputStream(new File(
					"lib/jquery.js")), response.getWriter(), true);
		} else if ("/niven.js".equals(target)) {
			baseRequest.setHandled(true);
			response.setContentType("text/javascript; charset=utf-8");
			Util.readInputStreamToWriter(new FileInputStream(new File(
					"lib/niven.js")), response.getWriter(), true);
		} else if ("/page-invoke.act".equals(target)) {
			baseRequest.setHandled(true);
			new RequestContext(request, response);
			response.setContentType("application/json");
			String action = request.getParameter("action");
			if (pageHandlers.containsKey(action)) {
				Object handler = pageHandlers.get(action);
				if (handler instanceof WebPageExt) {
					WebPageExt ext = (WebPageExt) handler;
					if (!ext.auth()) {
						response.setStatus(HttpServletResponse.SC_FORBIDDEN);
						response.getWriter().write("Access denied");
						return;
					}
				}
				String method = request.getParameter("method");
				Gson gson = new Gson();
				JsonParser parser = new JsonParser();
				JsonElement parameter = parser.parse(request
						.getParameter("parameter"));
				Method invokeTarget = null;
				Object args[] = null;
				if (parameter.isJsonArray()) {
					JsonArray params = parameter.getAsJsonArray();
					args = new Object[params.size()];
					if (params.size() == 0) {
						try {
							invokeTarget = handler.getClass().getMethod(method);
						} catch (NoSuchMethodException | SecurityException e) {
							throw new ServletException(e.getMessage());
						}
					} else {
						Class<?> argTypes[] = new Class<?>[params.size()];
						try {
							int i = 0;
							for (JsonElement ele : params) {
								JsonArray item = ele.getAsJsonArray();
								argTypes[i] = Class.forName(item.get(0)
										.getAsString());
								args[i] = gson.fromJson(item.get(1),
										argTypes[i]);
								i++;
							}
							invokeTarget = handler.getClass().getMethod(method,
									argTypes);
						} catch (ClassNotFoundException | NoSuchMethodException
								| SecurityException e) {
							e.printStackTrace();
							throw new ServletException(e.getMessage());
						}
					}
				} else if (parameter.isJsonNull()) {
					args = new Object[0];
					try {
						invokeTarget = handler.getClass().getMethod(method);
					} catch (NoSuchMethodException | SecurityException e) {
						throw new ServletException(e.getMessage());
					}
				}
				if (null != invokeTarget) {
					try {
						response.getWriter().write(
								gson.toJson(Invoker.invoke(invokeTarget,
										handler, args)));
					} catch (IllegalArgumentException e) {
						throw new ServletException(e.getCause());
					}
				}
			}
			RequestContext.clearCurrentContext();
		} else if (target.endsWith(".html")) {
			String key = target.substring(0, target.length() - 5);
			File htmlFile = new File("res/" + target);
			if (htmlFile.exists()) {
				new RequestContext(request, response);
				baseRequest.setHandled(true);
				response.setContentType("text/html; charset=utf-8");
				if (pageHandlers.containsKey(key)) {
					Object handler = pageHandlers.get(key);
					if (handler instanceof WebPageExt) {
						WebPageExt ext = (WebPageExt) handler;
						if (!ext.auth()) {
							if (new File("res/login.html").exists())
								S.redirect("/login.html");
							response.setStatus(HttpServletResponse.SC_FORBIDDEN);
							response.getWriter().write("Access denied");
							return;
						}
						ext.onBegin();
					}
				}
				Util.readInputStreamToWriter(new FileInputStream(htmlFile),
						response.getWriter(), true);
				response.getWriter()
						.write("<script type=\"text/javascript\" src=\"/jquery.js\"></script>");
				response.getWriter()
						.write("<script type=\"text/javascript\" src=\"/niven.js\"></script>");
				response.getWriter().write("<script type=\"text/javascript\">");
				if (pageCode.containsKey(key)) {
					if (pageCode.containsKey(key))
						response.getWriter().write(pageCode.get(key));
				}
				response.getWriter().write("</script>");
				if (new File("res/" + key + ".js").exists()) {
					response.getWriter().write(
							"<script type=\"text/javascript\" src=\"" + key
									+ ".js\"></script>");
				}
				RequestContext.clearCurrentContext();
			}
		}
	}
}
