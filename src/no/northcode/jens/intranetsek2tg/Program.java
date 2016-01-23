package no.northcode.jens.intranetsek2tg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import pro.zackpollard.telegrambot.api.TelegramBot;

public class Program {

	public static void main(String[] args) throws IOException {
		String botKey = null;
		try {
			BufferedReader freader = new BufferedReader(new FileReader("botkey.txt"));
			botKey = freader.readLine();
			freader.close();
		} catch (IOException ex) {
			System.err.println("Couldn't read botkey.txt!");
			System.exit(-1);
		}
		TelegramBot tgBot = TelegramBot.login(botKey);
		
		if(tgBot == null) System.exit(-1);
		
		TelegramListener listener = new TelegramListener(tgBot);
		
		tgBot.getEventsManager().register(listener);
		tgBot.startUpdates(false);
		try {
			SchedulerFactory schedFact = new StdSchedulerFactory();
			Scheduler sched = schedFact.getScheduler();
			sched.start();
			sched.getContext().put("telegramListener", listener);
			
			JobDetail job = JobBuilder.newJob(TimeListener.class)
					.withIdentity("UpdateJob", "updates")
					.build();
			
			JobDetail job2 = JobBuilder.newJob(TimeListener.class)
					.withIdentity("testJob", "updates")
					.build();
			
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity("DailyTrigger", "updates")
					//.startAt(DateBuilder.todayAt(18, 0, 0))
					.startNow()
					.withSchedule(CronScheduleBuilder.cronSchedule("0 0 18 * * ?"))
					.build();
			
			Trigger trigger2 = TriggerBuilder.newTrigger()
					.withIdentity("testTrigger", "updates")
					.startNow()
					.withSchedule(SimpleScheduleBuilder.simpleSchedule()
							.withRepeatCount(3)
							.withIntervalInSeconds(10))
					.build();
			
			sched.scheduleJob(job, trigger);
			sched.scheduleJob(job2, trigger2);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

}
