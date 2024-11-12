package com.example.bookingsys.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailVerificationJob implements Job {

    @Autowired
    private UserService userService;

    @Override
    public void execute(JobExecutionContext context) {
        String username = (String) context.getJobDetail().getJobDataMap().get("username");

        // Call the mock email function
        boolean isEmailSent = userService.SendVerifyEmail(username);

        if (isEmailSent) {
            System.out.println("Verification reminder sent to: " + username);
        } else {
            System.out.println("Failed to send verification reminder to: " + username);
        }
    }
}
