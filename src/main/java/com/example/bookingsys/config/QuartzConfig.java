package com.example.bookingsys.config;

import com.example.bookingsys.service.EmailVerificationJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail emailVerificationJobDetail() {
        return JobBuilder.newJob(EmailVerificationJob.class)
                .withIdentity("emailVerificationJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger emailVerificationJobTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("emailVerificationTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(1) // Run every hour
                        .repeatForever())
                .forJob(emailVerificationJobDetail())
                .build();
    }
}
