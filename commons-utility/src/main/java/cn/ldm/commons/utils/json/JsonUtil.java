package cn.ldm.commons.utils.json;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

	private static final ObjectMapper om = getObjectMapper();

	public static String toJson(Object obj) throws IOException {
		return om.writeValueAsString(obj);
	}

	public static <T> T fromJson(String json, Class<T> valueType) throws IOException {
		return om.readValue(json, valueType);
	}

	private static ObjectMapper getObjectMapper() {
		return new JsonMapper();
	}

}
