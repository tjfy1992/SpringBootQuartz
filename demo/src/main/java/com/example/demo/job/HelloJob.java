package com.example.demo.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class HelloJob implements BaseJob {  
  
    private static Logger log = LoggerFactory.getLogger(HelloJob.class);  
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {  
        log.info("Hello Job执行时间: " + new Date());  
          
    }  
}  
