package com.hospitalManagement.hazeify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.hospitalManagement.hazeify")
public class HazeifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(HazeifyApplication.class, args);
	}

}
