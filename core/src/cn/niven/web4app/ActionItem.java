package cn.niven.web4app;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import cn.niven.web4app.annotation.Component;
import cn.niven.web4app.errors.ArgumentMismatchError;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class ActionItem {
	Object service;
	Method action;
	String path;

	private int blank[];
	private Object components[];

	public ActionItem(Object service, Method action, String path,
			HashMap<String, Object> componentMap) {
		this.service = service;
		this.action = action;
		this.path = path;

		Annotation[][] annotations = action.getParameterAnnotations();
		Class<?> argTypes[] = action.getParameterTypes();
		components = new Object[argTypes.length];
		int i = 0;
		LinkedList<Integer> blankList = new LinkedList<>();
		for (Annotation[] notes : annotations) {
			for (Annotation note : notes) {
				if (note.annotationType().equals(Component.class)) {
					components[i] = componentMap.get(argTypes[i].getName());
					if (null == components[i]) {
						throw new RuntimeException("Missing component "
								+ argTypes[i].getName());
					}
				}
			}
			if (components[i] != null) {
				blankList.add(i);
			}
			i++;
		}
		if (!blankList.isEmpty()) {
			blank = new int[blankList.size()];
			i = 0;
			for (int n : blankList) {
				blank[i++] = n;
			}
		}
	}

	public Object invoke(ServiceRequestContext ctx) throws Exception {
		Class<?> argTypes[] = action.getParameterTypes();
		Object args[] = new Object[argTypes.length];
		for (int i = 0; i < argTypes.length; i++) {
			if (components[i] != null) {
				args[i] = components[i];
			} else {
			}
		}
		if (blank != null) {
			String reqType = ctx.getServletRequest().getHeader("reqtype");
			if (blank.length == 1
					&& argTypes[blank[0]].isAssignableFrom(InputStream.class)) {
				args[blank[0]] = ctx.getServletRequest().getInputStream();
			} else if ("json".equals(reqType)) {
				Gson gson = ctx.gson;
				JsonElement element = gson.toJsonTree(new InputStreamReader(ctx
						.getServletRequest().getInputStream()));
				if (element.isJsonObject()) {
					JsonObject inObj = element.getAsJsonObject();
					if (inObj.has("_header")) {
						JsonObject inHeader = inObj.get("_header")
								.getAsJsonObject();
						ctx.extraHeader = new HashMap<>();
						for (Map.Entry<String, JsonElement> entry : inHeader
								.entrySet()) {
							ctx.extraHeader.put(entry.getKey(), entry
									.getValue().getAsString());
						}
					}
					if (inObj.has("_body"))
						element = inObj.get("_body");
					else
						element = null;
				}
				if (element != null) {
					if (element.isJsonPrimitive()) {
						args[blank[0]] = gson.fromJson(element,
								argTypes[blank[0]]);
					} else if (element.isJsonArray()) {
						int i = 0;
						for (JsonElement item : element.getAsJsonArray()) {
							args[blank[i]] = gson.fromJson(item,
									argTypes[blank[i]]);
						}
					}
				}
			} else if ("plist".equals(reqType)) {

			} else if (null == reqType) {

			} else {
				throw new ArgumentMismatchError();
			}
		}
		return action.invoke(service, args);
	}
}
