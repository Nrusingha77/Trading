package com.BharatCrypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import  org.springframework.retry.annotation.EnableRetry;
@SpringBootApplication
@EnableRetry
public class TreadingPlateformApplication {

	public static void main(String[] args) {
		SpringApplication.run(TreadingPlateformApplication.class, args);
	}

}
