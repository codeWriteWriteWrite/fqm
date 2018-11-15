package com.funstep.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sf.json.JSONArray;

@RestController
@RequestMapping("/task")
public class TaskController {

	@Value("${outMiddleIp}")
	private String outMiddleIp;
	
	@Value("${innerMiddleIp}")
	private String innerMiddleIp;
	
	private static int n;
	
	@RequestMapping("/getTasks")
	public Object getTask() {
		// 请求内网地址
		String urlPath = "http://"+innerMiddleIp+":8082//worksy/list";

		try {
			// 请求内容
			String content = "enterprise_id=" + URLEncoder.encode("-156", "utf-8") + "&page="
					+n+ "&limit=" +10;
			n++;
			// 调用接口，获取任务数据
			ByteArrayOutputStream baos = getHttpInfo(urlPath, content);
			System.out.println(baos.toString());

			// 请求外网数据
			urlPath = "http://"+outMiddleIp+":8082//worksy/insertToTest";
			// 向外网数据库插入数据
			insertData(urlPath, baos);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 1;
	}

	public void insertData(String urlPath, ByteArrayOutputStream baos)
			throws UnsupportedEncodingException, MalformedURLException, IOException, ProtocolException {

		// 获取json字符串
		String str = baos.toString();
		str = str.substring(str.indexOf("["), str.lastIndexOf("]") + 1);

		// json字符串转化为Map
		JSONArray jsonArray = JSONArray.fromObject(str);
		List<Map<String, Object>> mapListJson = (List) jsonArray;

		// 遍历Map
		for (int i = 0; i < mapListJson.size(); i++) {
			Map<String, Object> map = mapListJson.get(i);
			String content = getContent(map);
			// 执行插入操作
			getHttpInfo(urlPath, content);

		}
	}

	/*
	 * 获取插入外网数据库的信息
	 */
	public String getContent(Map<String, Object> map) throws UnsupportedEncodingException {

		Object rwid = map.get("id");
		String bzbmmc = (String) map.get("plandept");
		String slbzmc = (String) map.get("bz_name");
		String rwnr = (String) map.get("content");
		String rwaprmc = (String) map.get("planuser");
		String dzorxl = (String) map.get("bdz_name");
		Long starttime = (Long) map.get("starttime");
		Long endtime = (Long) map.get("endtime");
		String tdfw = (String) map.get("powercut");

		String GZDWMC = (String) map.get("工作单位");
		String PH = map.get("编号").equals(0) ? "" : ((String) map.get("编号"));
		String GZBZCY = (String) map.get("班组成员");
		String GZDDMS = (String) map.get("地点描述");
		String GZNR = (String) map.get("工作内容");
		Object VIDEO = map.get("rtmpUrl");

		String startTime = transferTime(starttime);
		String endTime = transferTime(endtime);
		
		System.out.println("rwid:" + rwid + ",bzbmmc:" + bzbmmc + ",slbzmc:" + slbzmc + ",rwnr:" + rwnr
				+ ",rwaprmc:" + rwaprmc + ",dzorxl:" + dzorxl + ",startTime:" + startTime 
				+ ",endTime:" + endTime + ",tdfw:"+ tdfw + ",GZDWMC:" + GZDWMC + ",PH:" + PH 
				+ ",GZBZCY:" + GZBZCY + ",GZDDMS:" + GZDDMS + ",GZNR:"+ GZNR + ",VIDEO:" + VIDEO);

		//需要插入数据库的信息
		String content = "rwid=" + rwid 
				           + "&bzbmmc=" + URLEncoder.encode(bzbmmc, "utf-8") 
				           + "&slbzmc="+ URLEncoder.encode(slbzmc, "utf-8") 
				           + "&rwaprmc=" + URLEncoder.encode(rwaprmc, "utf-8")
				           + "&dzorxl=" + URLEncoder.encode(dzorxl, "utf-8") 
				           + "&startTime=" + URLEncoder.encode(startTime, "utf-8")
				           + "&endTime=" + URLEncoder.encode(endTime, "utf-8") 
				           + "&tdfwc=" + URLEncoder.encode(tdfw, "utf-8")
				           + "&GZDWMC=" + URLEncoder.encode(GZDWMC, "utf-8") 
				           + "&PH=" + URLEncoder.encode(PH, "utf-8")
				           + "&GZBZCY=" + URLEncoder.encode(GZBZCY, "utf-8") 
				           + "&GZDDMS=" + URLEncoder.encode(GZDDMS, "utf-8")
				           + "&GZNR=" + URLEncoder.encode(GZNR, "utf-8") 
				           + "&VIDEO=" + URLEncoder.encode(VIDEO + "", "utf-8");
		

		return content;
	}

	/*
	 * 调用http接口获取数据
	 */
	public ByteArrayOutputStream getHttpInfo(String urlPath, String content)
			throws MalformedURLException, IOException, ProtocolException {

		byte[] bytes = content.getBytes();
		// 请求的webservice的url
		URL url = new URL(urlPath);
		// 创建http链接
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		// 设置请求的方法类型
		httpURLConnection.setRequestMethod("POST");
		// 设置请求的内容类型
		httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
		// 设置发送数据
		httpURLConnection.setDoOutput(true);
		// 设置接受数据
		httpURLConnection.setDoInput(true);

		// 发送数据,使用输出流
		OutputStream outputStream = httpURLConnection.getOutputStream();
		// 发送数据
		outputStream.write(bytes);
		// 接收数据
		InputStream inputStream = httpURLConnection.getInputStream();
		// 定义字节数组
		byte[] b = new byte[1024];
		// 定义一个输出流存储接收到的数据
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 开始接收数据
		int len = 0;
		while (true) {
			len = inputStream.read(b);
			if (len == -1) {
				// 数据读完
				break;
			}
			baos.write(b, 0, len);
		}
		return baos;
	}

	/*
	 * 将long类型的时间转换为String类型
	 */
	public String transferTime(Long time) {
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
}
