package cn.ldm.commons.utils;

import java.io.BufferedReader;
import java.io.FileReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;

public class XmlUtil {
	public static XStream xstream;
	static {
		xstream = new XStream();
		xstream.autodetectAnnotations(true);

		// xstream.aliasType("NotifyInfo", FileNotifyRequest.class);
		xstream.registerConverter(new DateConverter("yyyy-MM-dd'T'HH:mm:ss", new String[] { "yyyy-MM-ddTHH:mm:ss" }));

		// xstream.aliasType("NotifyResponse", FileNotifyResponse.class);
	}

	public static <T> String beanToXml(T bean) {
		return xstream.toXML(bean);
	}

	@SuppressWarnings("unchecked")
	public static <T> T xmlToBean(String xml, Class<T> classType) {
		return (T) xstream.fromXML(xml);
	}

	public static String readFile(String path) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		StringBuffer sb = new StringBuffer();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		reader.close();
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {

	}

}
