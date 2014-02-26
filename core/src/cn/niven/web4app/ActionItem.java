package cn.niven.web4app;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import cn.niven.web4app.annotation.Action;
import cn.niven.web4app.errors.RequiredFieldError;
import cn.niven.web4app.value.StringValue;
import cn.niven.web4app.value.Value;

public final class ActionItem {
	Object service;
	Method action;
	String path;

	private String parameterKeys[];
	private String defaults[];
	private HashMap<String, Object> componentMap;

	public ActionItem(Object service, Method action, String path,
			HashMap<String, Object> componentMap) {
		this.service = service;
		this.action = action;
		this.path = path;
		this.componentMap = componentMap;

		Action actionNotes = action.getAnnotation(Action.class);
		String[] paramNotes = actionNotes.value();
		if (paramNotes.length != action.getParameterTypes().length) {
			throw new RuntimeException("Action parameter annotation mismatch: "
					+ service.getClass().getCanonicalName() + "("
					+ action.getName() + ")");
		}
		parameterKeys = new String[paramNotes.length];
		defaults = new String[parameterKeys.length];
		for (int i = 0; i < paramNotes.length; i++) {
			String note = paramNotes[i];
			int p = note.indexOf('=');
			if (p > -1) {
				parameterKeys[i] = note.substring(0, p);
				if (p + 1 < note.length()) {
					defaults[i] = note.substring(p + 1);
				} else {
					defaults[i] = "";
				}
			} else {
				parameterKeys[i] = note;
			}
			if (parameterKeys[i] == null || parameterKeys[i].length() < 1) {
				throw new RuntimeException(
						"Parameter key should not be empty: "
								+ service.getClass().getCanonicalName() + "("
								+ action.getName() + ")");
			}
		}
	}

	public void jsonRequest(ServiceRequestContext ctx,
			HashMap<String, Value> params) throws Exception {
		StringBuilder json = new StringBuilder();
		InputStream in = ctx.getServletRequest().getInputStream();
		byte buff[] = new byte[2000];
		int len;
		while (0 < (len = in.read(buff))) {
			json.append(new String(buff, 0, len, "utf-8"));
		}
//		JsonElement element = ctx.gson.toJsonTree(json.toString());
	}

	public void plainRequest(ServiceRequestContext ctx,
			HashMap<String, Value> params) throws Exception {
		for (Map.Entry<String, String[]> param : ctx.getServletRequest()
				.getParameterMap().entrySet()) {
			if (param.getValue().length > 0) {
				String key = param.getKey();
				String val = param.getValue()[0];
				if (key.charAt(0) == '@') {
					ctx.putExtraHeader(key.substring(1), val);
				} else {
					params.put(key, new StringValue(val));
				}
			}
		}
	}

	public Object invoke(ServiceRequestContext ctx) throws Exception {
		String reqType = ctx.getServletRequest().getHeader("reqtype");
		HashMap<String, Value> params = new HashMap<>();
		if ("json".equals(reqType)) {
			jsonRequest(ctx, params);
		} else if ("plist".equals(reqType)) {

		} else if (null == reqType) {
			plainRequest(ctx, params);
		}
		Class<?> argTypes[] = action.getParameterTypes();
		Object args[] = new Object[argTypes.length];
		int i = 0;
		for (String key : parameterKeys) {
			if (key.length() > 0) {
				switch (key.charAt(0)) {
				case '#':
					args[i] = componentMap.get(key.substring(1));
					break;
				case '@':
					args[i] = new StringValue(ctx.getHeader(key.substring(1)))
							.getValueAsType(argTypes[i]);
					break;
				default:
					Value val = params.get(key);
					Object argVal = null;
					if (val != null) {
						argVal = val.getValueAsType(argTypes[i]);
						if (argVal == null) {
							throw new RequiredFieldError(key);
						}
					} else if (defaults[i] != null) {
						argVal = new StringValue(defaults[i])
								.getValueAsType(argTypes[i]);
					} else {
						throw new RequiredFieldError(key);
					}
					args[i] = argVal;
					break;
				}
			}
			i++;
		}
		return action.invoke(service, args);
	}
}
