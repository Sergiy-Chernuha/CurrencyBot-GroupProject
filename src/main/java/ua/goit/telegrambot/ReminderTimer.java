package ua.goit.telegrambot;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.goit.userssetting.SettingUtils;

import java.util.HashMap;
import java.util.Map;

public class ReminderTimer {
   static Map<Long, Scheduler> schedulers=new HashMap<>();

    public void startTimer(String cronExpression, Long chatId) {
        try {
            if (schedulers.containsKey(chatId)) {
                schedulers.get(chatId).shutdown();
            }

            Scheduler scheduler = new StdSchedulerFactory().getScheduler();

            JobDetail job = JobBuilder.newJob(ReminderJob.class)
                    .withIdentity("reminderJob", "reminderGroup" + chatId + "")
                    .build();

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("reminderTrigger", "reminderGroup" + chatId + "")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            job.getJobDataMap().put("chatId", chatId);
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
            schedulers.put(chatId, scheduler);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void stopTimer(Long chatId) {
        try {
            if (schedulers.get(chatId) != null && !schedulers.get(chatId).isShutdown()) {
                schedulers.get(chatId).shutdown();
                schedulers.remove(chatId);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static class ReminderJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            Long chatId = (Long) context.getJobDetail().getJobDataMap().get("chatId");
            SendMessage sendMessage = new SendMessage();

            sendMessage.setChatId(chatId);
            sendMessage.setText(SettingUtils.getCurrentData(MyTelBot.getSettings().get(chatId)));
            new MyTelBot().sendNextMessage(sendMessage);
        }
    }
}
