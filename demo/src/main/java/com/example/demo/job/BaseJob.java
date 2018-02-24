package com.example.demo.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @DisallowConcurrentExecution 注解的作用：
 * 此标记用在实现Job的类上面,意思是不允许并发执行,按照我之前的理解是 不允许调度框架在同一时刻调用Job类，后来经过测试发现并不是这样，而是Job(任务)的执行时间[比如需要10秒]大于任务的时间间隔[Interval（5秒)],
 * 那么默认情况下,调度框架为了能让 任务按照我们预定的时间间隔执行,会马上启用新的线程执行任务。否则的话会等待任务执行完毕以后 再重新执行！（这样会导致任务的执行不是按照我们预先定义的时间间隔执行）
 * 设置 @DisallowConcurrentExecution 以后程序会等任务执行完毕以后再去执行，这样就不会再启用新的线程执行
 * 资料：http://www.cnblogs.com/daxin/archive/2013/05/27/3101972.html
 */
@DisallowConcurrentExecution
public interface BaseJob extends Job{
	void execute(JobExecutionContext context) throws JobExecutionException;
}

