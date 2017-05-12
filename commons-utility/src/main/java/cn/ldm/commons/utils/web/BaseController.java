package cn.ldm.commons.utils.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ldm.commons.utils.json.JsonResult;
import cn.ldm.commons.utils.json.JsonpObject;

public class BaseController {
	protected Logger log = LoggerFactory.getLogger(getClass());

//	@RequestMapping(value = "/hello")
//	@ResponseBody
//	public JsonResult hello(HttpServletRequest req) {
//		return JsonResult.OK.message("获取请求成功！");
//	}

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		CustomDateEditor dateEditor = new CustomDateEditor(fmt, true);
		binder.registerCustomEditor(Date.class, dateEditor);
		// super.initBinder(request, binder);
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public JsonResult handerException(Exception ex, HttpServletRequest request) {
		if (ex instanceof IllegalArgumentException) {
			log.error("IllegalArgumentException:{} ", ex.getMessage());
		} else {
			log.error("Exception => ", ex);
		}
		return JsonResult.FAIL.message("error:" + ex.getMessage());
	}

	protected Object getCallBackResult(JsonResult result, String callback) {
		if (callback == null) {
			return result;
		} else {
			return new JsonpObject(callback, result);
		}
	}

}
