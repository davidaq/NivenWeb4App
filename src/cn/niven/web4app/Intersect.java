package cn.niven.web4app;

import java.lang.reflect.Method;

public interface Intersect {
	public boolean match(String path, Method method);

	public void before(Object... parameters);

	public Object after(Object result);
}
