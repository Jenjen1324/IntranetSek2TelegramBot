package no.northcode.jens.intranetsek2tg;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;

public class TimeListener implements Job {
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("Job has been executed");
		
		
		SchedulerContext schedulerContext = null;
        try {
            schedulerContext = context.getScheduler().getContext();
        } catch (SchedulerException e1) {
            e1.printStackTrace();
        }
        
        TelegramListener listener = (TelegramListener) schedulerContext.get("telegramListener");
        
        HashMap<String, GroupData> groups = listener.getGroups();
        HashMap<Integer, UserData> users = listener.getUsers();
        
        
		for(String key : groups.keySet()) {
			Chat c = TelegramBot.getChat(key);
			listener.getIntranetHandler().sendTimetable(c, users.get(groups.get(key).user), LocalDate.now().plusDays(1));
		}
		
		
	}

	

}
