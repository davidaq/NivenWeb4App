package cn.niven.web4app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import org.reflections.Reflections;

import cn.niven.web4app.annotation.Service;
import cn.niven.web4app.annotation.WebPage;
import cn.niven.web4app.errors.BaseError;

public class Invoker {
	private static LinkedList<Intersect> intersects = new LinkedList<>();
	static {
		Reflections reflect = new Reflections();
		for (Class<? extends Intersect> c : reflect
				.getSubTypesOf(Intersect.class)) {
			try {
				intersects.add(c.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

	}

	public static Object invoke(Method m, Object target, Object... args) {
		String targetPath = null;
		if (target.getClass().isAnnotationPresent(WebPage.class)) {
			WebPage p = target.getClass().getAnnotation(WebPage.class);
			targetPath = p.value();
		} else if (target.getClass().isAnnotationPresent(Service.class)) {
			Service p = target.getClass().getAnnotation(Service.class);
			targetPath = p.value();
		}
		try {
			LinkedList<Intersect> called = new LinkedList<>();
			for (Intersect i : intersects) {
				if (i.match(targetPath, m)) {
					i.before(args);
					called.addFirst(i);
				}
			}
			Object ret = m.invoke(target, args);
			for (Intersect i : called) {
				ret = i.after(ret);
			}
			return ret;
		} catch (IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			if(e.getCause() instanceof BaseError) {
				throw (BaseError) e.getCause();
			} else {
				e.printStackTrace();
			}
		}
		return null;
	}
}
