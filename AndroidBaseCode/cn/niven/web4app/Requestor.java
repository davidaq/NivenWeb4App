package cn.niven.web4app;

import java.io.IOException;


public interface Requestor {
	public void sendRequest(Request<?> request) throws IOException;
}
