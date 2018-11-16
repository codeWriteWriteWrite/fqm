package com.funstep.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.funstep.controller.TaskController;

@Component
public class AsynTask {

	@Autowired
	private TaskController taskController;
	
	@Async
	public void asynTask() {
		//System.out.println(Thread.currentThread()+","+System.currentTimeMillis());
		System.out.println("我是工作任务票号搬运工");
		taskController.getTask();
	}
}
