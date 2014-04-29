package cn.niven.web4app;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonRequestor implements Requestor {

	@Override
	public void sendRequest(Request<?> request) throws IOException {
		URL url = new URL(request.url);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try {
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(10000);
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeAdapter(Date.class, new DateGsonAdapter());
			Gson gson = builder.create();
			for (Map.Entry<String, String> header : request.headers.entrySet()) {
				connection.setRequestProperty(header.getKey(),
						header.getValue());
			}
			if (Composition.sessionID != null) {
				connection.setRequestProperty("sessionid",
						Composition.sessionID);
				connection.setRequestProperty("Cookie", "sessionid="
						+ Composition.sessionID);
			}
			if (request.rawData == null) {
				connection.setRequestProperty("Content-type",
						"application/json; charset=UTF-8");
				connection.setRequestProperty("reqtype", "json");
				JsonObject root = new JsonObject();
				for (Map.Entry<String, Object> param : request.params
						.entrySet()) {
					root.add(param.getKey(), gson.toJsonTree(param.getValue()));
				}
				String json = gson.toJson(root);
				connection.getOutputStream().write(json.getBytes("utf8"));
			} else {
				connection.setRequestProperty("Content-type",
						"application/octet-stream;");
				connection.setRequestProperty("reqtype", "raw");
				connection.getOutputStream().write(request.rawData);
			}
			connection.getOutputStream().flush();
			connection.getOutputStream().close();
			Composition.sessionID = connection.getHeaderField("sessionid");
			if (request.resultInterfaceType != null) {
				InputStream in = connection.getInputStream();
				JsonParser parser = new JsonParser();
				JsonElement resultRoot = parser
						.parse(new InputStreamReader(in));
				try {
					JsonObject resultObj = resultRoot.getAsJsonObject();
					int errorCode = resultObj.get("error").getAsInt();
					if (0 != errorCode) {
						String message = "Unknown error";
						JsonElement msg = resultObj.get("message");
						if (msg != null && msg.isJsonPrimitive()) {
							message = msg.getAsString();
						}
						request.fail(errorCode, message);
					} else {
						if (request.resultType != null) {
							JsonElement resultE = resultObj.get("result");
							if (resultE == null)
								request.success(null);
							else {
								Object result = gson.fromJson(resultE,
										request.resultType);
								request.success(result);
							}
						} else {
							request.success(null);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Result json can't be parsed");
				}
			}
		} finally {
			connection.disconnect();
		}
	}
}
