package com.funstep.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/video")
public class StartController {
	
	@Value("${outMiddleIp}")
	private String ip;
	@Value("${timeInterva}")
	private long timeInterval;
	
	@RequestMapping("/start")
	public void start() {
		
		while(true) {
			
			
			//外部项目调用中间服务器接口
			String url="http://"+ip+":8082//test/getOutVideos";
			try {
				upload(url);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(timeInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public  void upload(String url) throws Exception{
		
	    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
	    CloseableHttpResponse httpResponse = null;
	    RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000000).build();
	    HttpPost httpPost = new HttpPost(url);
	    httpPost.setConfig(requestConfig);
	    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
	    
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
	             
	        System.out.println(buffer.toString());
	    }
	         
	    httpClient.close();
	    if(httpResponse!=null){
	        httpResponse.close();
	    }
	     
	}
}
