package cn.niven.web4app.ws;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import cn.niven.web4app.Components;
import cn.niven.web4app.Invoker;
import cn.niven.web4app.RequestContext;
import cn.niven.web4app.annotation.Action;
import cn.niven.web4app.errors.RequestFormatError;
import cn.niven.web4app.errors.RequiredFieldError;
import cn.niven.web4app.value.JsonElementValue;
import cn.niven.web4app.value.StringValue;
import cn.niven.web4app.value.Value;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class ActionItem {
	Object service;
	Method action;
	String path;

	public final String parameterKeys[];
	public final String defaults[];

	public ActionItem(Object service, Method action, String path) {
		this.service = service;
		this.action = action;
		this.path = path;

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

	public void jsonRequest(RequestContext ctx, HashMap<String, Value> params)
			throws Exception {
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(new InputStreamReader(ctx.request
				.getInputStream(), "utf8"));
		if (!element.isJsonObject()) {
			throw new RequestFormatError();
		}
		JsonObject o = element.getAsJsonObject();
		if (o.has("_header")) {
			JsonElement headerElement = o.get("_header");
			if (headerElement.isJsonObject()) {
				JsonObject header = headerElement.getAsJsonObject();
				for (Map.Entry<String, JsonElement> e : header.entrySet()) {
					ctx.putExtraHeader(e.getKey(), e.getValue().getAsString());
				}
			}
		}
		if (o.has("_body")) {
			if (o.get("_body").isJsonObject())
				o = o.get("_body").getAsJsonObject();
			else
				throw new RequestFormatError();
		}
		for (Map.Entry<String, JsonElement> e : o.entrySet()) {
			params.put(e.getKey(), new JsonElementValue(e.getValue()));
		}
	}

	public void plainRequest(RequestContext ctx, HashMap<String, Value> params)
			throws Exception {
		for (Map.Entry<String, String[]> param : ctx.request.getParameterMap()
				.entrySet()) {
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

	public Object invoke(RequestContext ctx) throws Exception {
		String reqType = ctx.request.getHeader("reqtype");
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
			if ("/".equals(key)) {
				Class<?> type = argTypes[i];
				Object argVal = type.newInstance();
				for (Field f : type.getDeclaredFields()) {
					String k = f.getName();
					Value val = params.get(k);
					if (null != val) {
						f.set(argVal, val.getValueAsType(f.getType()));
					}
				}
				args[i] = argVal;
			} else if (key.length() > 0) {
				switch (key.charAt(0)) {
				case '#':
					args[i] = Components.getComponent(key.substring(1));
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
		return Invoker.invoke(action, service, args);
	}
}
