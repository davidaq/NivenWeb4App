package cn.niven.web4app.value;

import java.lang.reflect.Method;

public class StringValue implements Value {

	private String val;

	public StringValue(String val) {
		this.val = val;
	}

	@Override
	public Object getValueAsType(Class<?> type) {
		if (String.class.equals(type)) {
			return val;
		} else {
			Method convMethod;
			try {
				convMethod = type.getMethod("valueOf", String.class);
				return convMethod.invoke(null, val);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
