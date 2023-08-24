package ua.goit.telegrambot;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.goit.userssetting.SettingUtils;

import java.util.List;

public class ReminderTimer {
    public static Logger logger = LoggerFactory.getLogger(ReminderTimer.class);
    private static Scheduler scheduler;

    public static void startTimer(String newTimer, Long chatId) {
        String cronExpression = "0 0 " + newTimer + " * * ?";
        JobKey jobKey = JobKey.jobKey("reminderJob" + newTimer, "reminderGroup" + chatId);
        checkInit();

        try {
            if (scheduler != null && !scheduler.isShutdown() && scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }

            JobDetail job = JobBuilder.newJob(ReminderJob.class)
                    .withIdentity(jobKey)
                    .build();

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("reminderTrigger" + newTimer, "reminderGroup" + chatId)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            job.getJobDataMap().put("chatId", chatId);
            scheduler.start();
            scheduler.scheduleJob(job, trigger);

        } catch (SchedulerException e) {
            logger.error("Error starting timer", e);
        }
    }

    public static void startAllTimers(List<Integer> newTimers, Long chatId) {
        for (Integer newTimer : newTimers) {
            startTimer(newTimer.toString(), chatId);
        }
    }

    private static void checkInit() {
        if (scheduler == null) {
            try {
                scheduler = new StdSchedulerFactory()
                        .getScheduler();
            } catch (SchedulerException e) {
                logger.error("Error initializing scheduler", e);
                throw new RuntimeException(e);
            }
        }
    }

    public static void stopTimer(String remoteTimer, Long chatId) {
        JobKey jobKey = JobKey.jobKey("reminderJob" + remoteTimer, "reminderGroup" + chatId);

        try {
            if (scheduler != null && !scheduler.isShutdown() && scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }else{
                logger.error("Not correct stopping of the timer: {}", remoteTimer);
            }

        } catch (SchedulerException e) {
            logger.error("Error stopping timer", e);
        }
    }

    public static void stopAllTimers(List<Integer> remoteTimers, Long chatId) {
        for (Integer remoteTimer : remoteTimers) {
            stopTimer(remoteTimer.toString(), chatId);
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
