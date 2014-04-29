package cn.niven.web4app;

import java.util.HashMap;

import org.reflections.Reflections;

import cn.niven.web4app.annotation.Component;

public class Components {

	private static final HashMap<String, Object> componentMap = new HashMap<>();

	static {
		Reflections reflect = new Reflections();

		System.out.println("Scan for components");
		for (Class<?> component : reflect
				.getTypesAnnotatedWith(Component.class)) {
			Component c = component.getAnnotation(Component.class);
			String key = c.value();
			if (componentMap.containsKey(key)) {
				System.err.println("Warning: Multiple components assigned to "
						+ key);
			} else {
				try {
					componentMap.put(key, component.newInstance());
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Object getComponent(String name) {
		return componentMap.get(name);
	}
}
