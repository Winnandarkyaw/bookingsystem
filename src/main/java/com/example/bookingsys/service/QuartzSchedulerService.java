package com.example.bookingsys.service;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuartzSchedulerService {

    @Autowired
    private Scheduler scheduler;

    public void scheduleEmailVerificationJob(String username) throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("username", username);

        JobDetail jobDetail = JobBuilder.newJob(EmailVerificationJob.class)
                .setJobData(jobDataMap)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("emailVerificationTrigger")
                .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.HOUR)) // Trigger after 1 hour
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }
}
