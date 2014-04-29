package cn.niven.web4app;

import java.io.IOException;
import java.util.Date;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class DateGsonAdapter extends TypeAdapter<Date> {
	@Override
	public Date read(JsonReader json) throws IOException {
		if (json.peek() == JsonToken.NULL) {
			json.nextNull();
			return null;
		}
		return new Date(json.nextLong());
	}

	@Override
	public void write(JsonWriter json, Date date) throws IOException {
		if (date == null) {
			json.nullValue();
		} else {
			json.value(date.getTime() / 1000);
		}
	}
}
