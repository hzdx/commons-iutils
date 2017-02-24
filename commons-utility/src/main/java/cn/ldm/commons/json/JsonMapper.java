package cn.ldm.commons.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

//使用jackson作为json解析工具
public class JsonMapper extends ObjectMapper {
	
	private static final long serialVersionUID = 7013632610452349752L;

	public JsonMapper() {
		super();
		this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		this.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

		this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		this.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true);

		this.configure(MapperFeature.AUTO_DETECT_CREATORS, true);
		this.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
		this.configure(MapperFeature.AUTO_DETECT_SETTERS, true);
		this.configure(MapperFeature.AUTO_DETECT_GETTERS, true);
		
		this.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

		this.configure(MapperFeature.USE_ANNOTATIONS, true);

	}
}
