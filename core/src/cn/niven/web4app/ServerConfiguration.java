package cn.niven.web4app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

public final class ServerConfiguration {
	public int port = 8080;
	public int poolsize = 20;
	public int idletimeout = 30000;
	
	public String db_server, db_username, db_password;

	public void loadFromFile(String filePath) throws FileNotFoundException,
			IOException, IllegalArgumentException, IllegalAccessException {
		if (new File(filePath).exists()) {
			Properties prop = new Properties();
			prop.load(new FileInputStream(filePath));
			for (Field field : ServerConfiguration.class.getDeclaredFields()) {
				String key = field.getName();
				if (null != prop.get(key)) {
					String value = (String) prop.get(key);
					Object o = value;
					if(field.getType().equals(Integer.TYPE)) {
						o = Integer.valueOf(value);
					} else {
						
					}
					field.set(this, o);
				}
			}
		}
	}
}
