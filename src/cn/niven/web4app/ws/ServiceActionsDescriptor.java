package cn.niven.web4app.ws;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import cn.niven.web4app.annotation.Action;
import cn.niven.web4app.annotation.Comment;
import cn.niven.web4app.annotation.Service;

public class ServiceActionsDescriptor {
	private final IndentedPrintWriter writer;
	private final HashMap<String, ActionItem> serviceMap;
	private final ServiceTypesDescriptor typeDescriptor;

	public ServiceActionsDescriptor(IndentedPrintWriter writer,
			HashMap<String, ActionItem> serviceMap,
			ServiceTypesDescriptor typeDescriptor) {
		this.writer = writer;
		this.serviceMap = serviceMap;
		this.typeDescriptor = typeDescriptor;
	}

	public void output() {
		HashMap<Class<?>, LinkedList<ActionItem>> actionGroups = new HashMap<Class<?>, LinkedList<ActionItem>>();
		for (ActionItem item : serviceMap.values()) {
			LinkedList<ActionItem> list = actionGroups.get(item.service
					.getClass());
			if (list == null) {
				list = new LinkedList<ActionItem>();
				actionGroups.put(item.service.getClass(), list);
			}
			list.add(item);
		}
		ArrayList<Entry<Class<?>, LinkedList<ActionItem>>> entries = new ArrayList<Entry<Class<?>, LinkedList<ActionItem>>>(
				actionGroups.entrySet());
		Collections.sort(entries,
				new Comparator<Entry<Class<?>, LinkedList<ActionItem>>>() {
					@Override
					public int compare(
							Entry<Class<?>, LinkedList<ActionItem>> o1,
							Entry<Class<?>, LinkedList<ActionItem>> o2) {
						return o1.getKey().getSimpleName()
								.compareTo(o2.getKey().getSimpleName());
					}
				});
		writer.indent++;
		for (Entry<Class<?>, LinkedList<ActionItem>> entry : entries) {
			writer.println("<group name=\"" + entry.getKey().getSimpleName()
					+ "\">");
			writer.indent++;
			Comment comment = entry.getKey().getAnnotation(Comment.class);
			if (comment != null) {
				writer.println("<comment>\n<![CDATA[");
				writer.println(comment.value());
				writer.println("]]>\n</comment>");
			}
			LinkedList<ActionItem> mlist = entry.getValue();
			Collections.sort(mlist, new Comparator<ActionItem>() {
				@Override
				public int compare(ActionItem o1, ActionItem o2) {
					return o1.action.getName().compareTo(o2.action.getName());
				}
			});
			String path = entry.getKey().getAnnotation(Service.class).value();
			for (ActionItem action : mlist) {
				Method m = action.action;
				writer.println("<action path=\"" + path + "/" + m.getName()
						+ "\" name=\"" + m.getName() + "\" return=\""
						+ typeDescriptor.getTypeName(m.getReturnType()) + "\">");
				comment = m.getAnnotation(Comment.class);
				if (comment != null) {
					writer.println("<comment>\n<![CDATA[");
					writer.println(comment.value());
					writer.println("]]>\n</comment>");
				}
				String[] info = m.getAnnotation(Action.class).value();
				Class<?>[] paramType = m.getParameterTypes();
				writer.indent++;
				for (int i = 0; i < info.length; i++) {
					char c = action.parameterKeys[i].charAt(0);
					if (c == '#' || c == '@') {
						continue;
					}
					writer.print("<parameter name=\"" + action.parameterKeys[i]
							+ "\" type=\""
							+ typeDescriptor.getTypeName(paramType[i]) + "\"");
					if (null != action.defaults[i])
						writer.print(" default=\"" + action.defaults[i] + "\"");
					writer.println(" />");
				}
				writer.indent--;
				writer.println("</action>");
			}
			writer.indent--;
			writer.println("</group>");

		}
		writer.indent--;
	}
}
