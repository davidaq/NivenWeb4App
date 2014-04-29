package cn.niven.web4app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ServerConfiguration {

	public static final ServerConfiguration config = new ServerConfiguration();

	private ServerConfiguration() {
	}

	Properties prop = new Properties();

	void loadFromFile(String filePath) throws IOException,
			IllegalArgumentException, IllegalAccessException {
		InputStream in = ServerConfiguration.class.getClassLoader()
				.getResourceAsStream(filePath);
		try {
			if (null != in) {
				prop.load(in);
			}
		} finally {
			in.close();
		}
	}

	public static String get(String key, String defaultValue) {
		return config.prop.getProperty(key, defaultValue);
	}

	public static String get(String key) {
		return get(key, null);
	}
}
