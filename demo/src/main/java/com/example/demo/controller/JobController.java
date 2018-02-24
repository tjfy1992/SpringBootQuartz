package com.example.demo.controller;

import com.example.demo.entity.JobAndTrigger;
import com.example.demo.job.BaseJob;
import com.example.demo.service.IJobAndTriggerService;
import com.github.pagehelper.PageInfo;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "/job")
public class JobController {
	@Autowired
	private IJobAndTriggerService iJobAndTriggerService;

	//加入Qulifier注解，通过名称注入bean
	@Autowired
	@Qualifier("Scheduler")
	private Scheduler scheduler;

	private static Logger log = LoggerFactory.getLogger(JobController.class);

	/**
	 * 添加 job
	 *
	 * @param jobClassName
	 * @param jobGroupName
	 * @param cronExpression
	 * @throws Exception
	 */
	@PostMapping(value = "/addjob")
	public void addjob(@RequestParam(value = "jobClassName") String jobClassName,
	                   @RequestParam(value = "jobGroupName") String jobGroupName,
	                   @RequestParam(value = "cronExpression") String cronExpression) throws Exception {
		// 启动调度器  
		scheduler.start();

		//构建job信息
		JobDetail jobDetail = JobBuilder.newJob(getClass(jobClassName).getClass()).withIdentity(jobClassName, jobGroupName).build();

		//表达式调度构建器(即任务执行的时间)
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

		//按新的cronExpression表达式构建一个新的trigger
		CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName, jobGroupName)
				.withSchedule(scheduleBuilder).build();

		try {
			scheduler.scheduleJob(jobDetail, trigger);

		} catch (SchedulerException e) {
			System.out.println("创建定时任务失败" + e);
			throw new Exception("创建定时任务失败");
		}
	}


	/**
	 * 暂停 job
	 *
	 * @param jobClassName
	 * @param jobGroupName
	 * @throws Exception
	 */
	@PostMapping(value = "/pausejob")
	public void pausejob(@RequestParam(value = "jobClassName") String jobClassName, @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception {
		scheduler.pauseJob(JobKey.jobKey(jobClassName, jobGroupName));
	}


	/**
	 * 恢复 job
	 *
	 * @param jobClassName
	 * @param jobGroupName
	 * @throws Exception
	 */
	@PostMapping(value = "/resumejob")
	public void resumejob(@RequestParam(value = "jobClassName") String jobClassName, @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception {
		scheduler.resumeJob(JobKey.jobKey(jobClassName, jobGroupName));
	}

	/**
	 * 立即执行一次 job
	 *
	 * @param jobClassName
	 * @param jobGroupName
	 * @throws Exception
	 */
	@PostMapping(value = "/executeOnceJob")
	public void executeOnceJob(@RequestParam(value = "jobClassName") String jobClassName, @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception {
		scheduler.triggerJob(JobKey.jobKey(jobClassName, jobGroupName));
	}

	/**
	 * 重新设置表达式
	 *
	 * @param jobClassName
	 * @param jobGroupName
	 * @param cronExpression
	 * @throws Exception
	 */
	@PostMapping(value = "/reschedulejob")
	public void rescheduleJob(@RequestParam(value = "jobClassName") String jobClassName,
	                          @RequestParam(value = "jobGroupName") String jobGroupName,
	                          @RequestParam(value = "cronExpression") String cronExpression) throws Exception {
		try {
			TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
			// 表达式调度构建器
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

			// 按新的cronExpression表达式重新构建trigger
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

			// 按新的trigger重新设置job执行
			scheduler.rescheduleJob(triggerKey, trigger);
		} catch (SchedulerException e) {
			System.out.println("更新定时任务失败" + e);
			throw new Exception("更新定时任务失败");
		}
	}

	/**
	 * 删除 job
	 *
	 * @param jobClassName
	 * @param jobGroupName
	 * @throws Exception
	 */
	@PostMapping(value = "/deletejob")
	public void deletejob(@RequestParam(value = "jobClassName") String jobClassName, @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception {
		scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName, jobGroupName));
		scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName, jobGroupName));
		scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
	}

	/**
	 * 查询 job 列表
	 *
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping(value = "/queryjob")
	public Map<String, Object> queryjob(@RequestParam(value = "pageNum") Integer pageNum, @RequestParam(value = "pageSize") Integer pageSize) {
		PageInfo<JobAndTrigger> jobAndTrigger = iJobAndTriggerService.getJobAndTriggerDetails(pageNum, pageSize);
		Map<String, Object> map = new HashMap<>();
		map.put("JobAndTrigger", jobAndTrigger);
		map.put("number", jobAndTrigger.getTotal());
		return map;
	}

	public static BaseJob getClass(String classname) throws Exception {
		Class<?> class1 = Class.forName(classname);
		return (BaseJob) class1.newInstance();
	}


}
