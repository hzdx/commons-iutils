package cn.ldm.commons.json;

import java.util.HashMap;

public class JsonResult extends HashMap<String, Object> {

	private static final long serialVersionUID = -497818973581188821L;
	private static final String STATUS = "status";
	private static final String MESSAGE = "message";

	public enum Status {
		OK, FAIL
	}

	public static final JsonResult OK = new JsonResult(Status.OK);
	public static final JsonResult FAIL = new JsonResult(Status.FAIL);

	public JsonResult(Status status) {
		super();
		super.put(STATUS, status.name().toLowerCase());
	}

	public JsonResult(Status status, String message) {
		super();
		super.put(STATUS, status.name().toLowerCase());
		super.put(MESSAGE, message);
	}

	public JsonResult put(String key, Object value) {
		if (this == JsonResult.OK || this == JsonResult.FAIL) {
			JsonResult jr = (JsonResult) this.clone();
			jr.put(key, value);
			return jr;
		} else {
			super.put(key, value);
			return this;
		}
	}

	public JsonResult message(String message) {
		return put(MESSAGE, message);
	}
	
	public static JsonResult ok(){
		return new JsonResult(Status.OK);
	}
	
	public static JsonResult fail(){
		return new JsonResult(Status.FAIL);
	}
	
	@Override
	public Object clone() {
		return super.clone();
	}
}