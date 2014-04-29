package cn.niven.web4app.ws;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import cn.niven.web4app.annotation.Component;

public class ServiceTypesDescriptor {

	private static final Set<Class<?>> primitiveTypes = new HashSet<Class<?>>();
	private static final Map<Class<?>, Class<?>> typeMap = new HashMap<Class<?>, Class<?>>();
	static {
		Collections.addAll(primitiveTypes, new Class<?>[] { String.class,
				Integer.class, Long.class, Float.class, Double.class,
				Boolean.class, Character.class, Byte.class, Date.class,
				Void.TYPE });
		typeMap.put(Integer.TYPE, Integer.class);
		typeMap.put(Long.TYPE, Long.class);
		typeMap.put(Float.TYPE, Float.class);
		typeMap.put(Double.TYPE, Double.class);
		typeMap.put(Boolean.TYPE, Boolean.class);
		typeMap.put(Character.TYPE, Character.class);
		typeMap.put(Byte.TYPE, Byte.class);
	}

	private final IndentedPrintWriter writer;
	private final HashMap<String, ActionItem> serviceMap;

	private final HashSet<String> usedTypeNames = new HashSet<String>();
	private final Map<Class<?>, String> typeName = new HashMap<Class<?>, String>();

	public ServiceTypesDescriptor(IndentedPrintWriter writer,
			HashMap<String, ActionItem> serviceMap) {
		this.writer = writer;
		this.serviceMap = serviceMap;

		for (Class<?> type : primitiveTypes) {
			usedTypeNames.add(type.getSimpleName());
			typeName.put(type, type.getSimpleName());
		}
	}

	private boolean addType(HashSet<Class<?>> types, Class<?> type) {
		if (!type.isAnnotationPresent(Component.class)) {
			if (type.isArray()) {
				type = type.getComponentType();
			}
			if (typeMap.containsKey(type))
				type = typeMap.get(type);
			if (!primitiveTypes.contains(type) && !types.contains(type)) {
				String name = type.getSimpleName();
				while (usedTypeNames.contains(name)) {
					name += "_";
				}
				usedTypeNames.add(name);
				typeName.put(type, name);
				types.add(type);
				return true;
			}
		}
		return false;
	}

	public String getTypeName(Class<?> type) {
		boolean isArray = false;
		if (type.isArray()) {
			isArray = true;
			type = type.getComponentType();
		}
		if (typeMap.containsKey(type))
			type = typeMap.get(type);
		String ret = typeName.get(type);
		if (isArray)
			ret += "[]";
		return ret;
	}

	public void output() {
		HashSet<Class<?>> types = new HashSet<Class<?>>();
		for (ActionItem item : serviceMap.values()) {
			addType(types, item.action.getReturnType());
			for (Class<?> paramType : item.action.getParameterTypes()) {
				addType(types, paramType);
			}
		}
		writer.indent++;
		LinkedList<Class<?>> typeList = new LinkedList<Class<?>>(types);
		Collections.sort(typeList, new Comparator<Class<?>>() {
			@Override
			public int compare(Class<?> o1, Class<?> o2) {
				return getTypeName(o1).compareTo(getTypeName(o2));
			}
		});
		while (!typeList.isEmpty()) {
			Class<?> type = typeList.removeFirst();
			writer.println("<type name=\"" + getTypeName(type) + "\">");
			writer.indent++;
			for (Field f : type.getDeclaredFields()) {
				int modifier = f.getModifiers();
				if (Modifier.isTransient(modifier)
						|| Modifier.isStatic(modifier)) {
					continue;
				}
				if (addType(types, f.getType())) {
					typeList.add(f.getType());
				}
				writer.println("<field name=\"" + f.getName() + "\" type=\""
						+ getTypeName(f.getType()) + "\"/>");
			}
			writer.indent--;
			writer.println("</type>");
		}
		writer.indent--;
	}

}
