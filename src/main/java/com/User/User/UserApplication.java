package com.User.User;

import com.stripe.Stripe;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@SpringBootApplication

public class UserApplication {

	public static void main(String[] args) {
		Stripe.apiKey = "sk_test_51Oz1DlP2CS4Z1mfDfVDAgGXa18phBbitkIdnBHRgEnaUaUiHo16AB42aMpzijmaDMsQDGDg37VcuuFUTNzHWrUDG00f1GDUO48";
		SpringApplication.run(UserApplication.class, args);
	}
	@Bean
	public TaskScheduler taskScheduler(){
		return new ThreadPoolTaskScheduler();
	}
}
