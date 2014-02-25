package cn.niven.web4app;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import cn.niven.web4app.annotation.Component;

public final class ActionItem {
	Object service;
	Method action;
	String path;

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
		for (Annotation[] notes : annotations) {
			for (Annotation note : notes) {
				if (note.annotationType().equals(Component.class)) {
					components[i] = componentMap.get(argTypes[i].getName());
				}
				break;
			}
			i++;
		}
	}

	public Object invoke(HttpServletRequest request) throws Exception {
		Class<?> argTypes[] = action.getParameterTypes();
		Object args[] = new Object[argTypes.length];
		for (int i = 0; i < argTypes.length; i++) {
			if (components[i] != null) {
				args[i] = components[i];
			}
		}
		return action.invoke(service, args);
	}
}
