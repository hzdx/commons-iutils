package cn.ldm.commons.utils.json;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public class JsonpHttpMessageConverter extends MappingJackson2HttpMessageConverter {
	@Override
	protected void writeInternal(Object object, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		if (object instanceof JsonpObject) {
			JsonpObject jsonp = (JsonpObject) object;
			OutputStream out = outputMessage.getBody();
			String text = jsonp.getFunction() + "(" + this.objectMapper.writeValueAsString(jsonp.getJson()) + ")";
			byte[] bytes = text.getBytes(outputMessage.getHeaders().getContentType().getCharSet());// 使用contentType中的编码
			out.write(bytes);
		} else {
			super.writeInternal(object, outputMessage);
		}

	}
}
