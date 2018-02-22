# SpringBootQuartz

- Quartz with Spring Boot
- 相关文章介绍：<http://blog.csdn.net/u012907049/article/details/73801122>
- 启动项目后访问：<http://127.0.0.1:8080/JobManager.html>
- 添加的时候：
	- 任务名称填写 Job 类完整包路径 + 类名，比如：com.example.demo.job.NewJob
	- 任务分组名称自己任意填写字符串。其中：**分组名+任务名称组成一个完整的唯一任务**。如果两者一样已经存在一样的，则无法添加成功，会抛异常：because one already exists with this identification
	- 表达式填写Cron表达式，比如：`0/3 * * * * ?`，在线表达：<http://cron.qqe2.com/>