package cn.ldm.commons.utils.json;

//对jsonp的支持-jsonp包装对象
public class JsonpObject {

	private String function;// JSONP回调方法

	private Object json;// 真正的Json对象

	public JsonpObject(String function, Object json) {
		this.function = function;
		this.json = json;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public Object getJson() {
		return json;
	}

	public void setJson(Object json) {
		this.json = json;
	}
}