package cn.niven.web4app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;

public class Util {
	public static String readInputStream(InputStream in) throws IOException {
		return readInputStream(in, false);
	}

	public static String readInputStream(InputStream in, boolean close)
			throws IOException {
		StringBuilder strBuff = new StringBuilder();
		char buff[] = new char[2000];
		int len;
		InputStreamReader reader = new InputStreamReader(in, "utf8");
		while (0 < (len = reader.read(buff))) {
			strBuff.append(buff, 0, len);
		}
		if (close) {
			reader.close();
			in.close();
		}
		return strBuff.toString();
	}

	public static void readInputStreamToWriter(InputStream in, Writer writer,
			boolean close) throws IOException {
		char buff[] = new char[2000];
		int len;
		InputStreamReader reader = new InputStreamReader(in, "utf8");
		while (0 < (len = reader.read(buff))) {
			writer.write(buff, 0, len);
		}
		if (close) {
			reader.close();
			in.close();
		}
	}

	public static void readInputStreamToWriter(InputStream in, Writer writer)
			throws IOException {
		readInputStreamToWriter(in, writer, false);
	}

	public static void touchURL(String url) {
		try {
			URL nUrl = new URL(url);
			nUrl.getContent();
		} catch (IOException e) {
		}
	}

	public static void deleteFile(File file) {
		if (!file.exists())
			return;
		if (file.isDirectory()) {
			for (File item : file.listFiles()) {
				deleteFile(item);
			}
		}
		file.delete();
	}
}
