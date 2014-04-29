package cn.niven.web4app.annotation;

@java.lang.annotation.Target({ java.lang.annotation.ElementType.TYPE,
		java.lang.annotation.ElementType.FIELD })
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Component {
	public String value();
}
