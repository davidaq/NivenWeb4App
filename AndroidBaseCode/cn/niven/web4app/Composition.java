package cn.niven.web4app;

public final class Composition {

	public static String sessionID;

	public static final Requestor requestor = new JsonRequestor();
	public static final RequestQueue requestQueue = new RequestQueue();
}
