package cn.niven.web4app.value;

import cn.niven.web4app.RequestContext;

import com.google.gson.JsonElement;

public class JsonElementValue implements Value {
	private JsonElement element;

	public JsonElementValue(JsonElement element) {
		this.element = element;
	}

	@Override
	public Object getValueAsType(Class<?> type) {
		return RequestContext.getContext().gson.fromJson(element, type);
	}
}
