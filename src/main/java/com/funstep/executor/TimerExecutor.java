package com.funstep.executor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TimerExecutor {

	@Autowired
	private AsynTask asynTask;
	
	@Value("${outerIp}")
	private String outerIp;
	
	private static int n=1;
	
	//fixRate是定时时间，单位为毫秒
	@Scheduled(fixedRate=10000)
	public void timerExecute() {
		System.out.println(Thread.currentThread()+","+System.currentTimeMillis());
		//异步调用从内网将工作任务票数据插入外网相应数据库中
		asynTask.asynTask();
		//从外网上传视频到内网中
		//getVideos();
		System.out.println("搬运次数："+(++n));
	}
	
	public void getVideos() {
		
		//中间服务器调用外网的上传视频接口，
		String url="http://"+outerIp+":8082//test/getOutVideos";
		try {
			upload(url);
			
		} catch (Exception e) {
			e.printStackTrace();
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
