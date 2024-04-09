package com.User.User.config;



import com.User.User.services.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configuration
@EnableScheduling
public class BillingScheduler {
    @Autowired
    private BillingService billingService;

    @Scheduled(cron = "0 02 11 * * ?")
    public void scheduleCalculateBillingCycle(){
        billingService.calculateBillingCycle();
    }
}

//@Scheduled(cron = "0 0 0 * * ?")