package com.zynetic.ev_charger_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.zynetic.ev_charger_management")
@EnableScheduling
public class EvChargerManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvChargerManagementApplication.class, args);
	}

}
