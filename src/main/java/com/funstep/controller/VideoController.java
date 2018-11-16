package com.funstep.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.funstep.config.Config;
import com.funstep.config.GetConfig;

/**
 * 外部视频搬运工（外部→中间→内部）
 * @author fqmle
 *
 */

@RestController
@RequestMapping("/video")
public class VideoController {
	
	@RequestMapping("/uploadVideo")
	public String uploadVideo1(@RequestParam("file") MultipartFile videoFile,
								@RequestParam("taskId")String taskId,
								@RequestParam("deviceId")String deviceId,
								@RequestParam("videoName")String videoName,
								@RequestParam("videoTime")String videoTime,
								@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date startTime,
								@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endTime,
								HttpServletRequest req) {
		
		String innerIp="";
		try {
			innerIp = GetConfig.getConfig(Config.class).innerIp;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//内网项目的Ip地址与上传视频的接口地址
		String url="http://"+innerIp+":8082//mapping/uploadVideo";
		
		File parentFile=new File("D:"+File.separator+"upload");
		if(!parentFile.exists()){//如果文件夹不存在
			parentFile.mkdir();//创建文件夹
		}
		
		String fileOriginalName=videoFile.getOriginalFilename();
		File fileName=new File("D:"+File.separator+"upload"+File.separator+fileOriginalName);
		try {
			//将外网读取到的视频保存到中间服务器上
			videoFile.transferTo(fileName);
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
		
		
		String filePath="D:"+File.separator+"upload"+File.separator+fileOriginalName;//视频地址
		
		String bzId=req.getParameter("bzId")==null?"1":req.getParameter("bzId");//+1
		String bzjsId=req.getParameter("bzjsId")==null?"1":req.getParameter("bzjsId");
		String stageId=req.getParameter("stageId")==null?"1":req.getParameter("stageId");//+1
		String uid=req.getParameter("uid")==null?"1":req.getParameter("uid");
		System.out.println(bzId+bzjsId+stageId+uid);
		
//		String taskId="1";//+1
//		String videoTime="720000";//+1	
//		String deviceId="12";
//		String videoName="991";//+1
		

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sTime=sdf.format(startTime);   //yyyy-MM-dd HH:mm:ss +1
		String eTime=sdf.format(endTime);     //yyyy-MM-dd HH:mm:ss +1
		
		Map<String,String> map=new HashMap<String,String>();
		map.put("taskId", taskId);
		map.put("bzId", bzId);
		map.put("bzjsId", bzjsId);
		map.put("stageId",stageId);
		map.put("uid",uid);
		
		map.put("deviceId",deviceId);
		map.put("videoName", videoName);
		map.put("startTime", sTime);
		map.put("endTime", eTime);
		map.put("videoTime", videoTime);
		try {
			upload(url, filePath,map);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}

	}
	
	

	public static void upload(String url,String filePath,Map<String,String> map) throws Exception{
		
	    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
	    CloseableHttpResponse httpResponse = null;
	    RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000000).build();
	    HttpPost httpPost = new HttpPost(url);
	    httpPost.setConfig(requestConfig);
	    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
	         
	    File file = new File(filePath);
	          
	    multipartEntityBuilder.addBinaryBody("file",file);
	    
	    multipartEntityBuilder.addTextBody("taskId",map.get("taskId"));
	    multipartEntityBuilder.addTextBody("bzId",map.get("bzId"));
	    multipartEntityBuilder.addTextBody("bzjsId",map.get("bzjsId"));
	    multipartEntityBuilder.addTextBody("stageId",map.get("stageId"));
	    multipartEntityBuilder.addTextBody("uid",map.get("uid"));
	    
	    multipartEntityBuilder.addTextBody("deviceId",map.get("deviceId"));
	    multipartEntityBuilder.addTextBody("videoName",map.get("videoName"));
	    multipartEntityBuilder.addTextBody("startTime",map.get("startTime"));
	    multipartEntityBuilder.addTextBody("endTime",map.get("endTime"));
	    multipartEntityBuilder.addTextBody("videoTime",map.get("videoTime"));
	    
	    
	    HttpEntity httpEntity = multipartEntityBuilder.build();
	    httpPost.setEntity(httpEntity);
	         
	    httpResponse = httpClient.execute(httpPost);
	    HttpEntity responseEntity = httpResponse.getEntity();
	    int statusCode= httpResponse.getStatusLine().getStatusCode();
	    if(statusCode == 200){
	        BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
	        StringBuffer buffer = new StringBuffer();
	        String str = "";
	        while(!((str = reader.readLine())==null)) {
	            buffer.append(str);
	        }
	        if(file.exists()) {
	        	file.delete();
	        }    
	        System.out.println(buffer.toString());
	    }
	         
	    httpClient.close();
	    if(httpResponse!=null){
	        httpResponse.close();
	    }
	     
	}
}
