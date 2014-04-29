package cn.niven.web4app.webpage;

import java.lang.reflect.Method;

import cn.niven.web4app.annotation.Action;

public class JSCodeMaker {
	public static String makeInvoker(Class<?> type, String actionName) {
		StringBuilder builder = new StringBuilder();
		builder.append("var Server = {");
		for (Method m : type.getDeclaredMethods()) {
			if (m.isAnnotationPresent(Action.class)) {
				Class<?>[] params = m.getParameterTypes();
				builder.append(m.getName() + " : function (");
				if (params.length > 0) {
					for (int i = 0; i < params.length; i++) {
						builder.append("arg").append(i).append(",");
					}
				}
				builder.append("callback) {");
				builder.append("var send=[];");
				int i = 0;
				for (Class<?> param : params) {
					builder.append("send.push(['").append(param.getName())
							.append("', arg").append(i++).append("]);");
				}
				builder.append("var ret = new ServerInvoke('" + actionName
						+ "', '" + m.getName() + "', send).invoke();");
				builder.append("if(callback)ret.onSuccess(callback);return ret;");
				builder.append("},");
			}
		}
		builder.append("_:false};");
		return builder.toString();
	}
}
